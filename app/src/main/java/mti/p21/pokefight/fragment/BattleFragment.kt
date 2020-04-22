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
    private var currentPokemonIndex : Int = 0
    private var currentOpponentIndex : Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_battle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setCurrentPokemonInformations(team[0])
        setOpponentPokemonInformations(opponentTeam[0])

        team[1].loadDetails {  }
        team[2].loadDetails {  }

        (activity as MainActivity).chooseAction(team, currentOpponentIndex)

        loopGame()
    }

    private fun setCurrentPokemonInformations(pokemon : SimplifiedPokemonDetails) {
        pokemon.loadDetails {
            currentPokemonHP_textView.text = pokemon.hp.toString()
        }

        pokemon.loadMoves(activity!!)

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

        pokemon.loadMoves(activity!!)

        Glide
            .with(activity!!)
            .load(pokemon.sprite)
            .into(opponentPokemon_imageView)
    }

    /**
     * Determine if the player has won or if he has lost
     */
    private fun hasWon() : Boolean {
        return team.find { pokemon -> pokemon.hp != 0 } != null
    }

    /**
     * Determine if a team has all of it's pokemon ko.
     */
    private fun gameIsFinished() : Boolean {
        return team.all { pokemon -> pokemon.hp == 0 }
                || opponentTeam.all { pokemon -> pokemon.hp == 0 }
    }

    /**
     * Function for making the opponent choose an action
     */
    private fun opponentChooseAction() {

    }

    /**
     * Let the player choose an action in this turn of game
     */
    private fun chooseAction() {

    }

    /**
     * Represent the loop of the game
     */
    private fun loopGame() {
        while (!gameIsFinished()) {
            // Prepare the turn
            chooseAction()
            opponentChooseAction()

            // Action turn
            if (team[currentPokemonIndex].speed >= opponentTeam[currentOpponentIndex].speed) {

            } else {

            }
        }
    }

    interface TurnSelection {
        fun chooseAction(pokemons: List<SimplifiedPokemonDetails>, position : Int)
    }
}
