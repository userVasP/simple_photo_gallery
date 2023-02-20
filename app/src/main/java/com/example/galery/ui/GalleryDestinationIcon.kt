package com.example.galery.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.navArgument

interface GalleryDestinationIcon: GalleryDestination {
    val icon: ImageVector
}

interface GalleryDestination {
    val route: String
}

object Gallery: GalleryDestinationIcon {
    override val icon: ImageVector = Icons.Filled.Home
    override val route: String = "gallery"
}

object Favorite: GalleryDestinationIcon {
    override val icon: ImageVector = Icons.Filled.Favorite
    override val route: String = "favorite"
}

object Server: GalleryDestinationIcon {
    override val icon: ImageVector = Icons.Filled.Cloud
    override val route: String = "server"
}

object Registration: GalleryDestination {
    override val route: String = "registration"
}

object Login: GalleryDestinationIcon {
    override val icon: ImageVector = Icons.Filled.AccountCircle
    override val route: String = "login"
}

object DetailPhoto: GalleryDestination {
    override val route: String = "detail_photo"
    const val uriPhotoArg = "uri_photo"
    const val keyPhotoArg = "key_photo"
    val routeWithArgs = "${route}/{${uriPhotoArg}}/{${keyPhotoArg}}"
    val arguments = listOf(
        navArgument(uriPhotoArg) { type = NavType.StringType },
        navArgument(keyPhotoArg) { type = NavType.StringType }
    )
}

val galleryScreensLogin = listOf(Gallery, Favorite, Server)
val galleryScreensLogout = listOf(Gallery, Favorite, Login)