package mti.p21.pokefight.adapter

import android.content.Context
import android.content.res.Resources
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_battle_move_item.view.*
import mti.p21.pokefight.R
import mti.p21.pokefight.model.MoveModel

/**
 * Represent the adapter to display moves in a recycler view.
 */
class BattleMovesAdapter(
    private val moves: List<MoveModel>,
    private val context: Context,
    private val resources: Resources,
    private  val onItemClickListener: View.OnClickListener
) : RecyclerView.Adapter<BattleMovesAdapter.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val moveNameTextView: TextView = itemView.itemBattleMoveName_textView
        val movePowerTextView: TextView = itemView.itemBattleMovePower_textView
        val moveTypeImageView: ImageView = itemView.itemBattleMoveType_imageView
        val moveCategoryImageView: ImageView = itemView.itemBattleMoveCategoryAttack
        val movePrecisionTextView: TextView = itemView.itembattleMovePrecision_textView
    }

    /**
     * Called when the ViewHolder is created to create the RowView.
     * @param parent : The ViewGroup on which attach the ViewHolder
     * @return Returns the ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowView : View =
            LayoutInflater.from(context)
                .inflate(R.layout.fragment_battle_move_item, parent, false)
        rowView.setOnClickListener(onItemClickListener)

        return ViewHolder(rowView)
    }

    /**
     * Get the number of items
     * @return Returns the size of the list
     */
    override fun getItemCount(): Int {
        return moves.size
    }

    /**
     * Called to display items in the RecyclerView.
     * @param holder : Represent the viewHolder
     * @param position : The position of the item to display
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = position

        val move: MoveModel = moves[position]

        holder.moveNameTextView.text = move.name
        holder.movePowerTextView.text = move.power.toString()
        holder.moveTypeImageView.setImageResource(move.type.getPictureID(resources, context))
        holder.movePrecisionTextView.text = move.accuracy.toString()
        holder.moveCategoryImageView.setImageResource(
            if (move.damage_class.name == "physical") R.drawable.physical else R.drawable.special
        )
    }
}