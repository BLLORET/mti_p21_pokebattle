package mti.p21.pokefight

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_splash_screen.*
import mti.p21.pokefight.fragment.*
import mti.p21.pokefight.model.MoveModel
import mti.p21.pokefight.model.PokeType
import mti.p21.pokefight.model.PokemonModel
import mti.p21.pokefight.model.SimplifiedPokemonDetails
import mti.p21.pokefight.webServiceInterface.PokemonModelInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity
    : AppCompatActivity(),
    SplashScreenFragment.SplashScreenButtonClicked,
    LobbyFragment.LobbyTypeClicked,
    LobbyFragment.FightClicked,
    BattleFragment.TurnSelection,
    BattleInteractionFragment.InteractionButtonClickedInterface,
    BattleMovesFragment.MoveClickedInterface,
    BattlePokemonsFragment.PokemonClickedInterface,
    PokedexListFragment.PokedexDetailsFragmentClicked {

    var data : List<PokemonModel>? = null

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
        val baseURL = "https://www.surleweb.xyz/api/"
        val jsonConverter = GsonConverterFactory.create(GsonBuilder().create())
        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(jsonConverter)
            .build()

        val service : PokemonModelInterface = retrofit.create(PokemonModelInterface::class.java)

        val wsServiceCallback : Callback<List<PokemonModel>> = object : Callback<List<PokemonModel>> {
            override fun onFailure(call: Call<List<PokemonModel>>, t: Throwable) {
                Toast.makeText(this@MainActivity,
                          "Cannot load pokemons, make sure you have internet",
                               Toast.LENGTH_LONG).show()
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
        service.getAllPokemons().enqueue(wsServiceCallback)
    }

    override fun onBattleClicked() {
        goToFragment(LobbyFragment())
    }

    override fun onPokedexClicked() {
        goToFragment(PokedexListFragment())
    }

    private fun goToFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.main_container, fragment)
            .commit()
    }

    override fun onTypeClicked(pokeType : PokeType) {

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

    override fun chooseAction(gameManager: GameManager) {

        val argumentBundle = Bundle()
        argumentBundle.putSerializable("GameManager", gameManager)

        val fragment = BattleInteractionFragment()
        fragment.arguments = argumentBundle

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.interaction_container, fragment)
            .commit()
    }

    override fun onFightClicked(team: List<PokemonModel>, opponents : List<PokemonModel>) {

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

    override fun onDetailsClicked(pokemon: PokemonModel) {
        val argumentsBundle = Bundle()
        val simplifiedPokemon = SimplifiedPokemonDetails(pokemon.name, pokemon.sprite, pokemon.types)

        argumentsBundle.putSerializable("SimplifiedPokemon", simplifiedPokemon)

        val detailsPokemonFragment = PokedexDetailsFragment()
        detailsPokemonFragment.arguments = argumentsBundle

        goToFragment(detailsPokemonFragment)
    }

    private fun changeInteractionZoneFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.interaction_container, fragment)
            .commit()
    }

    override fun onAttackClicked(gameManager: GameManager) {
        val argumentsBundle = Bundle()
        argumentsBundle.putSerializable("GameManager", gameManager)

        val fragment = BattleMovesFragment()
        fragment.arguments = argumentsBundle

        changeInteractionZoneFragment(fragment)
    }

    override fun onPokemonClicked(gameManager: GameManager) {
        val argumentsBundle = Bundle()
        argumentsBundle.putSerializable("GameManager", gameManager)

        val fragment = BattlePokemonsFragment()
        fragment.arguments = argumentsBundle

        changeInteractionZoneFragment(fragment)
    }

    override fun onMoveClicked(gameManager: GameManager) {
        val argumentsBundle = Bundle()
        argumentsBundle.putSerializable("GameManager", gameManager)

        val fragment = BattleInteractionFragment()
        fragment.arguments = argumentsBundle

        changeInteractionZoneFragment(fragment)
    }

    override fun onPokemonChangeClicked(gameManager: GameManager) {
        val argumentsBundle = Bundle()
        argumentsBundle.putSerializable("GameManager", gameManager)

        val fragment = BattleInteractionFragment(true)
        fragment.arguments = argumentsBundle

        changeInteractionZoneFragment(fragment)
    }
}
