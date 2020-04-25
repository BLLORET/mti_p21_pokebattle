package mti.p21.pokefight.adapter

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import mti.p21.pokefight.R
import mti.p21.pokefight.model.PokeType

/**
 * Represent the adapter that fill the recycler view of types.
 */
class HelpTypeAdapter(
    private val pokeTypes : List<PokeType>,
    private val context: Context,
    private val resources: Resources
) : RecyclerView.Adapter<HelpTypeAdapter.ViewHolder>() {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val pokeTypeImageView : ImageView = itemView.findViewById(R.id.helpItemType_imageView)
    }

    /**
     * Called when the ViewHolder is created to create the RowView.
     * @param parent : The ViewGroup on which attach the ViewHolder
     * @return Returns the ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val typeView : View = LayoutInflater.from(context)
                                            .inflate(R.layout.fragment_help_item_type,
                                                     parent, false)
        return ViewHolder(typeView)
    }

    /**
     * Get the number of items
     * @return Returns the size of the list
     */
    override fun getItemCount(): Int {
        return pokeTypes.size
    }

    /**
     * Called to display items in the RecyclerView.
     * @param holder : Represent the viewHolder
     * @param position : The position of the item to display
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = position
        holder.pokeTypeImageView.setImageResource(
            pokeTypes[position].getPictureID(resources, context)
        )
    }
}