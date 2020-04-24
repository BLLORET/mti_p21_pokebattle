package mti.p21.pokefight.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_battle_interaction.*
import mti.p21.pokefight.GameManager
import mti.p21.pokefight.MainActivity
import mti.p21.pokefight.R

/**
 * [BattleInteractionFragment] that represent the fragment on which the player
 * have to chose it next action.
 */
class BattleInteractionFragment(private val enableButtons : Boolean = false) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_battle_interaction, container, false)
    }

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
                (activity as MainActivity).onAttackButtonClicked(gameManager)
            }

            btn_battle_pokemon.setOnClickListener {
                (activity as MainActivity).onPokemonButtonClicked(gameManager)
            }
        }
    }
}
