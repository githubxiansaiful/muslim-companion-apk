package com.muslimcompanion.app.data.repository

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.core.content.ContextCompat
import com.muslimcompanion.app.data.constants.PrayerConstants
import com.muslimcompanion.app.data.model.CityOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import java.util.TimeZone
import kotlin.coroutines.resume
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object LocationHelper {

    fun hasLocationPermission(context: Context): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocation || coarseLocation
    }

    fun isLocationServiceEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return false
        val isGpsEnabled = try {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            false
        }
        val isNetworkEnabled = try {
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (e: Exception) {
            false
        }
        return isGpsEnabled || isNetworkEnabled
    }

    @SuppressLint("MissingPermission")
    suspend fun getBestCurrentLocation(context: Context): Location? {
        if (!hasLocationPermission(context)) return null
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null

        // 1. Try to get best last known location first (fast)
        var bestLastLocation: Location? = null
        val providers = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER,
            LocationManager.PASSIVE_PROVIDER
        )

        for (provider in providers) {
            try {
                if (locationManager.isProviderEnabled(provider)) {
                    val loc = locationManager.getLastKnownLocation(provider)
                    if (loc != null) {
                        if (bestLastLocation == null || loc.time > bestLastLocation.time) {
                            bestLastLocation = loc
                        }
                    }
                }
            } catch (e: Exception) {
                // provider might be disabled or security exception
            }
        }

        // If last known location is fresh (within 15 minutes), return it immediately
        val fifteenMinutesAgo = System.currentTimeMillis() - (15 * 60 * 1000)
        if (bestLastLocation != null && bestLastLocation.time > fifteenMinutesAgo) {
            return bestLastLocation
        }

        // 2. Request a fresh single location fix with a 6-second timeout
        val freshLocation = withTimeoutOrNull(6500L) {
            suspendCancellableCoroutine<Location?> { continuation ->
                val listener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        try {
                            locationManager.removeUpdates(this)
                        } catch (e: Exception) {}
                        if (continuation.isActive) {
                            continuation.resume(location)
                        }
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {}
                }

                var registered = false
                val availableProviders = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
                for (provider in availableProviders) {
                    try {
                        if (locationManager.isProviderEnabled(provider)) {
                            locationManager.requestLocationUpdates(
                                provider,
                                0L,
                                0f,
                                listener,
                                Looper.getMainLooper()
                            )
                            registered = true
                        }
                    } catch (e: Exception) {
                        // ignore and try next
                    }
                }

                if (!registered) {
                    if (continuation.isActive) continuation.resume(null)
                }

                continuation.invokeOnCancellation {
                    try {
                        locationManager.removeUpdates(listener)
                    } catch (e: Exception) {}
                }
            }
        }

        return freshLocation ?: bestLastLocation
    }

    suspend fun resolveCityOption(context: Context, location: Location): CityOption {
        return withContext(Dispatchers.IO) {
            val lat = location.latitude
            val lng = location.longitude
            var rawCityName: String? = null
            var rawCountryName: String? = null

            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val addressList = suspendCancellableCoroutine<List<android.location.Address>?> { cont ->
                        geocoder.getFromLocation(lat, lng, 1) { addresses ->
                            cont.resume(addresses)
                        }
                    }
                    if (!addressList.isNullOrEmpty()) {
                        val addr = addressList[0]
                        rawCityName = addr.locality ?: addr.subAdminArea ?: addr.adminArea ?: addr.featureName
                        rawCountryName = addr.countryName
                    }
                } else {
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(lat, lng, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val addr = addresses[0]
                        rawCityName = addr.locality ?: addr.subAdminArea ?: addr.adminArea ?: addr.featureName
                        rawCountryName = addr.countryName
                    }
                }
            } catch (e: Exception) {
                // Geocoder network error or offline fallback
            }

            // Find nearest known city in Bangladesh or international list for distance matching
            val nearestKnownCity = findNearestKnownCity(lat, lng)

            val finalCityName: String
            val finalNameBn: String
            val finalCountry: String

            if (!rawCityName.isNullOrBlank()) {
                val mappedBn = mapToBengaliCityName(rawCityName)
                finalNameBn = mappedBn
                finalCityName = rawCityName
                finalCountry = rawCountryName ?: if (isInsideBangladesh(lat, lng)) "বাংলাদেশ" else "অবস্থান"
            } else if (nearestKnownCity != null && nearestKnownCity.second < 60.0) {
                // Within 60 km of a recognized city
                val matched = nearestKnownCity.first
                finalNameBn = "${matched.nameBn} (GPS)"
                finalCityName = "${matched.name} (GPS)"
                finalCountry = matched.country
            } else {
                finalNameBn = "বর্তমান অবস্থান (GPS)"
                finalCityName = "Current Location (GPS)"
                finalCountry = if (isInsideBangladesh(lat, lng)) "বাংলাদেশ" else "আমার অবস্থান"
            }

            val tzId = try {
                TimeZone.getDefault().id
            } catch (e: Exception) {
                "Asia/Dhaka"
            }

            CityOption(
                name = finalCityName,
                nameBn = finalNameBn,
                country = finalCountry,
                latitude = lat,
                longitude = lng,
                timezone = tzId
            )
        }
    }

    private fun isInsideBangladesh(lat: Double, lng: Double): Boolean {
        return lat in 20.5..26.8 && lng in 88.0..92.8
    }

    private fun findNearestKnownCity(lat: Double, lng: Double): Pair<CityOption, Double>? {
        var minDistance = Double.MAX_VALUE
        var closestCity: CityOption? = null

        for (city in PrayerConstants.POPULAR_CITIES) {
            val dist = calculateHaversineKm(lat, lng, city.latitude, city.longitude)
            if (dist < minDistance) {
                minDistance = dist
                closestCity = city
            }
        }

        return if (closestCity != null) Pair(closestCity, minDistance) else null
    }

    private fun calculateHaversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth's radius in kilometers
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    private fun mapToBengaliCityName(name: String): String {
        val clean = name.trim().lowercase()
        return when {
            clean.contains("dhaka") -> "ঢাকা"
            clean.contains("chattogram") || clean.contains("chittagong") -> "চট্টগ্রাম"
            clean.contains("sylhet") -> "সিলেট"
            clean.contains("rajshahi") -> "রাজশাহী"
            clean.contains("khulna") -> "খুলনা"
            clean.contains("barishal") || clean.contains("barisal") -> "বরিশাল"
            clean.contains("rangpur") -> "রংপুর"
            clean.contains("mymensingh") -> "ময়মনসিংহ"
            clean.contains("cumilla") || clean.contains("comilla") -> "কুমিল্লা"
            clean.contains("cox") -> "কক্সবাজার"
            clean.contains("bogura") || clean.contains("bogra") -> "বগুড়া"
            clean.contains("narayanganj") -> "নারায়ণগঞ্জ"
            clean.contains("gazipur") -> "গাজীপুর"
            clean.contains("noakhali") -> "নোয়াখালী"
            clean.contains("jessore") || clean.contains("jashore") -> "যশোর"
            clean.contains("pabna") -> "পাবনা"
            clean.contains("faridpur") -> "ফরিদপুর"
            clean.contains("dinajpur") -> "দিনাজপুর"
            clean.contains("kushtia") -> "কুষ্টিয়া"
            clean.contains("tangail") -> "টাঙ্গাইল"
            clean.contains("brahmanbaria") -> "ব্রাহ্মণবাড়িয়া"
            clean.contains("jamalpur") -> "জামালপুর"
            clean.contains("feni") -> "ফেনী"
            clean.contains("mecca") || clean.contains("makkah") -> "মক্কা মুকাররমা"
            clean.contains("medina") || clean.contains("madinah") -> "মদিনা মুনাওয়ারা"
            clean.contains("dubai") -> "দুবাই"
            clean.contains("riyadh") -> "রিয়াদ"
            clean.contains("kuwait") -> "কুয়েত সিটি"
            clean.contains("doha") -> "দোহা"
            clean.contains("kuala lumpur") -> "কুয়ালালামপুর"
            clean.contains("london") -> "লন্ডন"
            clean.contains("new york") -> "নিউইয়র্ক"
            else -> name
        }
    }
}
