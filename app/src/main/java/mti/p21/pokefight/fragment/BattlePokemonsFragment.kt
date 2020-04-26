package mti.p21.pokefight.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_battle.*
import kotlinx.android.synthetic.main.fragment_list.*
import mti.p21.pokefight.GameManager
import mti.p21.pokefight.R
import mti.p21.pokefight.adapter.BattlePokemonAdapter
import mti.p21.pokefight.utils.AbstractActivity
import mti.p21.pokefight.utils.AbstractFragment

/**
 * [BattlePokemonsFragment] that represent the fragment to choose the new pokemon to call.
 */
class BattlePokemonsFragment(a: AbstractActivity) : AbstractFragment(a) {

    override val layoutResource = R.layout.fragment_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        GameManager.battleFragment.informations_textView.text =
            getString(R.string.interaction_select_pokemon)

        val onClickedPokemonListener = View.OnClickListener {
            val newPokemonIndex = it.tag as Int

            if (GameManager.currentPokemonIndex != newPokemonIndex &&
                GameManager.team[newPokemonIndex].hp != 0) {


                GameManager.battleFragment.informations_textView.text =
                    getString(R.string.interaction_select_action)

                GameManager.currentPokemonIndex = newPokemonIndex
                GameManager.loadCurrentPokemonInformation()
                mainActivity.chooseAction(true)
            }
        }

        recyclerView_container.layoutManager = LinearLayoutManager(mainActivity)
        recyclerView_container.adapter =
            BattlePokemonAdapter(mainActivity, resources, onClickedPokemonListener)
        recyclerView_container.addItemDecoration(
            DividerItemDecoration(mainActivity, LinearLayoutManager.VERTICAL)
        )
    }
}
