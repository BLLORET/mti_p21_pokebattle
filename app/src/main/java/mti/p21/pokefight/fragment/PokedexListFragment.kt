package mti.p21.pokefight.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_pokedex_list.*
import mti.p21.pokefight.R
import mti.p21.pokefight.adapter.PokemonModelAdapter
import mti.p21.pokefight.utils.AbstractActivity
import mti.p21.pokefight.utils.AbstractFragment

/**
 * [PokedexListFragment] represent the fragment of the pokedex list view.
 */
class PokedexListFragment(a: AbstractActivity) : AbstractFragment(a) {

    override val layoutResource =R.layout.fragment_pokedex_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity.executeWhenListPokemonIsLoaded(this) {
            val onPokemonDetailsClickedListener = View.OnClickListener {view ->
                val pokemon = it[view.tag as Int]
                mainActivity.onPokedexRowClicked(pokemon)
            }

            recycler_container.layoutManager = LinearLayoutManager(mainActivity)
            recycler_container.adapter =
                PokemonModelAdapter(it, mainActivity, resources, onPokemonDetailsClickedListener)
        }
    }
}
