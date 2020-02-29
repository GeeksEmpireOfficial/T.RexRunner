package net.geekstools.trexrunner

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.android.synthetic.main.configurations.*
import net.geeksempire.geeky.gify.Utils.UI.PopupAppShortcuts
import net.geekstools.trexrunner.GamePlay.UnityPlayerActivity
import net.geekstools.trexrunner.Util.Functions.FunctionsClassCheckpoint
import net.geekstools.trexrunner.Util.Functions.FunctionsClassUI
import net.geekstools.trexrunner.Util.Functions.PublicVariable

class EntryConfigurations : Activity() {

    lateinit var functionsClassCheckpoint: FunctionsClassCheckpoint
    lateinit var functionsClassUI: FunctionsClassUI

    lateinit private var firebaseRemoteConfig: FirebaseRemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PublicVariable.eligibleLoadShowAds = true

        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.configurations)

        functionsClassCheckpoint = FunctionsClassCheckpoint(applicationContext)
        functionsClassUI = FunctionsClassUI(applicationContext)

        playGame.setOnClickListener {
            startActivity(Intent(applicationContext, UnityPlayerActivity::class.java).apply {
                this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }

        setupAds()
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
            val builderAlertDialogue: AlertDialog.Builder = AlertDialog.Builder(this@EntryConfigurations, R.style.GeeksEmpire_Dialogue_Day)
            builderAlertDialogue.setTitle(getString(R.string.supportTitle))
            builderAlertDialogue.setIcon(getDrawable(R.drawable.draw_support))
            builderAlertDialogue.setSingleChoiceItems(contactOption, 0, null)
            builderAlertDialogue.setPositiveButton(android.R.string.ok) { dialog, whichButton ->
                when (whichButton) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        val selectedPosition = (dialog as AlertDialog).listView.checkedItemPosition
                        if (selectedPosition == 0) {
                            val textMsg = ("\n\n\n\n\n"
                                    + "[Essential Information]" + "\n"
                                    + functionsClassCheckpoint.getDeviceName() + " | " + "API " + Build.VERSION.SDK_INT + " | " + functionsClassCheckpoint.getCountryIso().toUpperCase())
                            val email = Intent(Intent.ACTION_SEND)
                            email.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support)))
                            email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_tag) + " [" + functionsClassCheckpoint.appVersionName(packageName) + "] ")
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
            builderAlertDialogue.show()
        })
    }

    override fun onResume() {
        super.onResume()

        adsCheckpoint()

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_default)
        firebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(this@EntryConfigurations, OnCompleteListener<Void> { task ->
                    if (task.isSuccessful) {
                        firebaseRemoteConfig.activate().addOnSuccessListener {

                            if (firebaseRemoteConfig.getLong(getString(R.string.integerVersionCodeNewUpdatePhone)) > functionsClassCheckpoint.appVersionCode(packageName)) {
                                functionsClassUI.notificationCreator(
                                        getString(R.string.updateAvailable),
                                        firebaseRemoteConfig.getString(getString(R.string.stringUpcomingChangeLogSummaryPhone)),
                                        firebaseRemoteConfig.getLong(getString(R.string.integerVersionCodeNewUpdatePhone)).toInt()
                                )
                            }

                            createPopupShortcutAd(firebaseRemoteConfig)
                        }
                    }
                })
    }

    override fun onPause() {
        super.onPause()
        PublicVariable.eligibleLoadShowAds = false
    }

    override fun onBackPressed() {

    }

    private fun createPopupShortcutAd(firebaseRemoteConfig: FirebaseRemoteConfig) {

        val shortcutId: String? = firebaseRemoteConfig.getString(getString(R.string.shortcutId))
        val shortcutLabel: String = firebaseRemoteConfig.getString(getString(R.string.shortcutLabel))
        val shortcutIconLink: String? = firebaseRemoteConfig.getString(getString(R.string.shortcutIconLink))
        val shortcutActionLink: String? = firebaseRemoteConfig.getString(getString(R.string.shortcutActionLink))

        shortcutId?.let {

            Glide.with(applicationContext)
                    .load(shortcutIconLink)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .apply(RequestOptions.circleCropTransform())
                    .addListener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            e?.printStackTrace()

                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {

                            val popupAppShortcuts: PopupAppShortcuts = PopupAppShortcuts(applicationContext)

                            resource?.let { icon ->

                                shortcutActionLink?.let { actionLink ->

                                    popupAppShortcuts.create(
                                            shortcutId,
                                            shortcutLabel,
                                            Icon.createWithBitmap(functionsClassUI.drawableToBitmap(icon)),
                                            actionLink
                                    )
                                }
                            }

                            return true
                        }
                    })
                    .submit()
        }
    }
}