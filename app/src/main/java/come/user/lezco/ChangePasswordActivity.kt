package come.user.lezco

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu
import com.koushikdutta.ion.Ion
import com.victor.loading.rotate.RotateLoading
import come.user.lezco.utils.Common
import come.user.lezco.utils.Common.Companion.showMKPanelError
import come.user.lezco.utils.Url
import org.json.JSONException
import org.json.JSONObject


class ChangePasswordActivity : AppCompatActivity() {

    lateinit var txt_change_password: TextView
    lateinit var txt_change_password_logo: TextView
    lateinit var txt_change_pass: TextView
    lateinit var edit_current_pass: EditText
    lateinit var edit_new_pass: EditText
    lateinit var edit_con_pass: EditText
    lateinit var layout_change_password_button: RelativeLayout
    lateinit var layout_menu: RelativeLayout

    lateinit var OpenSans_Regular: Typeface
    lateinit var OpenSans_Bold: Typeface
    lateinit var regularRoboto: Typeface
    lateinit var Roboto_Bold: Typeface
    lateinit var slidingMenu: SlidingMenu

    lateinit var userPref: SharedPreferences

    lateinit var ProgressDialog: Dialog
    lateinit var cusRotateLoading: RotateLoading

    //error Alert
    lateinit var rlMainView: RelativeLayout
    lateinit var tvTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        //Error Alert
        rlMainView = findViewById(R.id.rlMainView) as RelativeLayout
        tvTitle = findViewById(R.id.tvTitle) as TextView

        val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(0, resources.getDimension(R.dimen.height_50).toInt(), 0, 0)
        rlMainView.layoutParams = params

        txt_change_password = findViewById(R.id.txt_change_password) as TextView
        edit_current_pass = findViewById(R.id.edit_current_pass) as EditText
        edit_new_pass = findViewById(R.id.edit_new_pass) as EditText
        edit_con_pass = findViewById(R.id.edit_con_pass) as EditText
        layout_change_password_button = findViewById(R.id.layout_change_password_button) as RelativeLayout
        layout_menu = findViewById(R.id.layout_menu) as RelativeLayout
        txt_change_password_logo = findViewById(R.id.txt_change_password_logo) as TextView
        txt_change_pass = findViewById(R.id.txt_change_pass) as TextView

        ProgressDialog = Dialog(this@ChangePasswordActivity, android.R.style.Theme_Translucent_NoTitleBar)
        ProgressDialog.setContentView(R.layout.custom_progress_dialog)
        ProgressDialog.setCancelable(false)
        cusRotateLoading = ProgressDialog.findViewById(R.id.rotateloading_register) as RotateLoading

        userPref = PreferenceManager.getDefaultSharedPreferences(this@ChangePasswordActivity)

        OpenSans_Regular = Typeface.createFromAsset(assets, "fonts/OpenSans-Regular_0.ttf")
        OpenSans_Bold = Typeface.createFromAsset(assets, "fonts/OpenSans-Bold_0.ttf")
        regularRoboto = Typeface.createFromAsset(assets, "fonts/Roboto-Regular.ttf")
        Roboto_Bold = Typeface.createFromAsset(assets, "fonts/Roboto-Bold.ttf")

        txt_change_password.typeface = OpenSans_Bold
        edit_new_pass.typeface = OpenSans_Regular
        edit_con_pass.typeface = OpenSans_Regular
        edit_current_pass.typeface = OpenSans_Regular
        txt_change_password_logo.typeface = Roboto_Bold
        txt_change_pass.typeface = Roboto_Bold

