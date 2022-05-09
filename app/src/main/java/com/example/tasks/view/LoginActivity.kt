package com.example.tasks.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.tasks.R
import com.example.tasks.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*
import java.util.concurrent.Executor

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        // Inicializa eventos
        setListeners();
        observe()

        mViewModel.isAuthenticationAvailable()

    }

    override fun onClick(v: View) {
        if (v.id == R.id.button_login) {
            handleLogin()
        } else if (v.id == R.id.text_register) {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun showAutentication() {
        val executor: Executor = ContextCompat.getMainExecutor(this)

        val biometricPrompt = BiometricPrompt(
            this@LoginActivity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {

                /*       override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                           super.onAuthenticationError(errorCode, errString)
                       }

                       //nesse caso não precisa tratar erro ou falha, pq o user ainda tem a possibilidade de usar por senha!

                       override fun onAuthenticationFailed() {
                           super.onAuthenticationFailed()
                       }*/

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                }
            }
        )

        val info: BiometricPrompt.PromptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticação Digital")
            .setSubtitle("Um subtítulo explicando o que precisa ser feito")
            .setDescription("Descrição um pouco maior, explicando exatamente o que será feito[...]")
            .setNegativeButtonText("Cancelar")
            .build()

        biometricPrompt.authenticate(info)
    }


    /**
     * Inicializa os eventos de click
     */
    private fun setListeners() {
        button_login.setOnClickListener(this)
        text_register.setOnClickListener(this)
    }


    /**
     * Observa ViewModel
     */
    private fun observe() {
        mViewModel.login.observe(this, Observer {
            if (it.success()) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(applicationContext, it.failure(), Toast.LENGTH_SHORT).show()
            }
        })

        mViewModel.fingerprint.observe(this, Observer {
            if (it) {
                showAutentication()
            }
        })
    }

    /**
     * Autentica usuário
     */
    private fun handleLogin() {
        val email = edit_email.text.toString()
        val password = edit_password.text.toString()

        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, getString(R.string.ERROR_UNEXPECTED), Toast.LENGTH_SHORT)
                .show()
        } else {
            mViewModel.doLogin(email, password)
        }
    }


}
