package com.muslimcompanion.app.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.muslimcompanion.app.data.constants.QuranConstants
import com.muslimcompanion.app.data.model.AyahItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStreamReader
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

object QuranRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private val cache = ConcurrentHashMap<Int, List<AyahItem>>()
    private val gson = Gson()

    suspend fun getSurahAyahs(context: Context? = null, surahNumber: Int): List<AyahItem> = withContext(Dispatchers.IO) {
        val cached = cache[surahNumber]
        if (cached != null && cached.isNotEmpty()) {
            return@withContext cached
        }

        // 1. Instant Offline Local Asset Load
        if (context != null) {
            try {
                val assetPath = "quran/surah_$surahNumber.json"
                context.assets.open(assetPath).use { inputStream ->
                    InputStreamReader(inputStream, Charsets.UTF_8).use { reader ->
                        val itemType = object : TypeToken<List<AyahItem>>() {}.type
                        val list: List<AyahItem>? = gson.fromJson(reader, itemType)
                        if (!list.isNullOrEmpty()) {
                            cache[surahNumber] = list
                            return@withContext list
                        }
                    }
                }
            } catch (e: Exception) {
                // Fallback to network if asset load failed
            }
        }

        // 2. Network Fallback
        val url = "https://api.alquran.cloud/v1/surah/$surahNumber/editions/quran-uthmani,bn.bengali,en.transliteration"
        val request = Request.Builder().url(url).build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext emptyList()
                val bodyStr = response.body?.string() ?: return@withContext emptyList()
                val json = JsonParser.parseString(bodyStr).asJsonObject

                if (!json.has("data")) return@withContext emptyList()
                val dataArray = json.getAsJsonArray("data")
                if (dataArray.size() < 2) return@withContext emptyList()

                val arEdition = dataArray.get(0).asJsonObject.getAsJsonArray("ayahs")
                val bnEdition = dataArray.get(1).asJsonObject.getAsJsonArray("ayahs")
                val trEdition = if (dataArray.size() > 2) {
                    dataArray.get(2).asJsonObject.getAsJsonArray("ayahs")
                } else null

                val result = mutableListOf<AyahItem>()
                for (i in 0 until arEdition.size()) {
                    val arObj = arEdition.get(i).asJsonObject
                    val bnObj = if (i < bnEdition.size()) bnEdition.get(i).asJsonObject else null
                    val trObj = if (trEdition != null && i < trEdition.size()) trEdition.get(i).asJsonObject else null

                    val globalNumber = arObj.get("number").asInt
                    val numberInSurah = arObj.get("numberInSurah").asInt
                    val arabicText = arObj.get("text").asString
                    val bnText = bnObj?.get("text")?.asString ?: ""
                    val trText = trObj?.get("text")?.asString ?: ""

                    val bnPronunciation = QuranConstants.getPronunciation(surahNumber, numberInSurah, trText)
                    val audioUrl = "https://cdn.islamic.network/quran/audio/128/ar.alafasy/$globalNumber.mp3"

                    result.add(
                        AyahItem(
                            number = globalNumber,
                            numberInSurah = numberInSurah,
                            text = arabicText,
                            pronunciationBn = bnPronunciation,
                            translationBn = bnText,
                            audioUrl = audioUrl
                        )
                    )
                }

                cache[surahNumber] = result
                result
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
