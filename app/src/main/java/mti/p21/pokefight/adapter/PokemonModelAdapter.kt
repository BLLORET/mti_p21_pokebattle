package mti.p21.pokefight.adapter

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_pokemon_item_list.view.*
import mti.p21.pokefight.R
import mti.p21.pokefight.model.PokemonModel

/**
 * [PokemonModelAdapter] Represent the adapter that fill the RecyclerView of list of pokemons.
 * @param data : The list of pokemon
 * @param context : The context of the adapter
 */
class PokemonModelAdapter(
    private val data: List<PokemonModel>,
    private val context: Context,
    private  val resource: Resources,
    private  val onItemClickListener: View.OnClickListener
) : RecyclerView.Adapter<PokemonModelAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pokemonNameTextView: TextView = itemView.item_pokemon_name_textView
        val pokemonType1ImageView: ImageView = itemView.item_type1_imageView
        val pokemonType2ImageView: ImageView = itemView.item_type2_imageView
    }

    /**
     * Called when the ViewHolder is created to create the RowView.
     * @param parent : The ViewGroup on which attach the ViewHolder
     * @return Returns the ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowView : View = LayoutInflater.from(context)
            .inflate(R.layout.fragment_pokemon_item_list, parent, false)
        rowView.setOnClickListener(onItemClickListener)

        return ViewHolder(rowView)
    }

    /**
     * Get the number of items
     * @return Returns the size of the list
     */
    override fun getItemCount(): Int {
        return data.size
    }

    /**
     * Called to display items in the RecyclerView.
     * @param holder : Represent the viewHolder
     * @param position : The position of the item to display
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = position
        val pokemon: PokemonModel = data[position]

        holder.pokemonNameTextView.text = pokemon.name

        holder.pokemonType1ImageView.setImageResource(
            pokemon.types[0].getPictureID(resource, context)
        )
        holder.pokemonType2ImageView.setImageResource(
            if (pokemon.types.size > 1)
                pokemon.types[1].getPictureID(resource, context)
            else
                0
        )
    }
}