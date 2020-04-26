package mti.p21.pokefight.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_battle_interaction.*
import mti.p21.pokefight.GameManager
import mti.p21.pokefight.R
import mti.p21.pokefight.utils.AbstractActivity
import mti.p21.pokefight.utils.AbstractFragment

/**
 * [BattleInteractionFragment] that represent the fragment on which the player
 * have to chose it next action.
 */
class BattleInteractionFragment(a: AbstractActivity,
    private val enableButtons: Boolean = false) : AbstractFragment(a) {

    override val layoutResource = R.layout.fragment_battle_interaction

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Case when we come from pokemon battle choice
        if (enableButtons) {
            val informationText: TextView = activity!!.findViewById(R.id.informations_textView)
            informationText.text = getString(R.string.interaction_select_action)
        }

        btn_battle_attack.isEnabled = enableButtons
        btn_battle_pokemon.isEnabled = enableButtons

        arguments?.let {
            val gameManager = it.getSerializable("GameManager") as GameManager

            btn_battle_attack.setOnClickListener {
                mainActivity.onAttackButtonClicked(gameManager)
            }

            btn_battle_pokemon.setOnClickListener {
                mainActivity.onPokemonButtonClicked(gameManager)
            }
        }
    }
}
