package pl.domain.application.petreco.auth

import android.app.Activity
import android.util.Log
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth

object FacebookAuthHandler {

    lateinit var callbackManager: CallbackManager
    var onLoginResult: ((success: Boolean, errorMessage: String?) -> Unit)? = null
    var onLoadingChanged: ((Boolean) -> Unit)? = null

    fun init(activity: Activity) {
        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    onLoadingChanged?.invoke(true)
                    handleFacebookAccessToken(loginResult.accessToken.token, activity)
                }

                override fun onCancel() {
                    onLoadingChanged?.invoke(false)
                    Log.d("FacebookAuthHandler", "Login canceled")
                    onLoginResult?.invoke(false, "Logowanie zostało anulowane")
                }

                override fun onError(error: FacebookException) {
                    onLoadingChanged?.invoke(false)
                    Log.e("FacebookAuthHandler", "Login error: ${error.message}")
                    onLoginResult?.invoke(false, error.message ?: "Błąd logowania przez Facebook")
                }

            })
    }

    private fun handleFacebookAccessToken(token: String, activity: Activity) {
        val credential = FacebookAuthProvider.getCredential(token)
        FirebaseAuth.getInstance()
            .signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                onLoadingChanged?.invoke(false)
                if (task.isSuccessful) {
                    Log.d("FacebookAuthHandler", "Login successful: ${task.result?.user?.email}")
                    onLoginResult?.invoke(true, null)
                } else {
                    Log.e("FacebookAuthHandler", "Firebase login failed: ${task.exception?.message}")
                    onLoginResult?.invoke(false, task.exception?.message ?: "Nieznany błąd logowania")
                }
            }
    }

    fun loginWithFacebook(activity: Activity) {
        LoginManager.getInstance().logInWithReadPermissions(activity, listOf("email", "public_profile"))
    }
}