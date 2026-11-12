package com.example.data

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String
) {
    ENGLISH("en", "English", "English (Default)"),
    HINDI("hi", "Hindi", "हिन्दी (Hindi)"),
    BENGALI("bn", "Bengali", "বাংলা (Bengali)"),
    MARATHI("mr", "Marathi", "मराठी (Marathi)"),
    TAMIL("ta", "Tamil", "தமிழ் (Tamil)"),
    TELUGU("te", "Telugu", "తెలుగు (Telugu)"),
    GUJARATI("gu", "Gujarati", "ગુજરાતી (Gujarati)");

    companion object {
        fun fromCode(code: String): AppLanguage {
            return entries.firstOrNull { it.code == code } ?: ENGLISH
        }
    }
}

data class LocalizedStrings(
    val totalSpent: String,
    val todaysExpenses: String,
    val viewAll: String,
    val home: String,
    val analytics: String,
    val settings: String,
    val thisMonth: String,
    val lastMonth: String,
    val thisYear: String,
    val downloadPdfReport: String,
    val languageTitle: String,
    val languageDesc: String,
    val dataBackupTitle: String,
    val dataBackupDesc: String,
    val clearAllDataTitle: String,
    val clearAllDataDesc: String,
    val food: String,
    val travel: String,
    val groceries: String,
    val others: String
)

fun getLocalizedStrings(language: AppLanguage): LocalizedStrings {
    return when (language) {
        AppLanguage.ENGLISH -> LocalizedStrings(
            totalSpent = "Total Spent",
            todaysExpenses = "Today’s Expenses",
            viewAll = "View All",
            home = "Home",
            analytics = "Analytics",
            settings = "Settings",
            thisMonth = "This Month",
            lastMonth = "Last Month",
            thisYear = "This Year",
            downloadPdfReport = "Download PDF Report",
            languageTitle = "Language",
            languageDesc = "Choose your language",
            dataBackupTitle = "Data Backup",
            dataBackupDesc = "Backup your data safely",
            clearAllDataTitle = "Clear All Data",
            clearAllDataDesc = "Delete all your data",
            food = "Food",
            travel = "Travel",
            groceries = "Groceries",
            others = "Others"
        )
        AppLanguage.HINDI -> LocalizedStrings(
            totalSpent = "कुल खर्च (Total Spent)",
            todaysExpenses = "आज के खर्च (Today’s Expenses)",
            viewAll = "सभी देखें",
            home = "होम",
            analytics = "विश्लेषण",
            settings = "सेटिंग्स",
            thisMonth = "इस महीने",
            lastMonth = "पिछले महीने",
            thisYear = "इस वर्ष",
            downloadPdfReport = "PDF रिपोर्ट डाउनलोड करें",
            languageTitle = "भाषा (Language)",
            languageDesc = "अपनी भाषा चुनें",
            dataBackupTitle = "डेटा बैकअप (Data Backup)",
            dataBackupDesc = "अपना डेटा सुरक्षित रखें",
            clearAllDataTitle = "सारा डेटा हटाएं (Clear All Data)",
            clearAllDataDesc = "अपना सारा डेटा डिलीट करें",
            food = "भोजन (Food)",
            travel = "यात्रा (Travel)",
            groceries = "किराना (Groceries)",
            others = "अन्य (Others)"
        )
        else -> LocalizedStrings(
            totalSpent = "Total Spent",
            todaysExpenses = "Today’s Expenses",
            viewAll = "View All",
            home = "Home",
            analytics = "Analytics",
            settings = "Settings",
            thisMonth = "This Month",
            lastMonth = "Last Month",
            thisYear = "This Year",
            downloadPdfReport = "Download PDF Report",
            languageTitle = "Language (${language.displayName})",
            languageDesc = "Choose your language",
            dataBackupTitle = "Data Backup",
            dataBackupDesc = "Backup your data safely",
            clearAllDataTitle = "Clear All Data",
            clearAllDataDesc = "Delete all your data",
            food = "Food",
            travel = "Travel",
            groceries = "Groceries",
            others = "Others"
        )
    }
}
