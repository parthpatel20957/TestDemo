package com.e.testapp.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.e.testapp.R
import com.e.testapp.databinding.ActivityHomeBinding
import com.e.testapp.login.LoginActivity
import com.e.testapp.room.AppDatabase
import com.e.testapp.room.User
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding.vm = viewModel

        init()

    }

    fun init() {
        var user: List<User> = arrayListOf()
        val db = AppDatabase(this)
        GlobalScope.launch {
            user = db.userDao().getAll()
        }.invokeOnCompletion {
            viewModel.userName.set(user.firstOrNull()?.userName)
        }

        binding.btnLogout.setOnClickListener {
            GlobalScope.launch {
                db.userDao().clearData()
            }.invokeOnCompletion {
                finish()
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }


    }
}