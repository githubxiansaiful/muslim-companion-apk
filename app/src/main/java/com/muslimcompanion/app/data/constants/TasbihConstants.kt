package com.muslimcompanion.app.data.constants

import com.muslimcompanion.app.data.model.PresetTasbih

object TasbihConstants {
    val PRESETS = listOf(
        PresetTasbih(
            arabic = "سُبْحَانَ اللَّهِ",
            nameBn = "সুবহানাল্লাহ",
            pronunciationBn = "সুবহানাল্লাহ",
            meaningBn = "আল্লাহ পরম পবিত্র ও সকল ত্রুটি হতে মুক্ত।",
            defaultTarget = 33
        ),
        PresetTasbih(
            arabic = "الْحَمْدُ لِلَّهِ",
            nameBn = "আলহামদুলিল্লাহ",
            pronunciationBn = "আলহামদুলিল্লাহ",
            meaningBn = "সমস্ত প্রশংসা একমাত্র আল্লাহর জন্য।",
            defaultTarget = 33
        ),
        PresetTasbih(
            arabic = "اللَّهُ أَكْبَرُ",
            nameBn = "আল্লাহু আকবার",
            pronunciationBn = "আল্লাহু আকবার",
            meaningBn = "আল্লাহ সর্বশ্রেষ্ঠ ও মহান।",
            defaultTarget = 34
        ),
        PresetTasbih(
            arabic = "لَا إِلٰهَ إِلَّا اللَّهُ",
            nameBn = "লা ইলাহা ইল্লাল্লাহ",
            pronunciationBn = "লা ইলাহা ইল্লাল্লাহ",
            meaningBn = "আল্লাহ ছাড়া সত্য কোনো উপাস্য নেই।",
            defaultTarget = 100
        ),
        PresetTasbih(
            arabic = "أَسْتَغْفِرُ اللَّهَ",
            nameBn = "আস্তাগফিরুল্লাহ",
            pronunciationBn = "আস্তাগফিরুল্লাহ",
            meaningBn = "আমি আল্লাহর নিকট ক্ষমা প্রার্থনা করছি।",
            defaultTarget = 100
        ),
        PresetTasbih(
            arabic = "اللَّهُمَّ صَلِّ عَلَىٰ مُحَمَّدٍ",
            nameBn = "দরূদ শরীফ",
            pronunciationBn = "আল্লাহুম্মা সাল্লি আলা মুহাম্মাদ",
            meaningBn = "হে আল্লাহ! মুহাম্মাদ ﷺ এর প্রতি রহমত বর্ষণ করুন।",
            defaultTarget = 100
        ),
        PresetTasbih(
            arabic = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
            nameBn = "সুবহানাল্লাহি ওয়া বিহামদিহি",
            pronunciationBn = "সুবহানাল্লাহি ওয়া বিহামদিহি",
            meaningBn = "আল্লাহ পবিত্র এবং সমস্ত প্রশংসা কেবল তাঁরই জন্য।",
            defaultTarget = 100
        )
    )
}
