package com.example.tinderclonecoding

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity: AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // FirebaseAuth 인스턴스를 가져옴
        auth = Firebase.auth

        callbackManager = CallbackManager.Factory.create()

        initLoginButton()
        initSignUpButton()
        initEmailAndPasswordEditText()
        initFacebookLoginButton()
    }

    // 로그인 버튼
    private fun initLoginButton() {
        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            val email = getInputEmail()
            val password = getInputPassword()

            //FirebasAuth db와 입력한 login 정보 비교
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    // 성공하면 LoginActivity 종료
                    if (task.isSuccessful){
                        finish()
                    }else{ // 실패
                        Toast.makeText(this,"로그인에 실패했습니다. 이메일 또는 패스워드를 확인해 주세요!", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    // 회원가입 버튼
    private fun initSignUpButton() {
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        signUpButton.setOnClickListener {
            val email = getInputEmail()
            val password = getInputPassword()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful){
                        Toast.makeText(this,"회원가입이 완료되었습니다!!",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this,"이미 가입된 이메일이거나, 회원가입에 실패했습니다!!", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun initEmailAndPasswordEditText(){
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signUpButton = findViewById<Button>(R.id.signUpButton)

        // EditText Null 일 때 예외 처리
        emailEditText.addTextChangedListener {
            val enable = emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()
            loginButton.isEnabled = enable
            signUpButton.isEnabled = enable
        }

        passwordEditText.addTextChangedListener {
            val enable = emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()
            loginButton.isEnabled = enable
            signUpButton.isEnabled = enable
        }

    }

    private fun initFacebookLoginButton(){
        val facebookLoginButton = findViewById<LoginButton>(R.id.facebookLoginButton)

        //로그인 버튼을 눌렀을 때 유저에게 받아올 정보 권한 요청
        facebookLoginButton.setPermissions("email", "public_profile")

        facebookLoginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult) {
                // 성공하면 accessToken token을 가져옴
                val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                
                auth.signInWithCredential(credential)
                    .addOnCompleteListener(this@LoginActivity) { task ->
                        if (task.isSuccessful){
                            finish()
                        }else{
                            Toast.makeText(this@LoginActivity,"페이스북 로그인이 실패했습니다.",Toast.LENGTH_SHORT).show()
                        }
                    }
            }

            override fun onCancel() {}

            override fun onError(error: FacebookException?) {
                Toast.makeText(this@LoginActivity,"페이스북 로그인이 실패했습니다.",Toast.LENGTH_SHORT).show()
            }

        })
    }


    private fun getInputEmail(): String {
        return findViewById<EditText>(R.id.emailEditText).text.toString()
    }

    private fun getInputPassword(): String{
        return findViewById<EditText>(R.id.passwordEditText).text.toString()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

}