package net.geekstools.trexrunner.Util.Functions

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.*
import android.net.Uri
import android.os.Build
import android.text.Html
import net.geekstools.trexrunner.R

class FunctionsClassUI(var context: Context) {

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

    fun drawableToBitmap(drawable: Drawable): Bitmap? {
        var bitmap: Bitmap? = null
        if (drawable is VectorDrawable) {
            bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap!!)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
        } else if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                bitmap = drawable.bitmap
            }
        } else if (drawable is LayerDrawable) {

            bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap!!)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
        } else {
            bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap!!)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
        }
        return bitmap
    }
}