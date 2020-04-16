package mti.p21.pokefight.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_pokedex_details.*

import mti.p21.pokefight.R
import mti.p21.pokefight.model.PokemonDetailsModel
import mti.p21.pokefight.model.PokemonModel
import mti.p21.pokefight.model.SimplifiedPokemonDetails
import mti.p21.pokefight.model.TypeModel
import mti.p21.pokefight.webServiceInterface.PokeApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
        arguments?.let{
            val pokemon = it.getSerializable("PokemonModel") as PokemonModel
         //   getPokeStats(pokemon.name)
            Glide
                .with(activity!!)
                .load(pokemon!!.sprite)
                .into(details_pokemon_ImageView_Pokemon)
            Glide
                .with(activity!!)
                .load(pokemon!!.types[0])
                .into(details_pokemon_ImageView_Type1)
            if (pokemon!!.types.size > 1) {
                Glide
                    .with(activity!!)
                    .load(pokemon!!.types[1])
                    .into(details_pokemon_ImageView_Type2)
            }
            details_pokemon_TextView_Name.text = pokemon.name
        }
    }

    private fun getPokeStats(pokemonName: String) {
        val details = SimplifiedPokemonDetails(pokemonName)
        details_pokemon_TextView_Attack_Param.text = details.attack.toString()
        details_pokemon_TextView_Defense_Param.text = details.defense.toString()
        details_pokemon_TextView_HP_Param.text = details.hp.toString()
        details_pokemon_TextView_Height_Param.text = details.height.toString()
        details_pokemon_TextView_Weight_Param.text = details.weight.toString()
        details_pokemon_TextView_SpeAttack_Param.text = details.attackSpe.toString()
        details_pokemon_TextView_SpeDefense_Param.text = details.defenseSpe.toString()
    }
}
