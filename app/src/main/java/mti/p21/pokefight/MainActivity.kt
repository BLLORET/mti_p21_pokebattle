package mti.p21.pokefight

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import mti.p21.pokefight.fragment.*
import mti.p21.pokefight.model.PokeType
import mti.p21.pokefight.model.PokemonModel
import mti.p21.pokefight.model.SimplifiedPokemonDetails
import mti.p21.pokefight.utils.AbstractActivity
import mti.p21.pokefight.utils.ExceptionDuringSuccess
import mti.p21.pokefight.utils.call
import mti.p21.pokefight.webServiceInterface.PokemonModelInterface

class MainActivity : AbstractActivity() {

    override val layoutResource = R.layout.activity_main

    override lateinit var listPokemon: MutableLiveData<List<PokemonModel>?>
    override fun executeWhenListPokemonIsLoaded(owner: LifecycleOwner,
                                                lambda: (List<PokemonModel>) -> Unit) {
        listPokemon.observe(owner, Observer { if (it != null) lambda(it) })
    }

    override fun onInit() {
        setupRetrofitWithUrl("https://www.surleweb.xyz/api/")
        listPokemon = MutableLiveData<List<PokemonModel>?>()
        loadPokemonModels()

        replaceFragment<SplashScreenFragment>(false)
        setupRetrofitWithUrl("https://pokeapi.co/api/v2/")
    }

    /**
     * Load Pokemons on the internet and store it in [listPokemon]
     */
    private fun loadPokemonModels() {
        service<PokemonModelInterface>().getAllPokemons().call {
            onSuccess = {
                val pokemonModels = it.body()
                    ?: throw ExceptionDuringSuccess("Pokemon list is empty!")
                listPokemon.value = pokemonModels.sortedBy { p -> p.name }
            }
            onFailure = {
                toastLong("Failed to load pokemons, make sure you have internet")
                Log.w("Pokemons", "Cannot load all pokemons in API: $it")
                showDefaultErrors = false
            }
            onAnyErrorNoArg = { toastLong("Error while loading pokemons!") }
        }
    }

    override fun onBattleButtonClicked() { replaceFragment<LobbyFragment>() }
    override fun onPokedexButtonClicked() { replaceFragment<PokedexListFragment>() }

    override fun onTypePictureClicked(pokeType: PokeType) {

        val argumentsBundle = Bundle()
        argumentsBundle.putSerializable("PokeType", pokeType)

        val helpScreenFragment = HelpScreenFragment(this)
        helpScreenFragment.arguments = argumentsBundle

        // Add and not replace!
        addFragment(helpScreenFragment)
    }

    override fun onFightButtonClicked(team: List<PokemonModel>, opponents: List<PokemonModel>) {

        val teamSimplifiedDetails : List<SimplifiedPokemonDetails> = listOf (
            SimplifiedPokemonDetails(team[0].name, team[0].sprite, team[0].types),
            SimplifiedPokemonDetails(team[1].name, team[1].sprite, team[1].types),
            SimplifiedPokemonDetails(team[2].name, team[2].sprite, team[2].types)
        )

        val opponentSimplifiedDetails : List<SimplifiedPokemonDetails> = listOf (
            SimplifiedPokemonDetails(opponents[0].name, opponents[0].sprite, opponents[0].types),
            SimplifiedPokemonDetails(opponents[1].name, opponents[1].sprite, opponents[1].types),
            SimplifiedPokemonDetails(opponents[2].name, opponents[2].sprite, opponents[2].types)
        )

        GameManager.init(this, teamSimplifiedDetails, opponentSimplifiedDetails)
        replaceFragment<BattleFragment>()
    }

    override fun onPokedexRowClicked(pokemon: PokemonModel) {
        val argumentsBundle = Bundle()
        val simplifiedPokemon =
            SimplifiedPokemonDetails(pokemon.name, pokemon.sprite, pokemon.types)

        argumentsBundle.putSerializable("SimplifiedPokemon", simplifiedPokemon)

        val detailsPokemonFragment = PokedexDetailsFragment(this)
        detailsPokemonFragment.arguments = argumentsBundle

        replaceFragment(detailsPokemonFragment)
    }

    override fun chooseAction(enable: Boolean) {
        GameManager.interactFragment = BattleInteractionFragment(this, enable)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.interaction_container, GameManager.interactFragment)
            .commit()
    }

    override fun onAttackButtonClicked() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.interaction_container, BattleMovesFragment(this))
            .commit()
    }

    override fun onPokemonButtonClicked() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.interaction_container, BattlePokemonsFragment(this))
            .commit()
    }
}
