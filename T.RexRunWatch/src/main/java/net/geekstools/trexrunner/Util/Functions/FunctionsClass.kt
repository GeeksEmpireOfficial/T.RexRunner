package net.geekstools.trexrunner.Util.Functions

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.telephony.TelephonyManager
import android.text.Html
import net.geekstools.trexrunner.R

class FunctionsClass {

    lateinit var activity: Activity
    lateinit public var context: Context

    var API: Int


    init {
        API = Build.VERSION.SDK_INT
    }

    constructor(activityInit: Activity, contextInit: Context) {
        this.activity = activityInit
        this.context = contextInit
    }

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

    /*Notification*/
    public fun notificationCreator(titleText: String, contentText: String, notificationId: Int) {
        try {
            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationBuilder: Notification.Builder = Notification.Builder(context)
            notificationBuilder.setContentTitle(Html.fromHtml("<b><font color='" + context.getColor(R.color.default_color_darker) + "'>" + titleText + "</font></b>"))
            notificationBuilder.setContentText(Html.fromHtml("<font color='" + context.getColor(R.color.default_color_light) + "'>" + contentText + "</font>"))
            notificationBuilder.setTicker(context.resources.getString(R.string.app_name))
            notificationBuilder.setSmallIcon(R.drawable.ic_notification)
            notificationBuilder.setAutoCancel(true)
            notificationBuilder.setColor(context.getColor(R.color.default_color))
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH)

            val newUpdate: Intent = Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.play_store_link) + context.getPackageName()))
            val newUpdatePendingIntent: PendingIntent = PendingIntent.getActivity(context, 1, newUpdate, PendingIntent.FLAG_UPDATE_CURRENT)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel: NotificationChannel = NotificationChannel(context.packageName, context.getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(notificationChannel)
                notificationBuilder.setChannelId(context.packageName)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val builderActionNotification = Notification.Action.Builder(
                        Icon.createWithResource(context, R.drawable.draw_share_menu),
                        context.getString(R.string.rate),
                        newUpdatePendingIntent
                )
                notificationBuilder.addAction(builderActionNotification.build())
            }
            notificationBuilder.setContentIntent(newUpdatePendingIntent)
            notificationManager.notify(notificationId, notificationBuilder.build())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}