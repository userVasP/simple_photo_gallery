package com.example.galery.ui

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.galery.ui.theme.GalleryTheme
import com.example.galery.utilities.Constants
import com.example.galery.viewmodels.MainActivityViewModel


@Composable
fun GalleryApp(mainActivityViewModel: MainActivityViewModel = viewModel()) {

    val context = LocalContext.current

    val permissionNeed = mainActivityViewModel.permissionNeededForDelete.observeAsState()
    val permissionDeleteRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            mainActivityViewModel.deletePendingPhoto()
        }
    }


    if (permissionNeed.value != null)
    {
        LaunchedEffect(permissionNeed.value) {
            permissionNeed.value?.let {
                permissionDeleteRequest.launch(
                    IntentSenderRequest.Builder(it)
                        .build()
                )
            }
        }
    }


    var grantStateReadExtStoragePermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE,
            ) == PackageManager.PERMISSION_GRANTED
        )
    }


    val isPermissionsGranted = grantStateReadExtStoragePermission


    val resultReadExtStoragePermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            grantStateReadExtStoragePermission = true

        } else {
            (context as? Activity)?.finish()
        }
    }

    if (!grantStateReadExtStoragePermission) {
        LaunchedEffect(Unit) {
            resultReadExtStoragePermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    if (isPermissionsGranted) {
        GalleryTheme {
            val navController = rememberNavController()
            val isLogged = rememberSaveable { mutableStateOf(false) }
            val galleryScreens = if (isLogged.value) galleryScreensLogin else galleryScreensLogout
            val currentBackStack by navController.currentBackStackEntryAsState()
            val currentDestination = currentBackStack?.destination
            val galleryScreen = galleryScreens.find { it.route == currentDestination?.route }
            val currentScreen: GalleryDestinationIcon = galleryScreen ?: Gallery

            val scaffoldState: ScaffoldState = rememberScaffoldState()

            Scaffold(
                bottomBar = {
                    if(currentScreen.route != Registration.route && currentScreen.route != Login.route && galleryScreen != null) {
                        GalleryBottomNavigation(
                            screens = galleryScreens,
                            onItemSelected = { destination ->
                                navController.navigateSingleTopTo(destination.route)},
                            currentScreen = currentScreen
                        )
                    }  },
                scaffoldState = scaffoldState) {
                    padding -> GalleryNavHost(
                navController = navController,
                modifier = Modifier.padding(padding),
                scaffoldState = scaffoldState,
                isLogged = isLogged.value,
                onLogin = {isLogged.value = true},
                onLogout = {isLogged.value = false},
                mainViewModel = mainActivityViewModel
            )
            }
        }
    }
}

@Composable
fun GalleryBottomNavigation(
    modifier: Modifier = Modifier,
    screens: List<GalleryDestinationIcon>,
    onItemSelected: (GalleryDestinationIcon) -> Unit,
    currentScreen: GalleryDestinationIcon
) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.background,
        modifier = modifier
    ) {

        screens.forEach { screen ->
            BottomNavigationItem(
                icon = { Icon(imageVector = screen.icon, contentDescription = null)} ,
                label = { Text(screen.route) },
                selected = currentScreen == screen,
                onClick = { onItemSelected(screen) }
            )
        }
    }
}

@Preview
@Composable
fun GalleryAppPreview() {
    GalleryApp()
}