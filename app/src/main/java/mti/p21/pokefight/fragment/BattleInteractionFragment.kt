package mti.p21.pokefight.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_battle_interaction.*
import mti.p21.pokefight.MainActivity

import mti.p21.pokefight.R
import mti.p21.pokefight.model.MoveModel
import mti.p21.pokefight.model.SimplifiedPokemonDetails

/**
 * A simple [Fragment] subclass.
 */
class BattleInteractionFragment(private val pokemons: List<SimplifiedPokemonDetails>) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_battle_interaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val position = it.getInt("Position")
            btn_battle_attack.setOnClickListener {
                (activity as MainActivity).onAttackClicked(pokemons[position].moves as List<MoveModel>)
            }
        }

        btn_battle_pokemon.setOnClickListener {
            (activity as MainActivity).onPokemonClicked(pokemons)
        }
    }

    interface InteractionButtonClickedInterface {
        fun onAttackClicked(moves : List<MoveModel>)

        fun onPokemonClicked(pokemons : List<SimplifiedPokemonDetails>)
    }
}
