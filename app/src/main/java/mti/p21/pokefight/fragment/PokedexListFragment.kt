package mti.p21.pokefight.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_pokedex_list.*
import mti.p21.pokefight.MainActivity
import mti.p21.pokefight.R
import mti.p21.pokefight.adapter.PokemonModelAdapter
import mti.p21.pokefight.model.PokemonModel

/**
 * [PokedexListFragment] represent the fragment of the pokedex list view.
 */
class PokedexListFragment : Fragment() {

    lateinit var pokemons : List<PokemonModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pokedex_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val onDetailClickedListener = View.OnClickListener {
            val pokemon = pokemons[it.tag as Int]
            (activity as PokedexDetailsFragmentClicked).onDetailsClicked(pokemon)
        }

        pokemons = (activity as MainActivity).data!!
        recycler_container.layoutManager = LinearLayoutManager(activity)
        recycler_container.adapter = PokemonModelAdapter(pokemons, activity!!, resources,
                                                         onDetailClickedListener)
    }

    interface PokedexDetailsFragmentClicked {
        fun onDetailsClicked(pokemon: PokemonModel)
    }
}
