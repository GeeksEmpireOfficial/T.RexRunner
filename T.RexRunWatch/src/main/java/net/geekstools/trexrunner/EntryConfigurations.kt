package net.geekstools.trexrunner

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.support.wearable.activity.WearableActivity
import android.support.wearable.view.ConfirmationOverlay
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.wearable.intent.RemoteIntent
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.android.synthetic.main.configurations.*
import net.geekstools.trexrunner.GamePlay.UnityPlayerActivity
import net.geekstools.trexrunner.Util.Functions.FunctionsClassCheckpoint
import net.geekstools.trexrunner.Util.Functions.FunctionsClassUI

class EntryConfigurations : WearableActivity() {

    lateinit var functionsClassCheckpoint: FunctionsClassCheckpoint
    lateinit var functionsClassUI: FunctionsClassUI

    lateinit var firebaseRemoteConfig: FirebaseRemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.configurations)
        FirebaseApp.initializeApp(applicationContext);

        functionsClassCheckpoint = FunctionsClassCheckpoint(applicationContext)
        functionsClassUI = FunctionsClassUI(applicationContext)

        playGame.setOnClickListener {
            startActivity(Intent(applicationContext, UnityPlayerActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }

        setAmbientEnabled()
    }

    override fun onStart() {
        super.onStart()

        val resultReceiver = object : ResultReceiver(Handler()) {
            override protected fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                if (resultCode == RemoteIntent.RESULT_OK) {
                    println("RemoteIntent.RESULT_OK")
                    if (functionsClassCheckpoint.isFirstTimeOpen() || firebaseRemoteConfig.getBoolean(getString(R.string.booleanShowPlayStoreLinkDialogue))) {
                        ConfirmationOverlay()
                                .setType(ConfirmationOverlay.SUCCESS_ANIMATION)
                                .setMessage(firebaseRemoteConfig.getString(getString(R.string.stringPlayStoreLinkDialogue)))
                                .setDuration(1500 * 1)
                                .showOn(this@EntryConfigurations)

                        functionsClassCheckpoint.savePreference(".UserState", "FirstTime", false)
                    }
                } else if (resultCode == RemoteIntent.RESULT_FAILED) {
                    println("RemoteIntent.RESULT_FAILED")
                } else {
                    println("Unexpected Result $resultCode")
                }
            }
        }

        reward.setOnClickListener {
            val intentPlayStore = Intent(Intent.ACTION_VIEW)
                    .addCategory(Intent.CATEGORY_BROWSABLE)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .setData(Uri.parse(getString(R.string.play_store_link) + packageName))
            RemoteIntent.startRemoteActivity(
                    applicationContext,
                    intentPlayStore,
                    object : ResultReceiver(Handler()) {
                        override protected fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                            if (resultCode == RemoteIntent.RESULT_OK) {
                                println("RemoteIntent.RESULT_OK")
                                if (functionsClassCheckpoint.isFirstTimeOpen() || firebaseRemoteConfig.getBoolean(getString(R.string.booleanShowPlayStoreLinkDialogue))) {
                                    ConfirmationOverlay()
                                            .setType(ConfirmationOverlay.OPEN_ON_PHONE_ANIMATION)
                                            .setMessage(getString(R.string.playOnPhone))
                                            .setDuration((1000 * 1.9).toInt())
                                            .showOn(this@EntryConfigurations)
                                }
                            } else if (resultCode == RemoteIntent.RESULT_FAILED) {
                                println("RemoteIntent.RESULT_FAILED")
                            } else {
                                println("Unexpected Result $resultCode")
                            }
                        }
                    })
        }

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_default)
        firebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(this@EntryConfigurations, OnCompleteListener<Void> { task ->
                    if (task.isSuccessful) {
                        firebaseRemoteConfig.activate().addOnSuccessListener {

                            if (firebaseRemoteConfig.getLong(getString(R.string.integerVersionCodeNewUpdatePhone)) > functionsClassCheckpoint.appVersionCode(packageName)) {
                                Toast.makeText(applicationContext, getString(R.string.updateAvailable), Toast.LENGTH_LONG).show()

                                functionsClassUI.notificationCreator(
                                        getString(R.string.updateAvailable),
                                        firebaseRemoteConfig.getString(getString(R.string.stringUpcomingChangeLogSummaryPhone)),
                                        firebaseRemoteConfig.getLong(getString(R.string.integerVersionCodeNewUpdatePhone)).toInt()
                                )
                            }

                            if (functionsClassCheckpoint.isFirstTimeOpen() || firebaseRemoteConfig.getBoolean(getString(R.string.booleanPlayStoreLink))) {
                                val intentPlayStore = Intent(Intent.ACTION_VIEW)
                                        .addCategory(Intent.CATEGORY_BROWSABLE)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        .setData(Uri.parse(firebaseRemoteConfig.getString(getString(R.string.stringPlayStoreLink))))
                                RemoteIntent.startRemoteActivity(
                                        applicationContext,
                                        intentPlayStore,
                                        resultReceiver)
                            }
                        }

                        createPopupShortcutAd(firebaseRemoteConfig)
                    }
                })
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

                            resource?.let { icon ->

                                shortcutActionLink?.let { actionLink ->
                                    runOnUiThread {
                                        adShortcut.contentDescription = shortcutLabel
                                        adShortcut.setImageDrawable(icon)

                                        adShortcut.setOnClickListener {
                                            Intent(Intent.ACTION_VIEW).apply {
                                                data = Uri.parse(actionLink)

                                                startActivity(this)
                                            }
                                        }
                                    }
                                }
                            }

                            return true
                        }
                    })
                    .submit()
        }
    }
}