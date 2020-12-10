package com.e.testapp.login

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.e.testapp.R
import com.e.testapp.databinding.ActivityLoginBinding
import com.e.testapp.home.HomeActivity
import com.e.testapp.model.LoginResponseData
import com.e.testapp.room.AppDatabase
import com.e.testapp.room.User
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private val REQUEST_CODE = 101
    lateinit var telephonyManager: TelephonyManager

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        binding.vm = viewModel

        telephonyManager =
            getSystemService(android.content.Context.TELEPHONY_SERVICE) as android.telephony.TelephonyManager

        init()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    viewModel.imsi = telephonyManager.subscriberId
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        viewModel.imei = telephonyManager.imei
                    } else {
                        viewModel.imei = telephonyManager.deviceId
                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    fun init() {
        val db = AppDatabase(this)

        var user: List<User> = arrayListOf()
        GlobalScope.launch {
            user = db.userDao().getAll()
        }.invokeOnCompletion {
            if (user.size > 0) {
                finish()
                startActivity(Intent(this, HomeActivity::class.java))
            }
        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), REQUEST_CODE)
        } else {
            viewModel.imsi = telephonyManager.subscriberId
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                viewModel.imei = telephonyManager.imei
            } else {
                viewModel.imei = telephonyManager.deviceId
            }
        }


        binding.btnSubmit.setOnClickListener {
            if (isValid()) {
                viewModel.callLoginApi().observe(this, Observer {
                    it.let {
                        val xAcc = it.headers().get("X-Acc")
                        val loginResponseData = Gson().fromJson(it.body()?.string(), LoginResponseData::class.java)
                        val user = User(
                            userId = loginResponseData.user.userId.toString(),
                            userName = loginResponseData.user.userName,
                            xACC = xAcc
                        )
                        GlobalScope.launch {
                            db.userDao().insertAll(user)
                        }
                        Toast.makeText(this, loginResponseData.errorMessage, Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()

                    }
                })

            }
        }

    }

    fun isValid(): Boolean {
        return when {
            TextUtils.isEmpty(viewModel.userName.get()) -> {
                binding.edtUserName.error = "Please enter username"
                false
            }

            TextUtils.isEmpty(viewModel.pasword.get()) -> {
                binding.edtUserName.setErrorEnabled(false);
                binding.edtpassword.error = "Please enter password"
                false
            }

            TextUtils.isEmpty(viewModel.imei) -> {
                Toast.makeText(this, "Please Allow permission from setting", Toast.LENGTH_SHORT)
                    .show()
                false
            }
            else -> {
                binding.edtUserName.setErrorEnabled(false);
                binding.edtpassword.setErrorEnabled(false);
                true
            }
        }
    }

}