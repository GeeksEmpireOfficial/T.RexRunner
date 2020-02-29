/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 2/20/20 2:24 PM
 * Last modified 2/20/20 1:47 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.geeky.gify.Utils.UI

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PopupAppShortcuts(val context: Context) {

    fun create(shortcutId: String, shortcutLabel: String, appIcon: Icon,
               actionLink: String) = CoroutineScope(Dispatchers.IO).launch {

        val shortcutManager = context.getSystemService(ShortcutManager::class.java) as ShortcutManager

        val intent = Intent().setAction(Intent.ACTION_VIEW).setData(Uri.parse(actionLink)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val shortcutInfo = ShortcutInfo.Builder(context, shortcutId)
                .setShortLabel(shortcutLabel)
                .setLongLabel(shortcutLabel)
                .setIcon(appIcon)
                .setIntent(intent)
                .setRank(1)
                .build()

        shortcutManager.addDynamicShortcuts(arrayOf(shortcutInfo).toMutableList())
    }
}