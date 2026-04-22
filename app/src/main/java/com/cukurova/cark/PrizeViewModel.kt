package com.cukurova.cark

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class Prize(val id: Long, val name: String)

class PrizeViewModel : ViewModel() {
    private val _prizes = MutableLiveData<List<Prize>>(emptyList())
    val prizes: LiveData<List<Prize>> = _prizes

    fun addPrize(name: String) {
        val currentList = _prizes.value ?: emptyList()
        val newPrize = Prize(System.currentTimeMillis(), name)
        _prizes.value = currentList + newPrize
    }

    fun removePrize(prize: Prize) {
        val currentList = _prizes.value ?: emptyList()
        _prizes.value = currentList.filter { it.id != prize.id }
    }
}