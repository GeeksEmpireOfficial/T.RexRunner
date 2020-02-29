package net.geekstools.trexrunner.Util.Functions

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.telephony.TelephonyManager

class FunctionsClassCheckpoint(var context: Context) {

    var API: Int = Build.VERSION.SDK_INT

    /*System Checkpoint*/
    public fun getDeviceName(): String {
        var manufacturer: String = Build.MANUFACTURER
        var model: String = Build.MODEL
        if (model.startsWith(manufacturer)) {
            return capitalizeFirstChar(manufacturer)
        } else {
            return capitalizeFirstChar(manufacturer) + " " + model
        }
    }

    private fun capitalizeFirstChar(someText: String): String {
        if (someText == null || someText.length == 0) {
            return ""
        }
        val first = someText.get(0)
        return if (Character.isUpperCase(first)) {
            return someText
        } else {
            return Character.toUpperCase(first) + someText.substring(1)
        }
    }

    public fun getCountryIso(): String {
        var countryISO = "Undefined"
        try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            countryISO = telephonyManager.simCountryIso
        } catch (e: Exception) {
            countryISO = "Undefined"
        }
        return countryISO
    }

    public fun returnAPI(): Int {
        return API
    }

    public fun displayX(): Int {
        return context.resources.displayMetrics.widthPixels
    }

    public fun displayY(): Int {
        return context.resources.displayMetrics.heightPixels
    }

    public fun appVersion(packageName: String): String {
        var Version = "0"

        try {
            val packInfo = context.packageManager.getPackageInfo(packageName, 0)
            Version = packInfo.versionName
        } catch (e: Exception) {
        }

        return Version
    }

    public fun appVersionCode(packageName: String): Int {
        var VersionCode = 0
        try {
            val packInfo = context.packageManager.getPackageInfo(packageName, 0)
            VersionCode = packInfo.versionCode
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return VersionCode
    }

    @Throws(Exception::class)
    fun networkConnection(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    /*Checkpoint*/
    fun isFirstTimeOpen(): Boolean {

        return readPreference(".UserState", "FirstTime", true)
    }

    /*Files*/
    fun savePreference(PreferenceName: String, KEY: String, VALUE: Boolean) {
        val sharedPreferences = context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE)
        val editorSharedPreferences = sharedPreferences.edit()
        editorSharedPreferences.putBoolean(KEY, VALUE)
        editorSharedPreferences.apply()
    }

    fun readPreference(PreferenceName: String, KEY: String, defaultVALUE: Boolean): Boolean {
        return context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE).getBoolean(KEY, defaultVALUE)
    }
}