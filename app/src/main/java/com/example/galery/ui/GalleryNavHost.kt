package com.example.galery.ui

import android.net.Uri
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.galery.data.model.Photo
import com.example.galery.viewmodels.CommonGalleryViewModel
import com.example.galery.viewmodels.FavoriteGalleryViewModel
import com.example.galery.viewmodels.MainActivityViewModel
import java.nio.charset.StandardCharsets

@Composable
fun GalleryNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState,
    onLogin: () -> Unit,
    onLogout: () -> Unit,
    isLogged: Boolean,
    mainViewModel: MainActivityViewModel = viewModel()
) {

    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = if(!isLogged) Gallery.route else Server.route) {



        composable(route = Gallery.route) {
            val model: CommonGalleryViewModel = hiltViewModel()
            val photos = model.photos.observeAsState()
            GalleryGreed(
                modifier = Modifier,
                photos = photos.value?: emptyList(),
                onPhotoClick = { photo ->
                    val encodedUrl = Uri.encode(photo.uri.toString(), StandardCharsets.UTF_8.toString())
                    navController.navigateToDetail(encodedUrl, photo.getKey())
                }
            )
        }
        composable(route = Favorite.route) {
            val model: FavoriteGalleryViewModel = hiltViewModel()
            val photos = model.favoritePhotos.observeAsState()
            GalleryGreed(
                modifier = Modifier,
                photos = photos.value ?: listOf(),
                onPhotoClick = { photo ->
                    val encodedUrl = Uri.encode(photo.uri.toString(), StandardCharsets.UTF_8.toString())
                    navController.navigateToDetail(encodedUrl, photo.getKey())
                }
            )
        }
        if (isLogged) {
            composable(
                route = Server.route,
            ) {
                ServerScreen(onLogout = onLogout)
            }
        }
        else {
            composable(route = Registration.route) {
                RegistrationScreen(
                    onLoginClick = {navController.navigateSingleTopTo(Login.route)},
                    onRegistration = {
                        navController.navigate(Login.route) {
                            popUpTo(Login.route) { inclusive = true }
                        }
                                     },
                    scaffoldState = scaffoldState)
            }
            composable(route = Login.route) {
                LoginScreen(
                    onRegistrationClick =  { navController.navigateSingleTopTo(Registration.route)},
                    onLogin = {
                        onLogin()
                    },
                    scaffoldState = scaffoldState
                )
            }
        }


        composable(
            route = DetailPhoto.routeWithArgs,
            arguments = DetailPhoto.arguments
        ) {
            navBackStack ->
            val photoUri = navBackStack.arguments?.getString(DetailPhoto.uriPhotoArg) ?: ""
            val photoKey = navBackStack.arguments?.getString(DetailPhoto.keyPhotoArg) ?: ""
            GalleryDetailScreen(
                photoUri = photoUri,
                photoKey = photoKey,
                removePhoto = {
                    mainViewModel.deleteChosenPhoto(Uri.parse(photoUri))
                    navController.navigateUp()}
            )
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) { launchSingleTop = true }

fun NavHostController.navigateToDetail(uri: String, key: String) {
    this.navigateSingleTopTo("${DetailPhoto.route}/$uri/$key")
}


fun NavHostController.navigatePopUp(route: String) {
    this.navigate(route) {
        popUpTo(0)
    }
}