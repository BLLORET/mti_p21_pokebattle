package mti.p21.pokefight.fragment

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_splash_screen.*
import mti.p21.pokefight.R
import mti.p21.pokefight.utils.AbstractActivity
import mti.p21.pokefight.utils.AbstractFragment

/**
 * [SplashScreenFragment] represent the fragment of the splash screen view.
 */
class SplashScreenFragment(a: AbstractActivity) : AbstractFragment(a) {

    override val layoutResource = R.layout.fragment_splash_screen

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Disabled them if pokemons are not loaded
        btn_battle.isEnabled = false
        btn_pokedex.isEnabled = false
        mainActivity.executeWhenListPokemonIsLoaded(this) {
            btn_battle.isEnabled = true
            btn_pokedex.isEnabled = true
        }

        btn_battle.setOnClickListener {
            mainActivity.onBattleButtonClicked()
        }

        btn_pokedex.setOnClickListener {
            mainActivity.onPokedexButtonClicked()
        }
    }
}
