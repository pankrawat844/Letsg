package come.texi.driver

import android.app.Dialog
import android.graphics.Typeface
import android.os.Handler
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.google.gson.JsonObject
import com.koushikdutta.async.future.FutureCallback
import com.koushikdutta.ion.Ion
import com.victor.loading.rotate.RotateLoading

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import come.texi.driver.adapter.AllTripAdapter
import come.texi.driver.utils.AllTripFeed
import come.texi.driver.utils.Common
import come.texi.driver.utils.Url
import cz.msebera.android.httpclient.Header

class ForgotActivity : AppCompatActivity() {

    internal var txt_forgot_password: TextView? = null
    lateinit var txt_for_pass_logo: TextView
    lateinit var txt_retrive_password: TextView
    internal var layout_back_arrow: RelativeLayout? = null
    internal var layout_retrive_password: RelativeLayout? = null
    internal var edit_username: EditText? = null

    lateinit var OpenSans_Bold: Typeface
    lateinit var Roboto_Bold: Typeface

    lateinit var ProgressDialog: Dialog
    lateinit var cusRotateLoading: RotateLoading

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)

        txt_forgot_password = findViewById(R.id.txt_forgot_password) as TextView?
        layout_back_arrow = findViewById(R.id.layout_back_arrow) as RelativeLayout?
        layout_retrive_password = findViewById(R.id.layout_retrive_password) as RelativeLayout?
        edit_username = findViewById(R.id.edit_username) as EditText?
        txt_for_pass_logo = findViewById(R.id.txt_for_pass_logo) as TextView
        txt_retrive_password = findViewById(R.id.txt_retrive_password) as TextView

        OpenSans_Bold = Typeface.createFromAsset(assets, "fonts/OpenSans-Bold_0.ttf")
        Roboto_Bold = Typeface.createFromAsset(assets, "fonts/Roboto-Bold.ttf")

        txt_forgot_password!!.typeface = OpenSans_Bold
        txt_for_pass_logo.typeface = Roboto_Bold
        txt_retrive_password.typeface = Roboto_Bold

        ProgressDialog = Dialog(this@ForgotActivity, android.R.style.Theme_Translucent_NoTitleBar)
        ProgressDialog.setContentView(R.layout.custom_progress_dialog)
        ProgressDialog.setCancelable(false)
        cusRotateLoading = ProgressDialog.findViewById(R.id.rotateloading_register) as RotateLoading

        layout_retrive_password!!.setOnClickListener(View.OnClickListener {
            if (edit_username!!.text.toString().trim { it <= ' ' }.length == 0) {
                Common.showMkError(this@ForgotActivity, resources.getString(R.string.please_enter_email))
                return@OnClickListener
            } else if (edit_username!!.text.toString().trim { it <= ' ' }.length != 0 && !isValidEmail(edit_username!!.text.toString().trim { it <= ' ' })) {
                Common.showMkError(this@ForgotActivity, resources.getString(R.string.please_enter_valid_email))
                return@OnClickListener
            }

            if (Common.isNetworkAvailable(this@ForgotActivity)) {

                ProgressDialog.show()
                cusRotateLoading.start()

                Ion.with(this@ForgotActivity)
                        .load(Url.forgotPasswordUrl + "?email=" + edit_username!!.text.toString())
                        .setTimeout(10000)
                        .asJsonObject()
                        .setCallback { error, result ->
                            // do stuff with the result or error
                            Log.d("Login result", "Login result = $result==$error")
                            ProgressDialog.cancel()
                            cusRotateLoading.stop()
                            if (error == null) {

                                try {
                                    val resObj = JSONObject(result.toString())

                                    if (resObj.getString("status") == "success") {
                                        Common.showMkSucess(this@ForgotActivity, resObj.getString("message").toString(), "yes")
                                    } else if (resObj.getString("status") == "failed") {
                                        Common.showMkError(this@ForgotActivity, resObj.getString("error code").toString())
                                    }

                                    Handler().postDelayed({ finish() }, 2000)

                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }

                            } else {
                                ProgressDialog.cancel()
                                cusRotateLoading.stop()

                                Common.ShowHttpErrorMessage(this@ForgotActivity, error.message)
                            }
                        }

            } else {
                ProgressDialog.cancel()
                cusRotateLoading.stop()
                Common.showInternetInfo(this@ForgotActivity, "Network is not available")
            }
        })

        layout_back_arrow!!.setOnClickListener { finish() }

    }

    public override fun onDestroy() {
        super.onDestroy()

        txt_forgot_password = null
        layout_back_arrow = null
        edit_username = null
        layout_retrive_password = null

    }

    companion object {

        fun isValidEmail(target: CharSequence): Boolean {
            return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }
}
