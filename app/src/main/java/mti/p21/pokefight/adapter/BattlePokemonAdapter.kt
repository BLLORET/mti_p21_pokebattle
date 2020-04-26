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
import kotlinx.android.synthetic.main.fragment_battle_pokemon_item.view.*
import mti.p21.pokefight.R
import mti.p21.pokefight.model.SimplifiedPokemonDetails

/**
 * Represent an adapter to display pokemon informations to a recycler view.
 */
class BattlePokemonAdapter(
    private val pokemons: List<SimplifiedPokemonDetails>,
    private val context: Context,
    private val resources: Resources,
    private val onClickedPokemonListener: View.OnClickListener
) : RecyclerView.Adapter<BattlePokemonAdapter.ViewHolder>() {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val pokemonImageView: ImageView = itemView.itemBattlePokemon_imageView
        val pokemonType1ImageView: ImageView = itemView.itemPokemonBattleType1_imageView
        val pokemonType2ImageView: ImageView = itemView.itemPokemonBattleType2_imageView
        val pokemonNameTextView: TextView = itemView.itemBattlePokemonName_textView
        val pokemonHPTextView: TextView = itemView.itemBattlePokemonHPParams_textView
    }

    /**
     * Called when the ViewHolder is created to create the RowView.
     * @param parent : The ViewGroup on which attach the ViewHolder
     * @return Returns the ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowView : View =
            LayoutInflater.from(context)
                .inflate(R.layout.fragment_battle_pokemon_item, parent, false)

        rowView.setOnClickListener(onClickedPokemonListener)

        return ViewHolder(rowView)
    }

    /**
     * Get the number of items
     * @return Returns the size of the list
     */
    override fun getItemCount(): Int {
        return pokemons.size
    }

    /**
     * Called to display items in the RecyclerView.
     * @param holder : Represent the viewHolder
     * @param position : The position of the item to display
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = position

        val pokemon = pokemons[position]

        Glide
            .with(context)
            .load(pokemon.sprite)
            .into(holder.pokemonImageView)

        holder.pokemonType1ImageView
            .setImageResource(pokemon.types[0].getPictureID(resources, context))
        holder.pokemonType2ImageView
            .setImageResource(
                if (pokemon.types.size > 1)
                    pokemon.types[1].getPictureID(resources, context)
                else
                    0
            )

        holder.pokemonNameTextView.text = pokemon.name
        holder.pokemonHPTextView.text = pokemon.hp.toString()
    }
}
