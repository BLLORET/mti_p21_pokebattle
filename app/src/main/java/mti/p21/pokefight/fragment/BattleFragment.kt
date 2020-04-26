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
import mti.p21.pokefight.utils.AbstractActivity
import mti.p21.pokefight.utils.AbstractFragment

/**
 * [BattleFragment] that represent the principal fragment on which the battle takes place.
 */
class BattleFragment(a: AbstractActivity) : AbstractFragment(a) {

    override val layoutResource = R.layout.fragment_battle

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity.chooseAction(false)
        GameManager.start(this)
    }
}
