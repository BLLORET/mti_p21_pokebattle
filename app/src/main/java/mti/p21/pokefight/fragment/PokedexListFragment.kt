package mti.p21.pokefight.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_pokedex_list.*

import mti.p21.pokefight.R
import mti.p21.pokefight.adapter.PokemonModelAdapter
import mti.p21.pokefight.model.PokemonModel
import kotlin.reflect.typeOf

/**
 * A simple [Fragment] subclass.
 */
class PokedexListFragment : Fragment() {

    var pokemons : List<PokemonModel> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pokedex_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        arguments?.let {
            val jsonContent = it!!.getString("Pokemons")
            val jsonBuilder = GsonBuilder().create()

            val type = object : TypeToken<List<PokemonModel>>() {}.type
            pokemons = jsonBuilder.fromJson(jsonContent, type)
        }

        recycler_container.setHasFixedSize(true)
        recycler_container.layoutManager = LinearLayoutManager(activity)
        recycler_container.adapter = PokemonModelAdapter(pokemons, activity!!)
        recycler_container.addItemDecoration(
            DividerItemDecoration(
                activity,
                LinearLayoutManager.VERTICAL
            )
        )
    }
}
