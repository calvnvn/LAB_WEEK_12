package com.example.test_lab_week_12

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test_lab_week_12.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map

class MovieViewModel(private val movieRepository: MovieRepository)
    : ViewModel() {
    init {
        fetchPopularMovies()
    }

    private val _popularMovies = MutableStateFlow(
        emptyList<Movie>()
    )
    val popularMovies: StateFlow<List<Movie>> = _popularMovies

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error
    // fetch movies from the API
    private fun fetchPopularMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            movieRepository.fetchMovies()
                // --- BAGIAN BARU (ASSIGNMENT) ---
                .map { movies ->
                    // 1. Ambil tahun saat ini
                    val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR).toString()

                    // 2. Lakukan Filter & Sorting seperti di Part 1
                    movies.filter { movie ->
                        movie.releaseDate?.startsWith(currentYear) == true
                    }.sortedByDescending { movie ->
                        movie.popularity
                    }
                }
                // --------------------------------
                .catch { exception ->
                    _error.value = "An exception occurred: ${exception.message}"
                }
                .collect { movies ->
                    _popularMovies.value = movies
                }
        }
    }
}
