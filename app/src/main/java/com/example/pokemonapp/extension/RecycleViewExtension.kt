package com.example.pokemon_app.extension


import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


// Adds a pagination scroll listener to a RecyclerView with a LinearLayoutManager
fun RecyclerView.addPaginationScrollListener(
    layoutManager: LinearLayoutManager,
    itemsPerIncrement:Int = 15,
    onLoadMore: () -> Unit
) {
        var totalItemsSeen = 0
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // Get the visible item count and the index of the first visible item
                val visibleItemCount = layoutManager.childCount
                val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
                // Check if we have scrolled far enough to trigger loading more items
                if (visibleItemCount + firstVisibleItem >= totalItemsSeen + itemsPerIncrement) {
                    // Increment the total items seen and trigger the onLoadMore callback
                    totalItemsSeen += itemsPerIncrement
                    onLoadMore()
                }
            }

        })
}

