package mti.p21.pokefight.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_list.*
import mti.p21.pokefight.GameManager
import mti.p21.pokefight.MainActivity
import mti.p21.pokefight.R
import mti.p21.pokefight.adapter.BattleMovesAdapter

/**
 * [BattleMovesFragment] that represent the fragment on which the player can choose it's action.
 */
class BattleMovesFragment() : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val informationTextView: TextView = activity!!.findViewById(R.id.informations_textView)
        informationTextView.text = getString(R.string.interaction_select_move)

        arguments?.let { bundle ->
            val gameManager = bundle.getSerializable("GameManager") as GameManager
            val moves = gameManager.currentPokemon.moves

            val onClickedMoveListener = View.OnClickListener {
                gameManager.battleTurn(moves[it.tag as Int], activity!!)
                (activity as MainActivity).chooseAction(gameManager)
            }

            recyclerView_container.layoutManager = LinearLayoutManager(activity)
            recyclerView_container.adapter = BattleMovesAdapter(moves, activity!!, resources,
                                                                onClickedMoveListener)
            recyclerView_container.addItemDecoration(
                DividerItemDecoration(
                    activity,
                    LinearLayoutManager.VERTICAL
                )
            )
        }
    }
}
