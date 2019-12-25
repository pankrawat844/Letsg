package come.user.lezco.utils

import android.app.ActivityManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.google.android.gms.gcm.GcmListenerService
import come.user.lezco.AllTripActivity
import come.user.lezco.R
import java.net.URLDecoder

/**
 * Created by techintegrity on 05/08/16.
 */
class MyGcmListenerService : GcmListenerService() {

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     * For Set of keys use data.keySet().
     */
    // [START receive_message]
    override fun onMessageReceived(from: String?, data: Bundle) {

        val message = data.getString("message")
        //        System.out.println("Indiaries >>"+message);

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         * - Store message in local database.
         * - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */

        println("GCM Indiaries PushNotification Message >>" + message!!)

        sendNotification(message)
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private fun sendNotification(message: String?) {

        try {

            //            JSONObject jsonObject = new JSONObject(message);
            //
            //            String badgeCount = jsonObject.getString("badge");

            val intent = Intent(this, AllTripActivity::class.java)
            //        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            val pendingIntent = PendingIntent.getActivity(
                this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT
            )
            //Bitmap bitmap = getBitmapFromURL(URLDecoder.decode(jsonObject.getString("imageUrl"), "UTF-8"));
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)

                .setContentTitle(URLDecoder.decode(resources.getString(R.string.app_name), "UTF-8"))
                .setContentText(URLDecoder.decode(message, "UTF-8"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())


            //send Local BroadCast Message
            //            if(badgeCount!=null){
            //                Common.badgeCount=badgeCount;
            setBadge(this)
            //            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    companion object {
        private val TAG = "MyGcmListenerService"

        fun setBadge(context: Context) {

            val launcherClassName = getLauncherClassName(context) ?: return
            val intent = Intent("android.intent.action.BADGE_COUNT_UPDATE")
            //intent.putExtra("badge_count", count);
            intent.putExtra("badge_count_package_name", context.packageName)
            intent.putExtra("badge_count_class_name", launcherClassName)
            context.sendBroadcast(intent)


            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val cn = am.getRunningTasks(1)[0].topActivity

            println("Local Class Name >>" + cn!!.className)
            val intent1 = Intent(cn.className)
            //intent1.putExtra("value",Common.badgeCount);
            context.sendBroadcast(intent1)

        }

        fun getLauncherClassName(context: Context): String? {

            val pm = context.packageManager

            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)

            val resolveInfos = pm.queryIntentActivities(intent, 0)
            for (resolveInfo in resolveInfos) {
                val pkgName = resolveInfo.activityInfo.applicationInfo.packageName
                if (pkgName.equals(context.packageName, ignoreCase = true)) {
                    return resolveInfo.activityInfo.name
                }
            }
            return null
        }
    }
}