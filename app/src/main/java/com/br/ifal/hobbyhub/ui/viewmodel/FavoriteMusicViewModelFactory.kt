package com.br.ifal.hobbyhub.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.br.ifal.hobbyhub.db.DatabaseHelper
import com.br.ifal.hobbyhub.network.RetrofitProvider
import com.br.ifal.hobbyhub.repositories.MusicRepository

class FavoriteMusicViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val dao = DatabaseHelper.getInstance(context).musicDao()
        val api = RetrofitProvider.deezerApi
        val repo = MusicRepository(dao, api)
        @Suppress("UNCHECKED_CAST")
        return FavoriteMusicViewModel(repo) as T
    }
}