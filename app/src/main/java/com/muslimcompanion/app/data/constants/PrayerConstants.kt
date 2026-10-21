package com.muslimcompanion.app.data.constants

import com.muslimcompanion.app.data.model.CityOption

object PrayerConstants {
    val POPULAR_CITIES = listOf(
        // Bangladesh Divisions & Major Districts
        CityOption("ঢাকা", "ঢাকা", "বাংলাদেশ", 23.8103, 90.4125, "Asia/Dhaka"),
        CityOption("চট্টগ্রাম", "চট্টগ্রাম", "বাংলাদেশ", 22.3569, 91.7832, "Asia/Dhaka"),
        CityOption("সিলেট", "সিলেট", "বাংলাদেশ", 24.8949, 91.8687, "Asia/Dhaka"),
        CityOption("রাজশাহী", "রাজশাহী", "বাংলাদেশ", 24.3636, 88.6241, "Asia/Dhaka"),
        CityOption("খুলনা", "খুলনা", "বাংলাদেশ", 22.8456, 89.5403, "Asia/Dhaka"),
        CityOption("বরিশাল", "বরিশাল", "বাংলাদেশ", 22.7010, 90.3535, "Asia/Dhaka"),
        CityOption("রংপুর", "রংপুর", "বাংলাদেশ", 25.7439, 89.2752, "Asia/Dhaka"),
        CityOption("ময়মনসিংহ", "ময়মনসিংহ", "বাংলাদেশ", 24.7471, 90.4203, "Asia/Dhaka"),
        CityOption("কুমিল্লা", "কুমিল্লা", "বাংলাদেশ", 23.4682, 91.1788, "Asia/Dhaka"),
        CityOption("কক্সবাজার", "কক্সবাজার", "বাংলাদেশ", 21.4272, 92.0058, "Asia/Dhaka"),
        CityOption("বগুড়া", "বগুড়া", "বাংলাদেশ", 24.8465, 89.3777, "Asia/Dhaka"),
        CityOption("নারায়ণগঞ্জ", "নারায়ণগঞ্জ", "বাংলাদেশ", 23.6238, 90.5000, "Asia/Dhaka"),
        CityOption("গাজীপুর", "গাজীপুর", "বাংলাদেশ", 23.9999, 90.4203, "Asia/Dhaka"),
        CityOption("নোয়াখালী", "নোয়াখালী", "বাংলাদেশ", 22.8696, 91.0995, "Asia/Dhaka"),
        CityOption("যশোর", "যশোর", "বাংলাদেশ", 23.1664, 89.2081, "Asia/Dhaka"),
        CityOption("পাবনা", "পাবনা", "বাংলাদেশ", 24.0064, 89.2372, "Asia/Dhaka"),
        CityOption("ফরিদপুর", "ফরিদপুর", "বাংলাদেশ", 23.6071, 89.8429, "Asia/Dhaka"),
        CityOption("দিনাজপুর", "দিনাজপুর", "বাংলাদেশ", 25.6217, 88.6355, "Asia/Dhaka"),
        CityOption("কুষ্টিয়া", "কুষ্টিয়া", "বাংলাদেশ", 23.9013, 89.1205, "Asia/Dhaka"),
        CityOption("টাঙ্গাইল", "টাঙ্গাইল", "বাংলাদেশ", 24.2513, 89.9167, "Asia/Dhaka"),
        CityOption("ব্রাহ্মণবাড়িয়া", "ব্রাহ্মণবাড়িয়া", "বাংলাদেশ", 23.9571, 91.1119, "Asia/Dhaka"),
        CityOption("জামালপুর", "জামালপুর", "বাংলাদেশ", 24.9375, 89.9378, "Asia/Dhaka"),
        CityOption("ফেনী", "ফেনী", "বাংলাদেশ", 23.0159, 91.3976, "Asia/Dhaka"),
        // Holy & International Cities
        CityOption("মক্কা মুকাররমা", "মক্কা মুকাররমা", "সৌদি আরব", 21.4225, 39.8262, "Asia/Riyadh"),
        CityOption("মদিনা মুনাওয়ারা", "মদিনা মুনাওয়ারা", "সৌদি আরব", 24.4672, 39.6111, "Asia/Riyadh"),
        CityOption("দুবাই", "দুবাই", "সংযুক্ত আরব আমিরাত", 25.2048, 55.2708, "Asia/Dubai"),
        CityOption("রিয়াদ", "রিয়াদ", "সৌদি আরব", 24.7136, 46.6753, "Asia/Riyadh"),
        CityOption("কুয়েত সিটি", "কুয়েত সিটি", "কুয়েত", 29.3759, 47.9774, "Asia/Kuwait"),
        CityOption("দোহা", "দোহা", "কাতার", 25.2854, 51.5310, "Asia/Qatar"),
        CityOption("কুয়ালালামপুর", "কুয়ালালামপুর", "মালয়েশিয়া", 3.1390, 101.6869, "Asia/Kuala_Lumpur"),
        CityOption("লন্ডন", "লন্ডন", "যুক্তরাজ্য", 51.5074, -0.1278, "Europe/London"),
        CityOption("নিউইয়র্ক", "নিউইয়র্ক", "যুক্তরাষ্ট্র", 40.7128, -74.0060, "America/New_York")
    )

    fun toBengaliDigits(input: Any): String {
        val bnDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
        val str = input.toString()
        val sb = StringBuilder()
        for (ch in str) {
            if (ch in '0'..'9') {
                sb.append(bnDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }
}
