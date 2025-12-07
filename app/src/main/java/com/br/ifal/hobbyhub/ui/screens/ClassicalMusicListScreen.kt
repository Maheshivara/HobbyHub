package com.br.ifal.hobbyhub.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.br.ifal.hobbyhub.R
import com.br.ifal.hobbyhub.models.ClassicalMusicEntity
import com.br.ifal.hobbyhub.ui.viewmodel.ClassicalMusicViewModel

@Composable
fun ClassicalMusicListScreen(viewModel: ClassicalMusicViewModel) {

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(modifier = Modifier) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(uiState.workList.size) { index ->
                val work = uiState.workList[index]
                ClassicalMusicListItem(work = work, onClickRate = { updatedWork ->
                    viewModel.upsertWork(updatedWork)
                })
            }
        }
    }
}

@Composable
fun ClassicalMusicListItem(
    work: ClassicalMusicEntity,
    onClickRate: (work: ClassicalMusicEntity) -> Unit
) {

    Card(modifier = Modifier.fillMaxWidth()) {

        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = work.composerPicture,
                contentDescription = "Composer Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(10.dp)),
                placeholder = painterResource(R.drawable.ic_classical_music),
                error = painterResource(R.drawable.ic_classical_music)
            )
            Column {
                Text(text = "Título: ${work.title}", modifier = Modifier.padding(8.dp))
                Text(text = "Compositor: ${work.composerName}", modifier = Modifier.padding(8.dp))
                Text(text = "Gênero: ${work.genre}", modifier = Modifier.padding(8.dp))
                RatingStars(
                    rating = work.rating,
                    onClickRate = { rating ->
                        val updatedWork = work.copy(rating = rating)
                        onClickRate(updatedWork)
                    }
                )
            }
        }
    }
}

@Composable
fun RatingStars(rating: Int, onClickRate: (Int) -> Unit) {
    Row {
        for (i in 1..5) {
            Surface(
                onClick = { onClickRate(i) },
                color = Color.Transparent
            ) {
                Icon(
                    imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = if (i <= rating) "Filled Star" else "Empty Star",
                    modifier = Modifier.padding(end = 2.dp)
                )
            }
        }
    }
}