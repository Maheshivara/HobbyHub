package com.br.ifal.hobbyhub.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class RoutesNames() {
    @Serializable
    object HomeScreen : RoutesNames()

    @Serializable
    object MusicSearchScreen : RoutesNames()

    @Serializable
    object FavoriteMusicScreen : RoutesNames()

    @Serializable
    object ClassicalMusicListScreen : RoutesNames()
}