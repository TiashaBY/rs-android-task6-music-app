package com.rsschool.myapplication.mediaplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.rsschool.myapplication.mediaplayer.databinding.ActivityMainBinding
import com.rsschool.myapplication.mediaplayer.ui.PlayerFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.add(binding.fragmentContainer.id, PlayerFragment(), null).commit()
    }
}
