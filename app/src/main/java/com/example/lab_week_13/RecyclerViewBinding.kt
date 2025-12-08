package com.example.lab_week_13 // Pastikan nama package sesuai

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.lab_week_13.MovieAdapter // Sesuaikan import jika perlu
import com.example.lab_week_13.model.Movie

@BindingAdapter("list")
fun bindMovies(view: RecyclerView, movies: List<Movie>?) {
    val adapter = view.adapter as? MovieAdapter
    adapter?.addMovies(movies ?: emptyList())
}