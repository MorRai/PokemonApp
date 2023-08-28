package com.example.pokemon_app.extension


import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


// Adds a pagination scroll listener to a RecyclerView with a LinearLayoutManager
fun RecyclerView.addPaginationScrollListener(
    layoutManager: LinearLayoutManager,
    itemsToLoad: Int,
    onLoadMore: () -> Unit
) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {


        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)


             val totalItemCount = layoutManager.itemCount
            // Gets the position of the last visible item in the RecyclerView
            val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
            // Triggers the onLoadMore function when the user scrolls close to the end of the list
           if (dy != 0 && totalItemCount <= (lastVisibleItem + itemsToLoad)) {
                onLoadMore()
            }

        }
    })
}

