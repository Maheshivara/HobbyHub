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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.br.ifal.hobbyhub.R
import com.br.ifal.hobbyhub.db.DatabaseHelper
import com.br.ifal.hobbyhub.models.ClassicalMusicEntity
import com.br.ifal.hobbyhub.network.RetrofitProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ClassicalMusicListScreen(navController: NavHostController) {
    val openOpusApi = RetrofitProvider.openOpusApi
    val classicDao = DatabaseHelper.getInstance(LocalContext.current).classicalDao()

    var works by remember {
        mutableStateOf<List<ClassicalMusicEntity>>(emptyList<ClassicalMusicEntity>())
    }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val savedClassic = classicDao.getAllMusic()
            var notSavedClassic: List<ClassicalMusicEntity> = emptyList()
            val response = openOpusApi.getRandomWorks()
            if (response.isSuccessful) {
                val workListResponse = response.body()
                if (workListResponse != null) {
                    val composersId = workListResponse.works.map { it.composer.id }.toSet()
                    val composersIdString = composersId.joinToString(",")

                    val composersResponse = openOpusApi.getComposersByIds(composersIdString)
                    if (composersResponse.isSuccessful) {
                        val composersListResponse = composersResponse.body()
                        if (composersListResponse != null) {
                            val composersMap = composersListResponse.composers.associateBy { it.id }
                            val worksWithComposers = workListResponse.works.map { work ->
                                val composer = composersMap[work.composer.id]
                                ClassicalMusicEntity(
                                    id = work.id,
                                    title = work.title,
                                    composerName = composer?.fullName ?: "Unknown Composer",
                                    composerPicture = composer?.portrait,
                                    genre = work.genre
                                )
                            }
                            notSavedClassic = worksWithComposers.filter { work ->
                                savedClassic.none { it.id == work.id }
                            }
                        }
                    }
                }
            }
            works = savedClassic + notSavedClassic
        }
    }

    Scaffold(modifier = Modifier) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(works.size) { index ->
                val work = works[index]
                ClassicalMusicListItem(work = work)
            }
        }
    }
}

@Composable
fun ClassicalMusicListItem(work: ClassicalMusicEntity) {
    val classicDao = DatabaseHelper.getInstance(LocalContext.current).classicalDao()

    Card(modifier = Modifier.fillMaxWidth()) {
        var myRating by remember { mutableIntStateOf(work.rating) }

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
                    myRating,
                    { newRating ->
                        myRating = newRating
                        work.rating = newRating
                        CoroutineScope(Dispatchers.IO).launch {
                            classicDao.upsertMusic(work)
                        }
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