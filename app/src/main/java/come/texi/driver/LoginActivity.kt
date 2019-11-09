package come.texi.driver

import android.app.Dialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.victor.loading.rotate.RotateLoading
import come.texi.driver.utils.Common
import come.texi.driver.utils.Common.Companion.isNetworkAvailable
import come.texi.driver.utils.Common.Companion.showInternetInfo
import come.texi.driver.utils.Common.Companion.showMKPanelError
import come.texi.driver.utils.Url

import java.io.UnsupportedEncodingException
import java.net.URLEncoder

class LoginActivity : AppCompatActivity() {

    lateinit var edit_username: EditText
    lateinit var edit_password: EditText
    lateinit var layout_signin: RelativeLayout
    lateinit var layout_forgot: RelativeLayout
    lateinit var txt_forgot_pass: TextView
    lateinit var layout_show_hide: RelativeLayout
    lateinit var txt_hide_show: TextView
    lateinit var txt_signin: TextView
    lateinit var txt_sign_in_logo: TextView
    lateinit var layout_login_main: LinearLayout

    lateinit var OpenSans_Regular: Typeface
    lateinit var OpenSans_Bold: Typeface
    lateinit var regularRoboto: Typeface
    lateinit var Roboto_Bold: Typeface

    lateinit var ProgressDialog: Dialog
    lateinit var cusRotateLoading: RotateLoading
    internal var common = Common()

    //Error Alert
    lateinit var rlMainView: RelativeLayout
    lateinit var tvTitle: TextView

    internal var passwordShowHide = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Error Alert
        rlMainView = findViewById(R.id.rlMainView) as RelativeLayout
        tvTitle = findViewById(R.id.tvTitle) as TextView

        layout_login_main = findViewById(R.id.layout_login_main) as LinearLayout
        edit_username = findViewById(R.id.edit_username) as EditText
        edit_password = findViewById(R.id.edit_password) as EditText
        layout_signin = findViewById(R.id.layout_signin) as RelativeLayout
        layout_forgot = findViewById(R.id.layout_forgot) as RelativeLayout
        txt_forgot_pass = findViewById(R.id.txt_forgot_pass) as TextView
        layout_show_hide = findViewById(R.id.layout_show_hide) as RelativeLayout
        txt_hide_show = findViewById(R.id.txt_hide_show) as TextView
        txt_signin = findViewById(R.id.txt_signin) as TextView
        txt_sign_in_logo = findViewById(R.id.txt_sign_in_logo) as TextView

        ProgressDialog = Dialog(this@LoginActivity, android.R.style.Theme_Translucent_NoTitleBar)
        ProgressDialog.setContentView(R.layout.custom_progress_dialog)
        ProgressDialog.setCancelable(false)
        cusRotateLoading = ProgressDialog.findViewById(R.id.rotateloading_register) as RotateLoading

        OpenSans_Bold = Typeface.createFromAsset(assets, "fonts/OpenSans-Bold_0.ttf")
        OpenSans_Regular = Typeface.createFromAsset(assets, "fonts/OpenSans-Regular_0.ttf")
        regularRoboto = Typeface.createFromAsset(assets, "fonts/Roboto-Regular.ttf")
        Roboto_Bold = Typeface.createFromAsset(assets, "fonts/Roboto-Bold.ttf")

        edit_username.typeface = OpenSans_Regular
        edit_password.typeface = OpenSans_Regular
        txt_forgot_pass.typeface = OpenSans_Regular
        txt_signin.typeface = Roboto_Bold
        txt_sign_in_logo.typeface = Roboto_Bold

        layout_signin.setOnClickListener(View.OnClickListener {
            if (edit_username.text.toString().trim { it <= ' ' }.length == 0) {
                //showLoginRegisterMkError(LoginActivity.this, getResources().getString(R.string.please_enter_username));
                showMKPanelError(
                    this@LoginActivity,
                    resources.getString(R.string.please_enter_username),
                    rlMainView,
                    tvTitle,
                    regularRoboto
                )
                return@OnClickListener
            } else if (edit_password.text.toString().trim { it <= ' ' }.length == 0) {
                showMKPanelError(
                    this@LoginActivity,
                    resources.getString(R.string.please_enter_password),
                    rlMainView,
                    tvTitle,
                    regularRoboto
                )
                return@OnClickListener
            } else if (edit_password.text.toString().trim { it <= ' ' }.length < 8 || edit_password.text.toString().trim { it <= ' ' }.length > 32) {
                showMKPanelError(
                    this@LoginActivity,
                    resources.getString(R.string.password_length),
                    rlMainView,
                    tvTitle,
                    regularRoboto
                )
                return@OnClickListener
            }


            //                ProgressDialog.show();
            //                cusRotateLoading.start();

            if (isNetworkAvailable(this@LoginActivity)) {

                var loginUrl: String? = null
                try {
                    loginUrl = Url.loginUrl + "?email=" + URLEncoder.encode(
                        edit_username.text.toString().trim { it <= ' ' },
                        "UTF-8"
                    ) + "&password=" + edit_password.text.toString().trim { it <= ' ' }
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }

                Log.d("loginUrl", "loginUrl " + loginUrl!!)
                Common.LoginCallHttp(
                    this@LoginActivity,
                    ProgressDialog,
                    cusRotateLoading,
                    edit_password.text.toString().trim { it <= ' ' },
                    "",
                    loginUrl
                ).execute()
            } else {
                showInternetInfo(this@LoginActivity, "")
            }
        })

        layout_forgot.setOnClickListener {
            val fi = Intent(this@LoginActivity, ForgotActivity::class.java)
            startActivity(fi)
        }

        //        edit_password.addTextChangedListener(new TextWatcher() {
        //            @Override
        //            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //
        //            }
        //
        //            @Override
        //            public void onTextChanged(CharSequence s, int start, int before, int count) {
        //                edit_password_show.setText(s);
        //            }
        //
        //            @Override
        //            public void afterTextChanged(Editable s) {
        //
        //            }
        //        });

        layout_show_hide.setOnClickListener {
            if (passwordShowHide) {
                txt_hide_show.text = resources.getString(R.string.show)
                edit_password.transformationMethod = PasswordTransformationMethod()
                passwordShowHide = false
            } else {
                edit_password.transformationMethod = HideReturnsTransformationMethod()
                txt_hide_show.text = resources.getString(R.string.hide)
                passwordShowHide = true
            }
            edit_password.setSelection(edit_password.text.length)
        }

        layout_login_main.setOnClickListener {
            if (rlMainView.visibility == View.VISIBLE) {
                if (!isFinishing) {
                    val slideUp = TranslateAnimation(
                        Animation.RELATIVE_TO_SELF,
                        0f,
                        Animation.RELATIVE_TO_SELF,
                        0f,
                        Animation.RELATIVE_TO_SELF,
                        0f,
                        Animation.RELATIVE_TO_SELF,
                        -100f
                    )
                    slideUp.duration = 10
                    slideUp.fillAfter = true
                    rlMainView.startAnimation(slideUp)
                    slideUp.setAnimationListener(object : Animation.AnimationListener {

                        override fun onAnimationStart(animation: Animation) {}

                        override fun onAnimationRepeat(animation: Animation) {}

                        override fun onAnimationEnd(animation: Animation) {
                            rlMainView.visibility = View.GONE
                        }
                    })

                }
            }
        }

        Common.ValidationGone(this@LoginActivity, rlMainView, edit_username)
        Common.ValidationGone(this@LoginActivity, rlMainView, edit_password)

        Log.d("user_InActive", "user_InActive = " + Common.user_InActive)

    }
}