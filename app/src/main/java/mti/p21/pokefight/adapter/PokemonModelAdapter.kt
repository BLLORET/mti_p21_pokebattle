package mti.p21.pokefight.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mti.p21.pokefight.R
import mti.p21.pokefight.model.PokemonModel

class PokemonModelAdapter(private val data : List<PokemonModel>,
                          private val context : Context)
    : RecyclerView.Adapter<PokemonModelAdapter.ViewHolder>() {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val pokemonNameTextView : TextView = itemView.findViewById(R.id.item_pokemon_name_textView)
        val pokemonType1ImageView : ImageView = itemView.findViewById(R.id.item_type1_imageView)
        val pokemonType2ImageView : ImageView = itemView.findViewById(R.id.item_type2_imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowView : View = LayoutInflater.from(context)
                            .inflate(R.layout.fragment_pokemon_item_list,
                                     parent, false)
        return ViewHolder(rowView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = position
        holder.pokemonNameTextView.text = data[position].name
        //holder.pokemonType1ImageView.setImageResource("@drawable/" + data[position].types[0].getPicture())
    }
}