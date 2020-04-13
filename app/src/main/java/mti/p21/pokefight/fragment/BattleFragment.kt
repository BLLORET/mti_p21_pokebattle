package mti.p21.pokefight.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import mti.p21.pokefight.R
import mti.p21.pokefight.model.PokemonModel

/**
 * A simple [Fragment] subclass.
 */
class BattleFragment(val team : List<PokemonModel>,
                     val opponentTeam : List<PokemonModel>) : Fragment()

{

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_battle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}
