import android.content.SharedPreferences

data class ProfileData(
    val fio: String = "",
    val age: String = "",
    val gender: String = "",
    val height: String = "",
    val weight: String = "",
    val activity: String = "",
    val goal: String = "",
    val autoCalc: Boolean = true,
    val dailyLimit: String = ""
) {
    fun saveToPrefs(prefs: SharedPreferences) {
        prefs.edit().apply {
            putString("fio", fio)
            putString("age", age)
            putString("gender", gender)
            putString("height", height)
            putString("weight", weight)
            putString("activity", activity)
            putString("goal", goal)
            putBoolean("autoCalc", autoCalc)
            putString("dailyLimit", dailyLimit)
        }.apply()
    }

    companion object {
        fun fromPrefs(prefs: SharedPreferences): ProfileData {
            return ProfileData(
                fio = prefs.getString("fio", "") ?: "",
                age = prefs.getString("age", "") ?: "",
                gender = prefs.getString("gender", "") ?: "",
                height = prefs.getString("height", "") ?: "",
                weight = prefs.getString("weight", "") ?: "",
                activity = prefs.getString("activity", "") ?: "",
                goal = prefs.getString("goal", "") ?: "",
                autoCalc = prefs.getBoolean("autoCalc", true),
                dailyLimit = prefs.getString("dailyLimit", "") ?: ""
            )
        }
    }
}