package mti.p21.pokefight.fragment

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_battle_interaction.*
import mti.p21.pokefight.R
import mti.p21.pokefight.utils.AbstractActivity
import mti.p21.pokefight.utils.AbstractFragment

/**
 * [BattleInteractionFragment] that represent the fragment on which the player
 * have to chose it next action.
 */
class BattleInteractionFragment(a: AbstractActivity, val enable: Boolean) : AbstractFragment(a) {

    override val layoutResource = R.layout.fragment_battle_interaction

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_battle_attack.isEnabled = enable
        btn_battle_pokemon.isEnabled = enable

        btn_battle_attack.setOnClickListener { mainActivity.onAttackButtonClicked() }
        btn_battle_pokemon.setOnClickListener { mainActivity.onPokemonButtonClicked() }
    }

    fun buttons(enable: Boolean) {
        btn_battle_attack.isEnabled = enable
        btn_battle_pokemon.isEnabled = enable
    }
}
