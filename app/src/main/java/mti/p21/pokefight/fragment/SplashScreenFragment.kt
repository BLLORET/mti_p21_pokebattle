package mti.p21.pokefight.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_splash_screen.*
import mti.p21.pokefight.MainActivity
import mti.p21.pokefight.R

/**
 * [SplashScreenFragment] represent the fragment of the splash screen view.
 */
class SplashScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Disabled them if pokemons are not loaded
        if ((activity as MainActivity).data == null) {
            btn_battle.isEnabled = false
            btn_pokedex.isEnabled = false
        }

        btn_battle.setOnClickListener {
            (activity as MainActivity).onBattleButtonClicked()
        }

        btn_pokedex.setOnClickListener {
            (activity as MainActivity).onPokedexButtonClicked()
        }
    }
}
