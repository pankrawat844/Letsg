package come.user.lezco.utils

import android.app.IntentService
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager

import com.google.android.gms.gcm.GoogleCloudMessaging
import com.google.android.gms.iid.InstanceID

import come.user.lezco.R

/**
 * Created by techintegrity on 05/08/16.
 */
//Class constructor
class GCMRegistrationIntentService : IntentService("") {

    override fun onHandleIntent(intent: Intent?) {
        //Registering gcm to the device
        registerGCM()
    }

    private fun registerGCM() {
        //Registration complete intent initially null
        var registrationComplete: Intent? = null

        //Register token is also null
        //we will get the token on successfull registration
        var token: String? = null
        try {
            //Creating an instanceid
            val instanceID = InstanceID.getInstance(applicationContext)

            //Getting the token from the instance id
            token = instanceID.getToken(
                getString(R.string.gcm_defaultSenderId),
                GoogleCloudMessaging.INSTANCE_ID_SCOPE,
                null
            )

            //Displaying the token in the log so that we can copy it to send push notification
            //You can also extend the app by storing the token in to your server
            Log.w("GCMRegIntentService", "token:" + token!!)

            //on registration complete creating intent with success
            registrationComplete = Intent(REGISTRATION_SUCCESS)

            //Putting the token to the intent
            registrationComplete.putExtra("token", token)
        } catch (e: Exception) {
            //If any error occurred
            Log.w("GCMRegIntentService", "Registration error")
            registrationComplete = Intent(REGISTRATION_ERROR)
        }

        //Sending the broadcast that registration is completed
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete!!)
    }

    companion object {
        //Constants for success and errors
        val REGISTRATION_SUCCESS = "RegistrationSuccess"
        val REGISTRATION_ERROR = "RegistrationError"
    }
}
