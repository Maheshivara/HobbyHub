package com.br.ifal.hobbyhub.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesScreen(navController: NavHostController)
{
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Filmes") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        // Interesting, lazy column is faster than normal column.
        LazyColumn(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            items(5) {
                ElementCard()
            }
        }
    }
}

@Composable
fun ElementCard(){
    Card(
        modifier = Modifier.padding(8.dp).fillMaxWidth().height(160.dp),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Add a bit of shading.
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // Box height = card height - 32
            Box(modifier = Modifier.width(96.dp).height(128.dp).background(Color.Blue))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Título: (title)",
                )
                Text(
                    text = "Gênero: (genre)",
                )
                Text(
                    text = "Ano: (year)",
                )
                StarRating()
            }
        }
    }
}

@Composable
fun StarRating(){
    var rating by remember { mutableIntStateOf(0) }
    Row{
        for(i in 1..5) {
            IconButton(onClick = { rating = if (rating == i) 0 else i }) {
                Icon(
                    imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = null,
                    tint = if (i <= rating) Color.Yellow else Color.Gray
                )
            }
        }
    }
}
