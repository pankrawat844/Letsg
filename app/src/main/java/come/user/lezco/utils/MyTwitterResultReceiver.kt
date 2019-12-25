package come.user.lezco.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

import com.twitter.sdk.android.tweetcomposer.TweetUploadService

/**
 * Created by techintegrity on 08/07/16.
 */
class MyTwitterResultReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        println("Twitter is uploaded successfully" + intent.action!!)

        if (TweetUploadService.UPLOAD_SUCCESS == intent.action) {
            Toast.makeText(context, "Your post was shared successfully.", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Your post was shared successfully.", Toast.LENGTH_LONG).show()
        }
    }
}
