package mti.p21.pokefight.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_battle.*
import mti.p21.pokefight.GameManager
import mti.p21.pokefight.MainActivity

import mti.p21.pokefight.R
import mti.p21.pokefight.model.SimplifiedPokemonDetails

/**
 * A simple [Fragment] subclass.
 */

class BattleFragment(
    private val team : List<SimplifiedPokemonDetails>,
    private val opponentTeam : List<SimplifiedPokemonDetails>
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
        gameManager = GameManager(team, opponentTeam, fragment = activity!!)
        (activity as MainActivity).chooseAction(gameManager)

        gameManager.loadCurrentPokemonInformations(true)
        gameManager.loadOpponentPokemonInformations(true)

        team[1].loadDetails {  }
        team[2].loadDetails {
            activity!!.findViewById<Button>(R.id.btn_battle_attack).isEnabled = true
            activity!!.findViewById<Button>(R.id.btn_battle_pokemon).isEnabled = true
        }


    }

    interface TurnSelection {
        fun chooseAction(gameManager: GameManager)
    }
}
