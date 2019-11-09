package come.texi.driver.utils

import android.content.Intent

import com.google.android.gms.iid.InstanceIDListenerService

/**
 * Created by techintegrity on 05/08/16.
 */
class MyInstanceIDListenerService : InstanceIDListenerService() {

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    // [START refresh_token]
    override fun onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        val intent = Intent(this, RegistrationIntentService::class.java)
        startService(intent)
    }

    companion object {

        private val TAG = "MyInstanceIDLS"
    }
    // [END refresh_token]

}
