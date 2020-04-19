package mti.p21.pokefight.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_battle.*
import kotlinx.android.synthetic.main.fragment_battle_interaction.*
import mti.p21.pokefight.MainActivity

import mti.p21.pokefight.R
import mti.p21.pokefight.model.PokemonModel
import mti.p21.pokefight.model.SimplifiedPokemonDetails

/**
 * A simple [Fragment] subclass.
 */

class BattleFragment(private val team : List<SimplifiedPokemonDetails>,
                     private val opponentTeam : List<SimplifiedPokemonDetails>) : Fragment()

{
    private var currentPokemon : SimplifiedPokemonDetails = team[0]
    private var currentOpponent : SimplifiedPokemonDetails = opponentTeam[0]

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_battle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setCurrentPokemonInformations(currentPokemon)
        setOpponentPokemonInformations(currentOpponent)

        (activity as MainActivity).chooseAction()
    }

    private fun setCurrentPokemonInformations(pokemon : SimplifiedPokemonDetails) {
        pokemon.loadDetails {
            currentPokemonHP_textView.text = pokemon.hp.toString()
        }

        currentPokemonName_textView.text = pokemon.name

        Glide
            .with(activity!!)
            .load(pokemon.sprite)
            .into(currentPokemon_imageView)

    }

    private fun setOpponentPokemonInformations(pokemon : SimplifiedPokemonDetails) {
        opponentPokemonName_textView.text = pokemon.name

        pokemon.loadDetails {
            opponentPokementHP_textView.text = pokemon.hp.toString()
        }

        Glide
            .with(activity!!)
            .load(pokemon.sprite)
            .into(opponentPokemon_imageView)
    }


    interface TurnSelection {
        fun chooseAction()
    }
}
