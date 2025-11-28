package com.example.test_lab_week_12

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.test_lab_week_12.model.Movie
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private val movieAdapter by lazy {
        MovieAdapter(object : MovieAdapter.MovieClickListener {
            override fun onMovieClick(movie: Movie) {
                openMovieDetails(movie)
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.movie_list)
        recyclerView.adapter = movieAdapter

        // --- BAGIAN BARU DARI MODUL (LANGKAH 9) ---

        // 1. Ambil instance Repository dari Application class
        // Pastikan nama class Application kamu 'MovieApplication' sesuai modul
        val movieRepository = (application as MovieApplication).movieRepository

        // 2. Inisialisasi ViewModel menggunakan Factory
        val movieViewModel = ViewModelProvider(
            this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MovieViewModel(movieRepository) as T
                }
            }
        )[MovieViewModel::class.java]

        // 3. Observe data 'popularMovies'
        movieViewModel.popularMovies.observe(this) { popularMovies ->
            // Ambil tahun saat ini (misal: "2024" atau "2025")
            val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()

            // Masukkan data ke adapter dengan filter & sorting
            movieAdapter.addMovies(
                popularMovies
                    .filter { movie ->
                        // Hanya ambil film yang rilis tahun ini
                        movie.releaseDate?.startsWith(currentYear) == true
                    }
                    .sortedByDescending { it.popularity } // Urutkan dari yang terpopuler [cite: 148]
            )
        }

        // 4. Observe data 'error' untuk menampilkan Snackbar jika gagal
        movieViewModel.error.observe(this) { error ->
            if (error.isNotEmpty()) {
                Snackbar.make(recyclerView, error, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun openMovieDetails(movie: Movie) {
        val intent = Intent(this, DetailsActivity::class.java).apply {
            putExtra(DetailsActivity.EXTRA_TITLE, movie.title)
            putExtra(DetailsActivity.EXTRA_RELEASE, movie.releaseDate)
            putExtra(DetailsActivity.EXTRA_OVERVIEW, movie.overview)
            putExtra(DetailsActivity.EXTRA_POSTER, movie.posterPath)
        }
        startActivity(intent)
    }
}