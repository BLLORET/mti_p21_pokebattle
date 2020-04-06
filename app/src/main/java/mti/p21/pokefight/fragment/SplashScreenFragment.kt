package mti.p21.pokefight.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_splash_screen.*
import mti.p21.pokefight.R

/**
 * A simple [Fragment] subclass.
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

        btn_battle.setOnClickListener {
            (activity as SplashScreenButtonClicked).onBattleClicked()
        }

        btn_pokedex.setOnClickListener {
            (activity as SplashScreenButtonClicked).onPokedexClicked()
        }
    }

    interface SplashScreenButtonClicked {

        fun onBattleClicked()
        fun onPokedexClicked()
    }
}