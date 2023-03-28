package com.francis.searchapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

class MainViewModel: ViewModel(){

    // ViewModel can change it but not the UI
    private val _searchText = MutableStateFlow("")

    // to be changed by UI
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _persons = MutableStateFlow(allPersons)
    val persons = searchText
        .debounce(1000L)
        .onEach { _isSearching.update { true } }
        .combine(_persons){text, persons ->
            if (text.isBlank()){
                persons
            }else{
                // Simulate a network call
                delay(2000L)
                persons.filter {
                    it.doesMatchSearchQuery(text)
                }
            }
        }
        .onEach { _isSearching.update { false } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _persons.value
        )

    fun onSearchTextChange(text: String){
        _searchText.value = text
    }

}

data class Person(
    val firstName: String,
    val lastName: String
){

    fun doesMatchSearchQuery(query: String): Boolean{
        val matchingCombination = listOf(
            "$firstName$lastName",
            "$firstName $lastName",
            "${firstName.first()} ${lastName.first()}"
        )

        return matchingCombination.any {
            it.contains(query, ignoreCase = true)
        }
    }
}

private val allPersons = listOf(
    Person(
        firstName = "Francis",
        lastName = "Waithaka"
    ),
    Person(
        firstName = "Johnny",
        lastName = "Blue"
    ),
    Person(
        firstName = "Black",
        lastName = "Jack"
    ),
    Person(
        firstName = "Tonny",
        lastName = "Malcom"
    ),
    Person(
        firstName = "Anita",
        lastName = "Johns"
    ),
    Person(
        firstName = "Beauty",
        lastName = "Betty"
    ),
    Person(
        firstName = "Francis",
        lastName = "Mwangi"
    ),
    Person(
        firstName = "Johnny",
        lastName = "Mwangi"
    ),



)