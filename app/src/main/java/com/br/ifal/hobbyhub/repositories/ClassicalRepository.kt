package com.br.ifal.hobbyhub.repositories

import com.br.ifal.hobbyhub.db.ClassicalDao
import com.br.ifal.hobbyhub.models.ClassicalMusicEntity
import com.br.ifal.hobbyhub.network.OpenOpusApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClassicalRepository @Inject constructor(
    private val classicalDao: ClassicalDao,
    private val openOpusApi: OpenOpusApi
) {
    suspend fun getWorks(): List<ClassicalMusicEntity> {
        val savedWorks = getSavedWorks().sortedByDescending { it.rating }
        val randomWorks = getRandomWorks()
        val newWorks = randomWorks.filter { work ->
            savedWorks.none { it.id == work.id }
        }
        return savedWorks + newWorks
    }

    private suspend fun getRandomWorks(): List<ClassicalMusicEntity> {
        val response = openOpusApi.getRandomWorks()
        if (response.isSuccessful) {
            val workListResponse = response.body()
            if (workListResponse != null) {
                val composersId = workListResponse.works.map { it.composer.id }.toSet()
                val composersPicturesMap = getComposersPictures(composersId.toList())
                return workListResponse.works.map { work ->
                    ClassicalMusicEntity(
                        id = work.id,
                        title = work.title,
                        composerName = work.composer.fullName,
                        composerPicture = composersPicturesMap[work.composer.id] ?: "",
                        genre = work.genre
                    )
                }
            }
        }
        return emptyList()
    }

    private suspend fun getComposersPictures(composersIdList: List<Int>): Map<Int, String> {
        val composersIdString = composersIdList.joinToString(",")
        val composersResponse = openOpusApi.getComposersByIds(composersIdString)
        if (composersResponse.isSuccessful) {
            val body = composersResponse.body()
            if (body != null) {
                return body.composers.associate { it.id to (it.portrait ?: "") }
            }
        }
        return emptyMap()
    }

    suspend fun upsertWork(work: ClassicalMusicEntity) {
        classicalDao.upsertMusic(work)
    }

    private suspend fun getSavedWorks(): List<ClassicalMusicEntity> {
        return classicalDao.getAllMusic()
    }
}