package mti.p21.pokefight.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_pokedex_details.*
import kotlinx.coroutines.delay
import mti.p21.pokefight.R
import mti.p21.pokefight.model.SimplifiedPokemonDetails


/**
 * A simple [Fragment] subclass.
 */
class PokedexDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pokedex_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (super.onViewCreated(view, savedInstanceState))
        arguments?.let {
            val pokemon = it.getSerializable("SimplifiedPokemon") as SimplifiedPokemonDetails
            loadPokemonDetails(pokemon)
        }
    }

    /**
     * Load details about the current pokemon an display them in the details screen.
     */
    private fun loadPokemonDetails(pokemon: SimplifiedPokemonDetails) {

        pokemon.detailsCounter++
        pokemon.pokeApiInterface.getPokemonDetails(pokemon.name).enqueue(
            pokemon.loadCallBackPokemonDetails(activity!!)
        )

        // Set when the call is finished
        lifecycleScope.launchWhenStarted {

            while (pokemon.detailsCounter > 0) {
                delay(500)
            }
            details_pokemon_TextView_Attack_Param.text = pokemon.attack.toString()
            details_pokemon_TextView_Defense_Param.text = pokemon.defense.toString()
            details_pokemon_TextView_HP_Param.text = pokemon.hp.toString()
            details_pokemon_TextView_Speed_Param.text = pokemon.speed.toString()
            details_pokemon_TextView_Height_Param.text = pokemon.height.toString()
            details_pokemon_TextView_Weight_Param.text = pokemon.weight.toString()
            details_pokemon_TextView_SpeAttack_Param.text = pokemon.attackSpe.toString()
            details_pokemon_TextView_SpeDefense_Param.text = pokemon.defenseSpe.toString()
        }

        // Load pictures
        Glide
            .with(activity!!)
            .load(pokemon.sprite)
            .into(details_pokemon_ImageView_Pokemon)

        details_pokemon_TextView_Name.text = pokemon.name

        details_pokemon_ImageView_Type1.setImageResource(
            pokemon.types[0].getPictureID(resources, activity!!)
        )
        details_pokemon_ImageView_Type2.setImageResource(
            if (pokemon.types.size > 1) pokemon.types[1].getPictureID(resources, activity!!)
            else 0
        )
    }
}
