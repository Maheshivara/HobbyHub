package com.br.ifal.hobbyhub.bottombars

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.br.ifal.hobbyhub.navigation.RoutesNames

@Composable
fun MusicBottomBar(onNavigateTo: (RoutesNames) -> Unit) {

    BottomAppBar {
        MusicBottomBarEntriesEnum.entries.map { bottomNavigationItem ->
            NavigationBarItem(
                onClick = {
                    onNavigateTo(bottomNavigationItem.route)
                },
                selected = false,
                icon = {
                    Icon(
                        imageVector = bottomNavigationItem.icon,
                        contentDescription = bottomNavigationItem.label
                    )
                },
                label = {
                    Text(bottomNavigationItem.label)
                }
            )
        }
    }
}