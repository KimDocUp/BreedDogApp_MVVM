package com.test.breedsdogapp.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavArgument
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.test.breedsdogapp.repository.MainRepository
import com.test.breedsdogapp.R
import com.test.breedsdogapp.db.AppDataBase
import com.test.breedsdogapp.models.Breed
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var viewModel : MainViewModel

    private val navController by lazy(LazyThreadSafetyMode.NONE) {
        Navigation.findNavController(this, R.id.nav_host_fragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val mainRepository = MainRepository(AppDataBase(this))
        val viewModelProviderFactory = MainViewModelProviderFactory(application, mainRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(MainViewModel::class.java)

        setupNav()

        setSupportActionBar(toolbar)
        NavigationUI.setupWithNavController(toolbar, navController)
        // связывание контроллера навигации и меню

    }
    override fun onSupportNavigateUp() = navController.navigateUp()

    private fun setupNav() {

        findViewById<BottomNavigationView>(R.id.bottomNV)
            .setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.imagesFragment -> hideBottomNV()
                else -> showBottomNV()
            }
        }
    }

    private fun showBottomNV() {
        bottomNV.visibility = View.VISIBLE
    }
    private fun hideBottomNV() {
        bottomNV.visibility = View.GONE

    }
}