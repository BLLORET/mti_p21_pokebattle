package mti.p21.pokefight.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_battle.*
import kotlinx.android.synthetic.main.fragment_list.*
import mti.p21.pokefight.GameManager
import mti.p21.pokefight.R
import mti.p21.pokefight.adapter.BattleMovesAdapter
import mti.p21.pokefight.utils.AbstractActivity
import mti.p21.pokefight.utils.AbstractFragment

/**
 * [BattleMovesFragment] that represent the fragment on which the player can choose it's action.
 */
class BattleMovesFragment(a: AbstractActivity) : AbstractFragment(a) {

    override val layoutResource = R.layout.fragment_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val informationTextView: TextView = mainActivity.informations_textView
        informationTextView.text = getString(R.string.interaction_select_move)

        val moves = GameManager.currentPokemon.moves

        val onClickedMoveListener = View.OnClickListener {
            GameManager.battleTurn(moves[it.tag as Int])
            mainActivity.chooseAction(false)
        }

        recyclerView_container.layoutManager = LinearLayoutManager(mainActivity)
        recyclerView_container.adapter =
            BattleMovesAdapter(moves, mainActivity, resources, onClickedMoveListener)
        recyclerView_container.addItemDecoration(
            DividerItemDecoration(activity, LinearLayoutManager.VERTICAL)
        )
    }
}
