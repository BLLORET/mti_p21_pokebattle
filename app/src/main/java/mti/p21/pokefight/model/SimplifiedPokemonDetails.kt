package mti.p21.pokefight.model

import android.util.Log
import mti.p21.pokefight.utils.AbstractActivity
import mti.p21.pokefight.utils.CallBackKt
import mti.p21.pokefight.utils.ExceptionDuringSuccess
import mti.p21.pokefight.utils.call
import mti.p21.pokefight.webServiceInterface.PokeApiInterface
import retrofit2.Callback
import java.io.Serializable

/**
 * Make a simplified class of the pokemon through different poke API calls
 */
class SimplifiedPokemonDetails(
    val name: String,
    val sprite: String,
    val types: List<PokeType>
) : Serializable {
    var height : Int = 0
    var weight : Int = 0
    var defense: Int = 0
    var attack: Int = 0
    var hp: Int = 0
    var speed: Int = 0
    var attackSpe: Int = 0
    var defenseSpe: Int = 0
    var moves : MutableList<MoveModel> = arrayListOf()

    var detailsCounter: Int = 0
    var movesCounter: Int = 0

    /**
     * Load details from PokemonDetailsModel
     */
    fun loadDetails(pokemon: PokemonDetailsModel) {
        val stats = pokemon.stats
        height = pokemon.height
        weight = pokemon.weight
        attack = stats.find { st -> st.stat.name == "attack" }!!.base_stat
        speed = stats.find{ st -> st.stat.name == "speed"}!!.base_stat
        attackSpe = stats.find{ st -> st.stat.name == "special-attack"}!!.base_stat
        defenseSpe = stats.find{ st -> st.stat.name == "special-defense"}!!.base_stat
        defense = stats.find{ st -> st.stat.name == "defense"}!!.base_stat
        hp = stats.find{ st -> st.stat.name == "hp"}!!.base_stat
    }

    /**
     * Load details in the API, and store them when the callback is called
     */
    fun loadCallBackPokemonDetails(mainActivity: AbstractActivity) : Callback<PokemonDetailsModel> {

        return CallBackKt<PokemonDetailsModel>().apply {
            onSuccess = {
                val pokemon = it.body()
                    ?: throw ExceptionDuringSuccess("Body is null")
                loadDetails(pokemon)
                detailsCounter--
            }
            onFailure = {
                mainActivity.toastLong("Failed to load stats of $name")
                Log.w("PokeApi", "Cannot load statistics of the $name pokemon: $it")
            }
            onAnyErrorNoArg = {
                detailsCounter--
            }
        }
    }

    /**
     * Load moves in the API, and store them when the callback is called
     */
    fun loadCallBackMoves(mainActivity: AbstractActivity) : Callback<PokemonDetailsModel> {
        return CallBackKt<PokemonDetailsModel>().apply {
            onSuccess = {
                val pokemon = it.body()
                    ?: throw ExceptionDuringSuccess("Body is null")
                pokemon.moves.forEach { moveObject ->
                    movesCounter++
                    loadMove(moveObject.move.name, mainActivity)
                }
            }
            onFailure = {
                Log.w("PokeApi", "Cannot load moves of the $name pokemon")
            }
        }
    }

    /**
     * Load a specific move and add it to the pokemon moves
     */
    private fun loadMove(name : String, mainActivity: AbstractActivity) {
        mainActivity.service<PokeApiInterface>().getMoveDetails(name).call {
            onSuccess = {
                val moveModel = it.body()
                    ?: throw ExceptionDuringSuccess("Body is null")
                if (moveModel.power > 0)
                    moves.add(moveModel)
                movesCounter--
            }
            onFailure = { mainActivity.toastLong("Failed to load $name") }
            onAnyErrorNoArg = { movesCounter-- }
        }
    }
}