        layout_change_password_button.setOnClickListener(View.OnClickListener {
            Log.d("password", "Login password = " + userPref.getString("password", "")!!)
            if (edit_current_pass.text.toString().trim { it <= ' ' }.length == 0) {
                showMKPanelError(this@ChangePasswordActivity, resources.getString(R.string.please_enter_current_password), rlMainView, tvTitle, regularRoboto)
                edit_current_pass.requestFocus()
                return@OnClickListener
            } else if (edit_current_pass.text.toString().trim { it <= ' ' } != userPref.getString("password", "")) {
                showMKPanelError(this@ChangePasswordActivity, resources.getString(R.string.please_current_password), rlMainView, tvTitle, regularRoboto)
                edit_current_pass.requestFocus()
                return@OnClickListener
            } else if (edit_new_pass.text.toString().trim { it <= ' ' }.length == 0) {
                showMKPanelError(this@ChangePasswordActivity, resources.getString(R.string.please_enter_new_password), rlMainView, tvTitle, regularRoboto)
                edit_new_pass.requestFocus()
                return@OnClickListener
            } else if (edit_new_pass.text.toString().trim { it <= ' ' }.length < 6 || edit_new_pass.text.toString().trim { it <= ' ' }.length > 32) {
                showMKPanelError(this@ChangePasswordActivity, resources.getString(R.string.password_new_length), rlMainView, tvTitle, regularRoboto)
                edit_new_pass.requestFocus()
                return@OnClickListener
            } else if (edit_con_pass.text.toString().trim { it <= ' ' }.length == 0) {
                showMKPanelError(this@ChangePasswordActivity, resources.getString(R.string.please_enter_confirm_password), rlMainView, tvTitle, regularRoboto)
                edit_con_pass.requestFocus()
                return@OnClickListener
            } else if (edit_new_pass.text.toString() != edit_con_pass.text.toString()) {
                showMKPanelError(this@ChangePasswordActivity, resources.getString(R.string.password_new_confirm), rlMainView, tvTitle, regularRoboto)
                edit_con_pass.requestFocus()
                return@OnClickListener
            }

            if (Common.isNetworkAvailable(this@ChangePasswordActivity)) {

                ProgressDialog.show()
                cusRotateLoading.start()

                Ion.with(this@ChangePasswordActivity)
                        .load(Url.changePasswordUrl)
                        .setTimeout(6000)
                        //.setJsonObjectBody(json)
                        .setMultipartParameter("password", edit_new_pass.text.toString().trim { it <= ' ' })
                        .setMultipartParameter("uid", userPref.getString("id", ""))
                        .asJsonObject()
                        .setCallback { error, result ->
                            // do stuff with the result or error
                            Log.d("Login result", "Login result = $result==$error")
                            if (error == null) {

                                ProgressDialog.cancel()
                                cusRotateLoading.stop()

                                try {
                                    val resObj = JSONObject(result.toString())
                                    if (resObj.getString("status") == "success") {
                                        Common.showMkSucess(this@ChangePasswordActivity, resources.getString(R.string.password_change_sucess), "yes")

                                        val newPass = userPref.edit()
                                        newPass.putString("password", edit_new_pass.text.toString().trim { it <= ' ' })
                                        newPass.commit()

                                    } else if (resObj.getString("status") == "false") {
                                        Common.user_InActive = 1
                                        Common.InActive_msg = resObj.getString("message")

                                        val editor = userPref.edit()
                                        editor.clear()
                                        editor.commit()

                                        val logInt = Intent(this@ChangePasswordActivity, LoginOptionActivity::class.java)
                                        logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        logInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                        logInt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(logInt)
                                    }
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }

                            } else {
                                ProgressDialog.cancel()
                                cusRotateLoading.stop()

                                Common.ShowHttpErrorMessage(this@ChangePasswordActivity, error.message)
                            }
                        }
            }
        })

        slidingMenu = SlidingMenu(this)
        slidingMenu.mode = SlidingMenu.LEFT
        slidingMenu.touchModeAbove = SlidingMenu.TOUCHMODE_FULLSCREEN
        slidingMenu.setBehindOffsetRes(R.dimen.slide_menu_width)
        slidingMenu.setFadeDegree(0.20f)
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT)
        slidingMenu.setMenu(R.layout.left_menu)

        val common = Common()
        common.SlideMenuDesign(slidingMenu, this@ChangePasswordActivity, "change password")

        layout_menu.setOnClickListener { slidingMenu.toggle() }

        Common.ValidationGone(this@ChangePasswordActivity, rlMainView, edit_current_pass)
        Common.ValidationGone(this@ChangePasswordActivity, rlMainView, edit_new_pass)
        Common.ValidationGone(this@ChangePasswordActivity, rlMainView, edit_con_pass)


    }

    override fun onBackPressed() {
        if (slidingMenu.isMenuShowing) {
            slidingMenu.toggle()
        } else {
            AlertDialog.Builder(this)
                    .setTitle("Really Exit?")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes) { arg0, arg1 -> super@ChangePasswordActivity.onBackPressed() }.create().show()
        }
    }
}
