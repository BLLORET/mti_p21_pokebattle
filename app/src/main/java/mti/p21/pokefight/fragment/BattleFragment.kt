package mti.p21.pokefight.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_battle.*
import mti.p21.pokefight.MainActivity

import mti.p21.pokefight.R
import mti.p21.pokefight.model.PokemonModel

/**
 * A simple [Fragment] subclass.
 */

class BattleFragment(private val team : List<PokemonModel>,
                     private val opponentTeam : List<PokemonModel>) : Fragment()

{
    private var currentPokemon : PokemonModel = team[0]
    private var currentOpponent : PokemonModel = opponentTeam[0]

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

    private fun setCurrentPokemonInformations(pokemon : PokemonModel) {
        currentPokemonName_textView.text = pokemon.name

        Glide
            .with(activity!!)
            .load(pokemon.sprite)
            .into(currentPokemon_imageView)
    }

    private fun setOpponentPokemonInformations(pokemon : PokemonModel) {
        opponentPokemonName_textView.text = pokemon.name

        Glide
            .with(activity!!)
            .load(pokemon.sprite)
            .into(opponentPokemon_imageView)
    }


    interface TurnSelection {
        fun chooseAction()
    }
}
