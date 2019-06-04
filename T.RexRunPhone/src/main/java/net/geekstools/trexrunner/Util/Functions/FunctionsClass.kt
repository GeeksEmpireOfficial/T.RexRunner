package net.geekstools.trexrunner.Util.Functions

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.preference.PreferenceManager
import android.telephony.TelephonyManager
import android.text.Html
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import net.geekstools.trexrunner.BuildConfig
import net.geekstools.trexrunner.R
import java.io.FileOutputStream

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

        MobileAds.initialize(context, context.getString(R.string.ad_app_id))
        val rewardedVideoAdInstance = MobileAds.getRewardedVideoAdInstance(context)
        rewardedVideoAdInstance.setImmersiveMode(true)
        rewardedVideoAdInstance.loadAd(context.getString(R.string.ad_unit_reward), AdRequest.Builder()
                .addTestDevice("CDCAA1F20B5C9C948119E886B31681DE")
                .addTestDevice("D101234A6C1CF51023EE5815ABC285BD")
                .addTestDevice("65B5827710CBE90F4A99CE63099E524C")
                .addTestDevice("DD428143B4772EC7AA87D1E2F9DA787C")
                .addTestDevice("5901E5EE74F9B6652E05621140664A54")
                .build())
        rewardedVideoAdInstance.rewardedVideoAdListener = object : RewardedVideoAdListener {
            override fun onRewardedVideoAdLoaded() {
                context.sendBroadcast(Intent("ENABLE_REWARDED_VIDEO"))

                val intentFilter = IntentFilter()
                intentFilter.addAction("SHOW_REWARDED_VIDEO")
                val broadcastReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context, intent: Intent) {
                        if (intent.action == "SHOW_REWARDED_VIDEO") {
                            rewardedVideoAdInstance.show()
                        }
                    }
                }
                context.registerReceiver(broadcastReceiver, intentFilter)
            }

            override fun onRewardedVideoAdOpened() {

            }

            override fun onRewardedVideoStarted() {

            }

            override fun onRewardedVideoAdClosed() {
                val rewardedPromotionCode = readPreference(".NoAdsRewardedInfo", "RewardedPromotionCode", 0)
                if (rewardedPromotionCode >= 33 && readPreference(".NoAdsRewardedInfo", "Requested", false) == false) {
                    context.sendBroadcast(Intent("REWARDED_PROMOTION_CODE"))
                } else {
                    context.sendBroadcast(Intent("RELOAD_REWARDED_VIDEO"))
                    rewardedVideoAdInstance.loadAd(context.getString(R.string.ad_unit_reward), AdRequest.Builder()
                            .addTestDevice("CDCAA1F20B5C9C948119E886B31681DE")
                            .addTestDevice("D101234A6C1CF51023EE5815ABC285BD")
                            .addTestDevice("65B5827710CBE90F4A99CE63099E524C")
                            .addTestDevice("DD428143B4772EC7AA87D1E2F9DA787C")
                            .build())
                }
            }

            override fun onRewarded(rewardItem: RewardItem) {
                val rewardedPromotionCode = readPreference(".NoAdsRewardedInfo", "RewardedPromotionCode", 0)
                savePreference(".NoAdsRewardedInfo", "RewardedPromotionCode", rewardedPromotionCode + rewardItem.amount)

                savePreference(".NoAdsRewardedInfo", "RewardedTime", System.currentTimeMillis())
                savePreference(".NoAdsRewardedInfo", "RewardedAmount", rewardItem.amount)
                saveFile(".NoAdsRewarded", context.packageName)
            }

            override fun onRewardedVideoAdLeftApplication() {}

            override fun onRewardedVideoAdFailedToLoad(failedCode: Int) {
                if (BuildConfig.DEBUG) {
                    println("Ad Failed $failedCode")
                }
                rewardedVideoAdInstance.loadAd(context.getString(R.string.ad_unit_reward), AdRequest.Builder()
                        .addTestDevice("CDCAA1F20B5C9C948119E886B31681DE")
                        .addTestDevice("D101234A6C1CF51023EE5815ABC285BD")
                        .addTestDevice("65B5827710CBE90F4A99CE63099E524C")
                        .addTestDevice("DD428143B4772EC7AA87D1E2F9DA787C")
                        .build())
            }

            override fun onRewardedVideoCompleted() {}
        }
    }

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