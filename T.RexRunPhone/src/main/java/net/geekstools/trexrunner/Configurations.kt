package net.geekstools.trexrunner

import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.WindowManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.android.synthetic.main.configurations.*
import net.geekstools.trexrunner.Util.Functions.FunctionsClass
import net.geekstools.trexrunner.Util.Functions.PublicVariable

class Configurations : Activity() {

    lateinit var functionsClass: FunctionsClass

    lateinit private var firebaseRemoteConfig: FirebaseRemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PublicVariable.eligibleLoadShowAds = true

        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.configurations)

        functionsClass = FunctionsClass(this@Configurations, applicationContext)

        playGame.setOnClickListener {
            startActivity(Intent(applicationContext, UnityPlayerActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction("ENABLE_REWARDED_VIDEO")
        intentFilter.addAction("RELOAD_REWARDED_VIDEO")
        intentFilter.addAction("REWARDED_PROMOTION_CODE")
        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == "ENABLE_REWARDED_VIDEO") {
                    val rewardedPromotionCode = functionsClass.readPreference(".NoAdsRewardedInfo", "RewardedPromotionCode", 0)
                    if ((rewardedPromotionCode >= 33)
                            && functionsClass.readPreference(".NoAdsRewardedInfo", "Requested", false) == true) {

                        rewardVideo.text = Html.fromHtml("<br/><font color='#f2f7ff'>" +
                                "<big>Please Click to See Video Ads to<br/>" +
                                "\uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E <b>Support Geeks Empire Open Source Projects</b> \uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E </big><br/>"
                                + "</font>")
                    } else {
                        rewardVideo.text = Html.fromHtml("<br/><font color='#f2f7ff'>" +
                                "<big>Click to See Video Ads to Get<br/>" +
                                "\uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E <b>Promotion Codes of Geeks Empire Premium Apps</b> \uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E </big><br/>"
                                + "</font>")
                        rewardVideo.append("$rewardedPromotionCode / 33" + Html.fromHtml("<br/>"))
                    }
                    rewardVideo.setVisibility(View.VISIBLE)
                } else if (intent.action == "RELOAD_REWARDED_VIDEO") {
                    rewardVideo.setVisibility(View.INVISIBLE)
                } else if (intent.action == "REWARDED_PROMOTION_CODE") {
                    rewardVideo.setTextColor(getColor(R.color.light))
                    rewardVideo.text = Html.fromHtml("<br/><font color='#f2f7ff'>" +
                            "<big>Upgrade to Geeks Empire Premium Apps<br/>" +
                            "\uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E <b>Rewarded to Get Promotion Codes</b> \uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E </big>"
                            + "\uD83D\uDCE9 Click Here to Request \uD83D\uDCE9<br/>"
                            + "</font>")
                    rewardVideo.setVisibility(View.VISIBLE)
                }
            }
        }
        registerReceiver(broadcastReceiver, intentFilter)

        rewardVideo.setOnClickListener {
            if ((functionsClass.readPreference(".NoAdsRewardedInfo", "RewardedPromotionCode", 0) >= 33)
                    && functionsClass.readPreference(".NoAdsRewardedInfo", "Requested", false) == false) {
                try {
                    val textMsg = ("\n\n\n\n\n"
                            + "[Essential Information]" + "\n"
                            + getString(R.string.app_name) + " | " + functionsClass.appVersionName(getPackageName()) + "\n"
                            + functionsClass.getDeviceName() + " | " + "API " + Build.VERSION.SDK_INT + " | " + functionsClass.getCountryIso().toUpperCase())
                    val email = Intent(Intent.ACTION_SEND)
                    email.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support)))
                    email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.rewardedPromotionCodeTitle))
                    email.putExtra(Intent.EXTRA_TEXT, textMsg)
                    email.type = "text/*"
                    email.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    email.setPackage("com.google.android.gm")
                    startActivity(Intent.createChooser(email, getString(R.string.rewardedPromotionCodeTitle)))

                    functionsClass.savePreference(".NoAdsRewardedInfo", "Requested", true)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                sendBroadcast(Intent("SHOW_REWARDED_VIDEO"))
            }
        }
    }

    override fun onStart() {
        super.onStart()
        supportView.setOnClickListener(View.OnClickListener {
            val contactOption = arrayOf(
                    "Send an Email",
                    "Contact via Forum",
                    "Rate & Write Review",
                    "Floating Shortcuts",
                    "Super Shortcuts",
                    "Pin a Pic on Map"
            )
            var builder: AlertDialog.Builder = AlertDialog.Builder(this@Configurations, R.style.GeeksEmpire_Dialogue_Day)
            builder.setTitle(getString(R.string.supportTitle))
            builder.setIcon(getDrawable(R.drawable.draw_support))
            builder.setSingleChoiceItems(contactOption, 0, null)
            builder.setPositiveButton(android.R.string.ok) { dialog, whichButton ->
                when (whichButton) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        val selectedPosition = (dialog as AlertDialog).listView.checkedItemPosition
                        if (selectedPosition == 0) {
                            val textMsg = ("\n\n\n\n\n"
                                    + "[Essential Information]" + "\n"
                                    + functionsClass.getDeviceName() + " | " + "API " + Build.VERSION.SDK_INT + " | " + functionsClass.getCountryIso().toUpperCase())
                            val email = Intent(Intent.ACTION_SEND)
                            email.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support)))
                            email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_tag) + " [" + functionsClass.appVersionName(packageName) + "] ")
                            email.putExtra(Intent.EXTRA_TEXT, textMsg)
                            email.type = "message/*"
                            email.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(Intent.createChooser(email, getString(R.string.feedback_tag)))
                        } else if (selectedPosition == 1) {
                            val r = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_xda)))
                            startActivity(r)
                        } else if (selectedPosition == 2) {
                            val r = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_play_store)))
                            startActivity(r)
                        } else if (selectedPosition == 3) {
                            val r = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_floating_shortcuts)))
                            startActivity(r)
                        } else if (selectedPosition == 4) {
                            val r = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_super_shortcuts)))
                            startActivity(r)
                        } else if (selectedPosition == 5) {
                            val r = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_pin_pic)))
                            startActivity(r)
                        }
                    }
                }
            }
            builder.show()
        })
    }

    override fun onResume() {
        super.onResume()
        PublicVariable.eligibleLoadShowAds = true

        val rewardedPromotionCode = functionsClass.readPreference(".NoAdsRewardedInfo", "RewardedPromotionCode", 0)
        if (rewardedPromotionCode >= 33 && functionsClass.readPreference(".NoAdsRewardedInfo", "Requested", false) == true) {
            rewardVideo.visibility = View.INVISIBLE
        } else if (rewardedPromotionCode >= 33 && functionsClass.readPreference(".NoAdsRewardedInfo", "Requested", false) == false) {
            rewardVideo.setTextColor(getColor(R.color.light))
            rewardVideo.text = Html.fromHtml("<br/><font color='#f2f7ff'>" +
                    "<big>Upgrade to Geeks Empire Premium Apps<br/>" +
                    "\uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E <b>Rewarded to Get Promotion Codes</b> \uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E </big>"
                    + "\uD83D\uDCE9 Click Here to Request \uD83D\uDCE9<br/>"
                    + "</font>")
            rewardVideo.setVisibility(View.VISIBLE)
        }

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_default)
        firebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(this@Configurations, OnCompleteListener<Void> { task ->
                    if (task.isSuccessful) {
                        firebaseRemoteConfig.activate().addOnSuccessListener {
                            if (firebaseRemoteConfig.getLong(getString(R.string.integerVersionCodeNewUpdatePhone)) > functionsClass.appVersionCode(packageName)) {
                                functionsClass.notificationCreator(
                                        getString(R.string.updateAvailable),
                                        firebaseRemoteConfig.getString(getString(R.string.stringUpcomingChangeLogSummaryPhone)),
                                        firebaseRemoteConfig.getLong(getString(R.string.integerVersionCodeNewUpdatePhone)).toInt()
                                )
                            } else {
                            }
                        }
                    } else {

                    }
                })
    }

    override fun onPause() {
        super.onPause()
        PublicVariable.eligibleLoadShowAds = false
    }

    override fun onBackPressed() {

    }
}