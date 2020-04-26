package mti.p21.pokefight.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_pokedex_details.*
import mti.p21.pokefight.R
import mti.p21.pokefight.model.SimplifiedPokemonDetails
import mti.p21.pokefight.utils.AbstractActivity
import mti.p21.pokefight.utils.AbstractFragment
import mti.p21.pokefight.utils.ExceptionDuringSuccess
import mti.p21.pokefight.utils.call
import mti.p21.pokefight.webServiceInterface.PokeApiInterface


/**
 * A simple [Fragment] subclass.
 */
class PokedexDetailsFragment(a: AbstractActivity) : AbstractFragment(a) {

    override val layoutResource = R.layout.fragment_pokedex_details

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val pokemon =
                it.getSerializable("SimplifiedPokemon") as SimplifiedPokemonDetails
            loadPokemonDetails(pokemon)
        }
    }

    /**
     * Load details about the current pokemon an display them in the details screen.
     */
    private fun loadPokemonDetails(pokemon: SimplifiedPokemonDetails) {
        service<PokeApiInterface>().getPokemonDetails(pokemon.name).call {
            onSuccess = {
                val pokemonDetails = it.body()
                    ?: throw ExceptionDuringSuccess("Body is null")
                pokemon.loadDetails(pokemonDetails)
                details_pokemon_TextView_Attack_Param.text = pokemon.attack.toString()
                details_pokemon_TextView_Defense_Param.text = pokemon.defense.toString()
                details_pokemon_TextView_HP_Param.text = pokemon.hp.toString()
                details_pokemon_TextView_Speed_Param.text = pokemon.speed.toString()
                details_pokemon_TextView_Height_Param.text = pokemon.height.toString()
                details_pokemon_TextView_Weight_Param.text = pokemon.weight.toString()
                details_pokemon_TextView_SpeAttack_Param.text = pokemon.attackSpe.toString()
                details_pokemon_TextView_SpeDefense_Param.text = pokemon.defenseSpe.toString()
            }
            onFailure = {
                mainActivity.toastLong("Failed to load stats of ${pokemon.name}")
                Log.w("PokeApi",
                    "Cannot load statistics of the ${pokemon.name} pokemon: $it")
            }
        }

        // Load pictures
        Glide
            .with(mainActivity)
            .load(pokemon.sprite)
            .into(details_pokemon_ImageView_Pokemon)

        details_pokemon_TextView_Name.text = pokemon.name

        details_pokemon_ImageView_Type1.setImageResource(
            pokemon.types[0].getPictureID(resources, mainActivity)
        )
        details_pokemon_ImageView_Type2.setImageResource(
            if (pokemon.types.size > 1)
                pokemon.types[1].getPictureID(resources, mainActivity)
            else
                0
        )
    }
}
