package com.example.pokemonapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.domain.model.Pokemon
import com.example.pokemonapp.databinding.ItemPokemonBinding

class PokemonsAdapter(
    context: Context,
    private val onItemClicked: (Pokemon) -> Unit
): ListAdapter<Pokemon, PokemonViewHolder>(
   DIFF_UTIL
) {
    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        return PokemonViewHolder(
            binding = ItemPokemonBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it,onItemClicked)
        }
    }

    companion object {
        // DiffUtil callback to efficiently update the RecyclerView items
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<Pokemon>() {
            override fun areItemsTheSame(oldItem: Pokemon, newItem: Pokemon): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Pokemon, newItem: Pokemon): Boolean {
                return oldItem == newItem
            }
        }
    }
}

class PokemonViewHolder(
    private val binding: ItemPokemonBinding
) : RecyclerView.ViewHolder(binding.root) {
    // Binds a pokemon item to the view holder's views and sets click listener
    fun bind(item: Pokemon, onItemClicked: (Pokemon) -> Unit) {
        binding.imagePokemon.load(item.image)
        binding.pokemonName.text = item.name
        itemView.setOnClickListener {
            onItemClicked(item)
        }
    }
}