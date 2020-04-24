package mti.p21.pokefight

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_splash_screen.*
import mti.p21.pokefight.fragment.*
import mti.p21.pokefight.model.PokeType
import mti.p21.pokefight.model.PokemonModel
import mti.p21.pokefight.model.SimplifiedPokemonDetails
import mti.p21.pokefight.webServiceInterface.PokemonModelInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), FragmentInteractionsInterface, BattleInteractionsInterface {

    var data: List<PokemonModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container, SplashScreenFragment())
            .commit()

        loadPokemonModels()
    }

    /**
     * Load Pokemons on the internet and store it in [data]
     */
    private fun loadPokemonModels() {

        val service = Retrofit.Builder()
            .baseUrl("https://www.surleweb.xyz/api/")
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().create()
                )
            )
            .build()
            .create(PokemonModelInterface::class.java)

        val wsCallback : Callback<List<PokemonModel>> = object: Callback<List<PokemonModel>> {
            override fun onFailure(call: Call<List<PokemonModel>>, t: Throwable) {
                Toast.makeText(this@MainActivity,
                          "Failed to load pokemons, make sure you have internet",
                               Toast.LENGTH_LONG).show()
                Log.w("Pokemons", "Cannot load all pokemons in API: $t")
            }

            override fun onResponse(
                call: Call<List<PokemonModel>>,
                response: Response<List<PokemonModel>>
            ) {
                if (response.code() == 200) {
                    response.body()?.let { pokemonModels ->
                        data = pokemonModels.sortedBy {pokemon ->
                            pokemon.name
                        }
                        btn_battle.isEnabled = true
                        btn_pokedex.isEnabled = true
                    }
                }
            }
        }

        service.getAllPokemons().enqueue(wsCallback)
    }

    override fun onBattleButtonClicked() {
        goToFragment(LobbyFragment())
    }

    override fun onPokedexButtonClicked() {
        goToFragment(PokedexListFragment())
    }

    /**
     * Intermediary function to switch the main fragment
     */
    private fun goToFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.main_container, fragment)
            .commit()
    }

    override fun onTypePictureClicked(pokeType: PokeType) {

        val argumentsBundle = Bundle()
        argumentsBundle.putSerializable("PokeType", pokeType)

        val helpScreenFragment = HelpScreenFragment()
        helpScreenFragment.arguments = argumentsBundle

        // Do not use goToFragment because it does not replace the fragment
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .add(R.id.main_container, helpScreenFragment)
            .commit()
    }

    override fun onFightButtonClicked(team: List<PokemonModel>, opponents: List<PokemonModel>) {

        val teamSimplifiedPokemonDetails : List<SimplifiedPokemonDetails> = listOf (
            SimplifiedPokemonDetails(team[0].name, team[0].sprite, team[0].types),
            SimplifiedPokemonDetails(team[1].name, team[1].sprite, team[1].types),
            SimplifiedPokemonDetails(team[2].name, team[2].sprite, team[2].types)
        )

        val opponentSimplifiedPokemonDetails : List<SimplifiedPokemonDetails> = listOf (
            SimplifiedPokemonDetails(opponents[0].name, opponents[0].sprite, opponents[0].types),
            SimplifiedPokemonDetails(opponents[1].name, opponents[1].sprite, opponents[1].types),
            SimplifiedPokemonDetails(opponents[2].name, opponents[2].sprite, opponents[2].types)
        )

        goToFragment(BattleFragment(teamSimplifiedPokemonDetails, opponentSimplifiedPokemonDetails))
    }

    override fun onPokedexRowClicked(pokemon: PokemonModel) {
        val argumentsBundle = Bundle()
        val simplifiedPokemon = SimplifiedPokemonDetails(pokemon.name, pokemon.sprite, pokemon.types)

        argumentsBundle.putSerializable("SimplifiedPokemon", simplifiedPokemon)

        val detailsPokemonFragment = PokedexDetailsFragment()
        detailsPokemonFragment.arguments = argumentsBundle

        goToFragment(detailsPokemonFragment)
    }

    private fun changeInteractionZoneFragment(fragment: Fragment, gameManager: GameManager) {
        val argumentBundle = Bundle()
        argumentBundle.putSerializable("GameManager", gameManager)

        fragment.arguments = argumentBundle

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.interaction_container, fragment)
            .commit()
    }

    override fun chooseAction(gameManager: GameManager, enableButtons: Boolean) {
        changeInteractionZoneFragment(BattleInteractionFragment(enableButtons), gameManager)
    }

    override fun onAttackButtonClicked(gameManager: GameManager) {
        changeInteractionZoneFragment(BattleMovesFragment(), gameManager)
    }

    override fun onPokemonButtonClicked(gameManager: GameManager) {
        changeInteractionZoneFragment(BattlePokemonsFragment(), gameManager)
    }
}
