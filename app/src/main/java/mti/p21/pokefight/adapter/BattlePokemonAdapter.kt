package mti.p21.pokefight.adapter

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mti.p21.pokefight.R
import mti.p21.pokefight.model.SimplifiedPokemonDetails

class BattlePokemonAdapter(
    private val pokemons: List<SimplifiedPokemonDetails>,
    private val context: Context,
    private val resources: Resources,
    private val onClickedPokemonListener: View.OnClickListener
) : RecyclerView.Adapter<BattlePokemonAdapter.ViewHolder>() {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val pokemonImageView : ImageView = itemView.findViewById(R.id.itemBattlePokemon_imageView)
        val pokemonType1ImageView : ImageView = itemView.findViewById(R.id.itemPokemonBattleType1_imageView)
        val pokemonType2ImageView : ImageView = itemView.findViewById(R.id.itemPokemonBattleType2_imageView)
        val pokemonNameTextView : TextView = itemView.findViewById(R.id.itemBattlePokemonName_textView)
        val pokemonHPTextView : TextView = itemView.findViewById(R.id.itemBattlePokemonHPParams_textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowView : View = LayoutInflater.from(context)
                                           .inflate(R.layout.fragment_battle_pokemon_item,
                                                    parent, false)

        rowView.setOnClickListener(onClickedPokemonListener)

        return ViewHolder(rowView)
    }

    override fun getItemCount(): Int {
        return pokemons.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = position

        val pokemon : SimplifiedPokemonDetails = pokemons[position]

        Glide
            .with(context)
            .load(pokemon.sprite)
            .into(holder.pokemonImageView)

        holder.pokemonType1ImageView.setImageResource(pokemon.types[0].getPictureID(resources, context))
        holder.pokemonType2ImageView.setImageResource(
            if (pokemon.types.size > 1) pokemon.types[1].getPictureID(resources, context)
            else 0
        )

        holder.pokemonNameTextView.text = pokemon.name
        holder.pokemonHPTextView.text = pokemon.hp.toString()
    }


}