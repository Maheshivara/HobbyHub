package com.br.ifal.hobbyhub.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.br.ifal.hobbyhub.models.Movie
import com.br.ifal.hobbyhub.viewmodel.MovieViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesScreen(navController: NavHostController, movieViewModel: MovieViewModel = viewModel()) {
    val movies by movieViewModel.movies.collectAsState()
    val ratings by movieViewModel.ratings.collectAsState()

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
        LazyColumn(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            items(movies) { movie ->
                ElementCard(movie = movie, rating = ratings[movie.id] ?: 0) {
                    movieViewModel.updateRating(movie.id, it)
                }
            }
        }
    }
}

@Composable
fun ElementCard(movie: Movie, rating: Int, onRatingChange: (Int) -> Unit){
    Card(
        modifier = Modifier.padding(8.dp).fillMaxWidth().height(160.dp),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w500/${movie.posterPath}",
                contentDescription = movie.title,
                modifier = Modifier.width(96.dp).height(128.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = movie.title,
                )
                Text(
                    text = "Lançamento: ${movie.releaseDate}",
                )
                StarRating(rating = rating, onRatingChange = onRatingChange)
            }
        }
    }
}

@Composable
fun StarRating(rating: Int, onRatingChange: (Int) -> Unit){
    Row{
        for(i in 1..5) {
            IconButton(onClick = { onRatingChange(if (rating == i) 0 else i) }) {
                Icon(
                    imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = null,
                    tint = if (i <= rating) Color.Yellow else Color.Gray
                )
            }
        }
    }
}
