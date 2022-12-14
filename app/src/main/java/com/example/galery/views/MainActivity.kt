package com.example.galery.views

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.example.galery.GalleryApplication
import com.example.galery.R
import com.example.galery.databinding.ActivityMainBinding
import com.example.galery.di.AppComponent
import com.example.galery.utilities.Constants
import com.example.galery.viewmodels.MainActivityViewModel
import javax.inject.Inject


class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    private lateinit var appComponent: AppComponent
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var activityMainBinding: ActivityMainBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<MainActivityViewModel> { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding = DataBindingUtil.setContentView(
            this, R.layout.activity_main
        )
        appComponent = (application as GalleryApplication).appComponent
        appComponent.inject(this)

        checkStoragePermission()
        
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        activityMainBinding.bottomNav?.setupWithNavController(navController)
        val drawerLayout : DrawerLayout? = activityMainBinding.mainActivityLayout as DrawerLayout?

        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.galleryDetailFragment -> {
                    drawerLayout?.visibility = View.GONE
                    activityMainBinding.bottomNav?.visibility = View.GONE
                }
                R.id.loginFragment -> {
                    drawerLayout?.visibility = View.GONE
                    activityMainBinding.bottomNav?.visibility = View.GONE
                }
                R.id.registrationFragment -> {
                    drawerLayout?.visibility = View.GONE
                    activityMainBinding.bottomNav?.visibility = View.GONE
                }
                else -> {
                    drawerLayout?.visibility = View.VISIBLE
                    activityMainBinding.bottomNav?.visibility = View.VISIBLE
                }
            }
        }

        viewModel.permissionNeededForDelete.observe(this) { intentSender ->
            intentSender?.let {
                // On Android 10+, if the app doesn't have permission to modify
                // or delete an item, it returns an `IntentSender` that we can
                // use here to prompt the user to grant permission to delete (or modify)
                // the image.
                startIntentSenderForResult(
                    intentSender,
                    Constants.DELETE_PERMISSION_REQUEST,
                    null,
                    0,
                    0,
                    0,
                    null
                )
            }
        }
    }

    private fun onPermissionGranted() {
        viewModel.isPermissionGranted.value = true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val retValue = super.onCreateOptionsMenu(menu)
        val navigationView = activityMainBinding.navView

        if (navigationView == null) {
            menuInflater.inflate(R.menu.bottom_nav_menu, menu)
            return true
        }
        return retValue
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == Constants.PERMISSION_REQUEST_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted()
            }
            else {
                finish()
            }

        }
    }

    private fun checkStoragePermission() {
        if (SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
            PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
            PackageManager.PERMISSION_GRANTED) {
            onPermissionGranted()
        }
        else {
            requestStoragePermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.DELETE_PERMISSION_REQUEST) {
            viewModel.deletePendingPhoto()
        }
    }

    private fun requestStoragePermission(permissions: Array<String>) {
        ActivityCompat.requestPermissions(this, permissions, Constants.PERMISSION_REQUEST_STORAGE)
    }
}