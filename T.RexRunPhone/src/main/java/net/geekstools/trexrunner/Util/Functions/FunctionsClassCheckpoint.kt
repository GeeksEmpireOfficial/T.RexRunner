package net.geekstools.trexrunner.Util.Functions

import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import androidx.preference.PreferenceManager
import java.io.FileOutputStream

class FunctionsClassCheckpoint(var context: Context) {

    var API: Int = Build.VERSION.SDK_INT

    /*Files*/
    fun saveFile(fileName: String, fileContent: String) {
        try {
            val fileOutputStream: FileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            fileOutputStream.write((fileContent).toByteArray())

            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun savePreference(PreferenceName: String, KEY: String, VALUE: String?) {
        val sharedPreferences = context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE)
        val editorSharedPreferences = sharedPreferences.edit()
        editorSharedPreferences.putString(KEY, VALUE)
        editorSharedPreferences.apply()
    }

    fun savePreference(PreferenceName: String, KEY: String, VALUE: Int) {
        val sharedPreferences = context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE)
        val editorSharedPreferences = sharedPreferences.edit()
        editorSharedPreferences.putInt(KEY, VALUE)
        editorSharedPreferences.apply()
    }

    fun savePreference(PreferenceName: String, KEY: String, VALUE: Long) {
        val sharedPreferences = context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE)
        val editorSharedPreferences = sharedPreferences.edit()
        editorSharedPreferences.putLong(KEY, VALUE)
        editorSharedPreferences.apply()
    }

    fun savePreference(PreferenceName: String, KEY: String, VALUE: Boolean) {
        val sharedPreferences = context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE)
        val editorSharedPreferences = sharedPreferences.edit()
        editorSharedPreferences.putBoolean(KEY, VALUE)
        editorSharedPreferences.apply()
    }

    fun saveDefaultPreference(KEY: String, VALUE: Int) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editorSharedPreferences = sharedPreferences.edit()
        editorSharedPreferences.putInt(KEY, VALUE)
        editorSharedPreferences.apply()
    }

    fun readPreference(PreferenceName: String, KEY: String, defaultVALUE: String?): String? {
        return context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE).getString(KEY, defaultVALUE)
    }

    fun readPreference(PreferenceName: String, KEY: String, defaultVALUE: Int): Int {
        return context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE).getInt(KEY, defaultVALUE)
    }

    fun readPreference(PreferenceName: String, KEY: String, defaultVALUE: Long): Long {
        return context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE).getLong(KEY, defaultVALUE)
    }

    fun readPreference(PreferenceName: String, KEY: String, defaultVALUE: Boolean): Boolean {
        return context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE).getBoolean(KEY, defaultVALUE)
    }

    fun readDefaultPreference(KEY: String, defaultVALUE: Int): Int {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(KEY, defaultVALUE)
    }

    fun readDefaultPreference(KEY: String, defaultVALUE: String): String? {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(KEY, defaultVALUE)
    }

    fun readDefaultPreference(KEY: String, defaultVALUE: Boolean): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEY, defaultVALUE)
    }

    /*System Checkpoint*/
    public fun getDeviceName(): String {
        val manufacturer: String = Build.MANUFACTURER
        val model: String = Build.MODEL
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

    public fun appVersionName(packageName: String): String {
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
}