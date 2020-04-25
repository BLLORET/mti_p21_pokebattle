package mti.p21.pokefight.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mti.p21.pokefight.GameManager
import mti.p21.pokefight.MainActivity

import mti.p21.pokefight.R
import mti.p21.pokefight.model.SimplifiedPokemonDetails

/**
 * [BattleFragment] that represent the principal fragment on which the battle takes place.
 */

class BattleFragment(
    private val team: List<SimplifiedPokemonDetails>,
    private val opponentTeam: List<SimplifiedPokemonDetails>
) : Fragment() {

    private lateinit var gameManager : GameManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_battle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gameManager = GameManager(team, opponentTeam, fragment = activity!!, resources = resources)
        (activity as MainActivity).chooseAction(gameManager)
    }
}
