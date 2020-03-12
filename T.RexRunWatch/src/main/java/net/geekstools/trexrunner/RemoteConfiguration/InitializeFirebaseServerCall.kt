package net.geekstools.trexrunner.RemoteConfiguration

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.widget.Toast
import androidx.wear.widget.ConfirmationOverlay
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.wearable.intent.RemoteIntent
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.android.synthetic.main.configurations.*
import net.geekstools.trexrunner.EntryConfigurations
import net.geekstools.trexrunner.R
import net.geekstools.trexrunner.Util.Functions.FunctionsClassCheckpoint
import net.geekstools.trexrunner.Util.Functions.FunctionsClassUI

class InitializeFirebaseServerCall(private var entryConfigurations: EntryConfigurations) {

    private val functionsClassCheckpoint = FunctionsClassCheckpoint(entryConfigurations)
    private val functionsClassUI = FunctionsClassUI(entryConfigurations)

    fun retrieveFirebaseRemoteConfiguration(): FirebaseRemoteConfig {

        val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_default)
        firebaseRemoteConfig.fetch(0)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        firebaseRemoteConfig.activate().addOnSuccessListener {

                            if (firebaseRemoteConfig.getLong(entryConfigurations.getString(R.string.integerVersionCodeNewUpdatePhone)) > functionsClassCheckpoint.appVersionCode(entryConfigurations.packageName)) {
                                Toast.makeText(entryConfigurations, entryConfigurations.getString(R.string.updateAvailable), Toast.LENGTH_LONG).show()

                                functionsClassUI.notificationCreator(
                                        entryConfigurations.getString(R.string.updateAvailable),
                                        firebaseRemoteConfig.getString(entryConfigurations.getString(R.string.stringUpcomingChangeLogSummaryPhone)),
                                        firebaseRemoteConfig.getLong(entryConfigurations.getString(R.string.integerVersionCodeNewUpdatePhone)).toInt()
                                )
                            }

                            if (functionsClassCheckpoint.isFirstTimeOpen() || firebaseRemoteConfig.getBoolean(entryConfigurations.getString(R.string.booleanPlayStoreLink))) {

                                val intentPlayStore = Intent(Intent.ACTION_VIEW)
                                        .addCategory(Intent.CATEGORY_BROWSABLE)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        .setData(Uri.parse(firebaseRemoteConfig.getString(entryConfigurations.getString(R.string.stringPlayStoreLink))))

                                RemoteIntent.startRemoteActivity(
                                        entryConfigurations,
                                        intentPlayStore,
                                        object : ResultReceiver(Handler()) {
                                            override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                                                if (resultCode == RemoteIntent.RESULT_OK) {

                                                    if (functionsClassCheckpoint.isFirstTimeOpen() || firebaseRemoteConfig.getBoolean(entryConfigurations.getString(R.string.booleanShowPlayStoreLinkDialogue))) {

                                                        ConfirmationOverlay()
                                                                .setType(ConfirmationOverlay.SUCCESS_ANIMATION)
                                                                .setMessage(firebaseRemoteConfig.getString(entryConfigurations.getString(R.string.stringPlayStoreLinkDialogue)))
                                                                .setDuration(1500 * 1)
                                                                .showOn(entryConfigurations)

                                                        functionsClassCheckpoint.savePreference(".UserState", "FirstTime", false)
                                                    }
                                                } else if (resultCode == RemoteIntent.RESULT_FAILED) {

                                                } else {

                                                }
                                            }
                                        })
                            }
                        }

                        createShortcutAd(firebaseRemoteConfig)
                    }
                }

        return firebaseRemoteConfig
    }

    private fun createShortcutAd(firebaseRemoteConfig: FirebaseRemoteConfig) {

        val shortcutIconType: String? = firebaseRemoteConfig.getString(entryConfigurations.getString(R.string.shortcutIconType))
        val shortcutId: String? = firebaseRemoteConfig.getString(entryConfigurations.getString(R.string.shortcutId))
        val shortcutLabel: String = firebaseRemoteConfig.getString(entryConfigurations.getString(R.string.shortcutLabel))
        val shortcutIconLink: String? = firebaseRemoteConfig.getString(entryConfigurations.getString(R.string.shortcutIconLink))
        val shortcutActionLink: String? = firebaseRemoteConfig.getString(entryConfigurations.getString(R.string.shortcutActionLink))

        shortcutId?.let {

            if (shortcutIconType == "GIF") {

                Glide.with(entryConfigurations)
                        .asGif()
                        .load(shortcutIconLink)
                        .addListener(object : RequestListener<GifDrawable> {
                            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<GifDrawable>?, isFirstResource: Boolean): Boolean {
                                e?.printStackTrace()

                                return false
                            }

                            override fun onResourceReady(resource: GifDrawable?, model: Any?, target: Target<GifDrawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {

                                resource?.let { icon ->

                                    shortcutActionLink?.let { actionLink ->

                                        entryConfigurations.runOnUiThread {
                                            entryConfigurations.adShortcut.contentDescription = shortcutLabel

                                            entryConfigurations.adShortcut.setOnClickListener {

                                                Intent(Intent.ACTION_VIEW).apply {
                                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                    data = Uri.parse(actionLink)

                                                    entryConfigurations.startActivity(this)
                                                }
                                            }

                                            entryConfigurations.adShortcut.setOnLongClickListener {
                                                Toast.makeText(entryConfigurations, shortcutLabel, Toast.LENGTH_LONG).show()

                                                false
                                            }
                                        }
                                    }
                                }

                                return false
                            }
                        })
                        .into(entryConfigurations.adShortcut)
            } else {

                Glide.with(entryConfigurations)
                        .asDrawable()
                        .load(shortcutIconLink)
                        .addListener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                                e?.printStackTrace()

                                return false
                            }

                            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {

                                resource?.let { icon ->

                                    shortcutActionLink?.let { actionLink ->
                                        entryConfigurations.runOnUiThread {
                                            entryConfigurations.adShortcut.contentDescription = shortcutLabel

                                            entryConfigurations.adShortcut.setOnClickListener {

                                                Intent(Intent.ACTION_VIEW).apply {
                                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                    data = Uri.parse(actionLink)

                                                    entryConfigurations.startActivity(this)
                                                }
                                            }

                                            entryConfigurations.adShortcut.setOnLongClickListener {
                                                Toast.makeText(entryConfigurations, shortcutLabel, Toast.LENGTH_LONG).show()

                                                false
                                            }
                                        }
                                    }
                                }

                                return false
                            }
                        })
                        .into(entryConfigurations.adShortcut)
            }
        }
    }
}