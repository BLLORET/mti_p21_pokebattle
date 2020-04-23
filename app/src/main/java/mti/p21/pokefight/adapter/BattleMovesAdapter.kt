package mti.p21.pokefight.adapter

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mti.p21.pokefight.R
import mti.p21.pokefight.model.MoveModel

class BattleMovesAdapter(
    private val moves : List<MoveModel>,
    private val context : Context,
    private val resources: Resources,
    private  val onItemClickListener: View.OnClickListener
) : RecyclerView.Adapter<BattleMovesAdapter.ViewHolder>() {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val moveNameTextView : TextView = itemView.findViewById(R.id.itemBattleMoveName_textView)
        val movePowerTextView : TextView = itemView.findViewById(R.id.itemBattleMovePower_textView)
        val moveTypeImageView : ImageView = itemView.findViewById(R.id.itemBattleMoveType_imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowView : View = LayoutInflater.from(context)
                                           .inflate(R.layout.fragment_battle_move_item,
                                                    parent, false)
        rowView.setOnClickListener(onItemClickListener)

        return ViewHolder(rowView)
    }

    override fun getItemCount(): Int {
        return moves.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = position

        val move : MoveModel = moves[position]

        holder.moveNameTextView.text = move.name
        holder.movePowerTextView.text = move.power.toString()
        holder.moveTypeImageView.setImageResource(move.type.getPictureID(resources, context))
    }
}