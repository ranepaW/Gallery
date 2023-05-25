/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package com.dot.gallery.core.presentation.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.dot.gallery.R
import com.dot.gallery.core.Constants
import com.dot.gallery.core.Constants.Target.TARGET_FAVORITES
import com.dot.gallery.core.Constants.Target.TARGET_TRASH
import com.dot.gallery.feature_node.presentation.ChanneledViewModel
import com.dot.gallery.feature_node.presentation.MediaViewModel
import com.dot.gallery.feature_node.presentation.albums.AlbumsScreen
import com.dot.gallery.feature_node.presentation.albums.AlbumsViewModel
import com.dot.gallery.feature_node.presentation.library.LibraryScreen
import com.dot.gallery.feature_node.presentation.library.favorites.FavoriteScreen
import com.dot.gallery.feature_node.presentation.library.trashed.TrashedGridScreen
import com.dot.gallery.feature_node.presentation.mediaview.MediaViewScreen
import com.dot.gallery.feature_node.presentation.settings.SettingsScreen
import com.dot.gallery.feature_node.presentation.settings.SettingsViewModel
import com.dot.gallery.feature_node.presentation.timeline.TimelineScreen
import com.dot.gallery.feature_node.presentation.util.Screen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavigationComp(
    navController: NavHostController,
    paddingValues: PaddingValues,
    bottomBarState: MutableState<Boolean>,
    systemBarFollowThemeState: MutableState<Boolean>,
    windowSizeClass: WindowSizeClass
) {
    val useNavRail = windowSizeClass.widthSizeClass > WindowWidthSizeClass.Compact
    val bottomNavEntries = rememberNavigationItems()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    navBackStackEntry?.destination?.route?.let {
        val shouldDisplayBottomBar = bottomNavEntries.find { item -> item.route == it } != null
        bottomBarState.value = shouldDisplayBottomBar
        systemBarFollowThemeState.value = !it.contains(Screen.MediaViewScreen.route)
    }
    val navPipe = hiltViewModel<ChanneledViewModel>()
    navPipe
        .initWithNav(navController, bottomBarState)
        .collectAsStateWithLifecycle(LocalLifecycleOwner.current)
    AnimatedNavHost(
        navController = navController,
        startDestination = Screen.TimelineScreen.route
    ) {
        composable(
            route = Screen.TimelineScreen.route,
            enterTransition = { Constants.Animation.navigateInAnimation },
            exitTransition = { Constants.Animation.navigateUpAnimation },
            popEnterTransition = { Constants.Animation.navigateInAnimation },
            popExitTransition = { Constants.Animation.navigateUpAnimation }
        ) {
            val viewModel =
                hiltViewModel<MediaViewModel>().apply(MediaViewModel::launchInPhotosScreen)

            TimelineScreen(
                paddingValues = paddingValues,
                retrieveMedia = viewModel::launchInPhotosScreen,
                handler = viewModel.handler,
                mediaState = viewModel.photoState,
                selectionState = viewModel.multiSelectState,
                selectedMedia = viewModel.selectedPhotoState,
                toggleSelection = viewModel::toggleSelection,
                allowNavBar = !useNavRail,
                navigate = navPipe::navigate,
                navigateUp = navPipe::navigateUp,
                toggleNavbar = navPipe::toggleNavbar
            )
        }
        composable(
            route = Screen.TrashedScreen.route,
            enterTransition = { Constants.Animation.navigateInAnimation },
            exitTransition = { Constants.Animation.navigateUpAnimation },
            popEnterTransition = { Constants.Animation.navigateInAnimation },
            popExitTransition = { Constants.Animation.navigateUpAnimation }
        ) {
            val viewModel = hiltViewModel<MediaViewModel>().apply { target = TARGET_TRASH }
            TrashedGridScreen(
                paddingValues = paddingValues,
                mediaState = viewModel.photoState,
                selectionState = viewModel.multiSelectState,
                selectedMedia = viewModel.selectedPhotoState,
                handler = viewModel.handler,
                toggleSelection = viewModel::toggleSelection,
                navigate = navPipe::navigate,
                navigateUp = navPipe::navigateUp,
                toggleNavbar = navPipe::toggleNavbar
            )
        }
        composable(
            route = Screen.FavoriteScreen.route,
            enterTransition = { Constants.Animation.navigateInAnimation },
            exitTransition = { Constants.Animation.navigateUpAnimation },
            popEnterTransition = { Constants.Animation.navigateInAnimation },
            popExitTransition = { Constants.Animation.navigateUpAnimation }
        ) {
            val viewModel = hiltViewModel<MediaViewModel>().apply { target = TARGET_FAVORITES }
            FavoriteScreen(
                paddingValues = paddingValues,
                mediaState = viewModel.photoState,
                handler = viewModel.handler,
                selectionState = viewModel.multiSelectState,
                selectedMedia = viewModel.selectedPhotoState,
                toggleFavorite = viewModel::toggleFavorite,
                toggleSelection = viewModel::toggleSelection,
                navigate = navPipe::navigate,
                navigateUp = navPipe::navigateUp,
                toggleNavbar = navPipe::toggleNavbar
            )
        }
        composable(
            route = Screen.LibraryScreen.route,
            enterTransition = { Constants.Animation.navigateInAnimation },
            exitTransition = { Constants.Animation.navigateUpAnimation },
            popEnterTransition = { Constants.Animation.navigateInAnimation },
            popExitTransition = { Constants.Animation.navigateUpAnimation }
        ) {
            LibraryScreen(
                navController = navController
            )
        }
        composable(
            route = Screen.AlbumsScreen.route,
            enterTransition = { Constants.Animation.navigateInAnimation },
            exitTransition = { Constants.Animation.navigateUpAnimation },
            popEnterTransition = { Constants.Animation.navigateInAnimation },
            popExitTransition = { Constants.Animation.navigateUpAnimation }
        ) {
            val viewModel = hiltViewModel<AlbumsViewModel>()
            AlbumsScreen(
                navigate = navPipe::navigate,
                toggleNavbar = navPipe::toggleNavbar,
                paddingValues = paddingValues,
                viewModel = viewModel
            )
        }
        composable(
            route = Screen.AlbumViewScreen.route +
                    "?albumId={albumId}&albumName={albumName}",
            enterTransition = { Constants.Animation.navigateInAnimation },
            exitTransition = { Constants.Animation.navigateUpAnimation },
            popEnterTransition = { Constants.Animation.navigateInAnimation },
            popExitTransition = { Constants.Animation.navigateUpAnimation },
            arguments = listOf(
                navArgument(name = "albumId") {
                    type = NavType.LongType
                    defaultValue = -1
                },
                navArgument(name = "albumName") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val argumentAlbumName = backStackEntry.arguments?.getString("albumName")
                ?: stringResource(id = R.string.app_name)
            val argumentAlbumId = backStackEntry.arguments?.getLong("albumId") ?: -1
            val viewModel: MediaViewModel = hiltViewModel<MediaViewModel>().apply {
                albumId = argumentAlbumId
            }
            TimelineScreen(
                paddingValues = paddingValues,
                albumId = argumentAlbumId,
                albumName = argumentAlbumName,
                handler = viewModel.handler,
                mediaState = viewModel.photoState,
                selectionState = viewModel.multiSelectState,
                selectedMedia = viewModel.selectedPhotoState,
                toggleSelection = viewModel::toggleSelection,
                allowNavBar = false,
                navigate = navPipe::navigate,
                navigateUp = navPipe::navigateUp,
                toggleNavbar = navPipe::toggleNavbar
            )
        }
        composable(
            route = Screen.MediaViewScreen.route +
                    "?mediaId={mediaId}&albumId={albumId}",
            enterTransition = { Constants.Animation.navigateInAnimation },
            exitTransition = { Constants.Animation.navigateUpAnimation },
            popEnterTransition = { Constants.Animation.navigateInAnimation },
            popExitTransition = { Constants.Animation.navigateUpAnimation },
            arguments = listOf(
                navArgument(name = "mediaId") {
                    type = NavType.LongType
                    defaultValue = -1
                },
                navArgument(name = "albumId") {
                    type = NavType.LongType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val mediaId: Long = backStackEntry.arguments?.getLong("mediaId") ?: -1
            val albumId: Long = backStackEntry.arguments?.getLong("albumId") ?: -1
            val entryName =
                if (albumId == -1L) Screen.TimelineScreen.route else Screen.AlbumViewScreen.route
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(entryName)
            }
            val viewModel = hiltViewModel<MediaViewModel>(parentEntry)
            MediaViewScreen(
                paddingValues = paddingValues,
                mediaId = mediaId,
                mediaState = viewModel.photoState,
                handler = viewModel.handler,
                navigateUp = navPipe::navigateUp
            )
        }
        composable(
            route = Screen.MediaViewScreen.route +
                    "?mediaId={mediaId}&target={target}",
            enterTransition = { Constants.Animation.navigateInAnimation },
            exitTransition = { Constants.Animation.navigateUpAnimation },
            popEnterTransition = { Constants.Animation.navigateInAnimation },
            popExitTransition = { Constants.Animation.navigateUpAnimation },
            arguments = listOf(
                navArgument(name = "mediaId") {
                    type = NavType.LongType
                    defaultValue = -1
                },
                navArgument(name = "target") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val mediaId: Long = backStackEntry.arguments?.getLong("mediaId") ?: -1
            val target: String? = backStackEntry.arguments?.getString("target")
            val entryName = when (target) {
                TARGET_FAVORITES -> Screen.FavoriteScreen.route
                TARGET_TRASH -> Screen.TrashedScreen.route
                else -> Screen.TimelineScreen.route
            }
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(entryName)
            }
            val viewModel = hiltViewModel<MediaViewModel>(parentEntry)
            MediaViewScreen(
                paddingValues = paddingValues,
                mediaId = mediaId,
                target = target,
                mediaState = viewModel.photoState,
                handler = viewModel.handler,
                navigateUp = navPipe::navigateUp
            )
        }
        composable(
            route = Screen.SettingsScreen.route,
            enterTransition = { Constants.Animation.navigateInAnimation },
            exitTransition = { Constants.Animation.navigateUpAnimation },
            popEnterTransition = { Constants.Animation.navigateInAnimation },
            popExitTransition = { Constants.Animation.navigateUpAnimation },
        ) {
            val viewModel = hiltViewModel<SettingsViewModel>()
            SettingsScreen(
                navigateUp = navPipe::navigateUp,
                viewModel = viewModel
            )
        }
    }
}