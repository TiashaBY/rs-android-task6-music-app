package com.rsschool.myapplication.mediaplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rsschool.myapplication.mediaplayer.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController

@AndroidEntryPoint
class PlayerActivity : AppCompatActivity() {

    private var navController: NavController? = null

    private var _binding: ActivityMainBinding? = null
    private val binding get() = checkNotNull(_binding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container)
        navController = navHostFragment?.findNavController()
        navController?.graph?.let {
            val appBarConfiguration = AppBarConfiguration(it)
            setupActionBarWithNavController(navController!!, appBarConfiguration)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController?.navigateUp() == true || super.onSupportNavigateUp()
    }
}


   /*
    public override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            viewModel.initializePlayer()
            viewModel.audioList.observe(this, Observer {
                viewBinding.exoStyledPlayerView.player = viewModel.getPlayer()
            })
        }
    }

    public override fun onResume()  {
        super.onResume()
        if (Util.SDK_INT <= 23) {
            viewModel.initializePlayer()
            viewModel.audioList.observe(this, Observer {
                viewBinding.exoStyledPlayerView.player = viewModel.getPlayer()
            })
        }
    }

    public override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    public override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }*/

