package mti.p21.pokefight.model

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.gson.GsonBuilder
import mti.p21.pokefight.webServiceInterface.PokeApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

    val pokeApiInterface: PokeApiInterface = getPokeAPIService()

    /**
     * Get the pokeApiService to make request to it.
     */
    private fun getPokeAPIService() : PokeApiInterface {
        val baseURL = "https://pokeapi.co/api/v2/"
        val jsonConverter = GsonConverterFactory.create(GsonBuilder().create())
        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(jsonConverter)
            .build()

        return retrofit.create(PokeApiInterface::class.java)
    }

    /**
     * Load details in the API, and store them when the callback is called
     */
    fun loadCallBackPokemonDetails(context: Context) : Callback<PokemonDetailsModel> {

        return object : Callback<PokemonDetailsModel> {
            override fun onFailure(call: Call<PokemonDetailsModel>, t: Throwable) {
                Toast.makeText(context, "Failed to load stats of $name", Toast.LENGTH_LONG)
                Log.w("PokeApi", "Cannot load statistics of the $name pokemon: $t")
                detailsCounter--
            }

            override fun onResponse(call: Call<PokemonDetailsModel>, response: Response<PokemonDetailsModel>) {
                Log.w("Response: ", response.code().toString())
                if (response.code() == 200) {
                    response.body()?.let {
                        val stats = it.stats
                        height = it.height
                        weight = it.weight
                        attack = stats.find { st -> st.stat.name == "attack" }!!.base_stat
                        speed = stats.find{ st -> st.stat.name == "speed"}!!.base_stat
                        attackSpe = stats.find{ st -> st.stat.name == "special-attack"}!!.base_stat
                        defenseSpe = stats.find{ st -> st.stat.name == "special-defense"}!!.base_stat
                        defense = stats.find{ st -> st.stat.name == "defense"}!!.base_stat
                        hp = stats.find{ st -> st.stat.name == "hp"}!!.base_stat
                    }
                }
                detailsCounter--
            }
        }
    }

    /**
     * Load moves in the API, and store them when the callback is called
     */
    fun loadCallBackMoves(context: Context) : Callback<PokemonDetailsModel> {
        return object : Callback<PokemonDetailsModel> {
            override fun onFailure(call: Call<PokemonDetailsModel>, t: Throwable) {
                Log.w("PokeApi", "Cannot load moves of the $name pokemon")
            }

            override fun onResponse(call: Call<PokemonDetailsModel>, response: Response<PokemonDetailsModel>) {
                Log.w("Response: ", response.code().toString())
                if (response.code() == 200) {
                    response.body()?.let {
                        it.moves.forEach { moveObject ->
                            movesCounter++
                            loadMove(moveObject.move.name, context)
                        }
                    }
                }
            }
        }
    }

    /**
     * Load a specific move and add it to the pokemon moves
     */
    private fun loadMove(name : String, context: Context) {

        pokeApiInterface.getMoveDetails(name).enqueue(
            object : Callback<MoveModel> {
                override fun onFailure(call: Call<MoveModel>, t: Throwable) {
                    Toast.makeText(context, "Failed to load $name", Toast.LENGTH_LONG).show()
                    Log.w("MoveError", t)
                    movesCounter--
                }

                override fun onResponse(call: Call<MoveModel>, response: Response<MoveModel>) {
                    if (response.code() == 200) {
                        response.body()?.let {
                            if (it.power > 0) {
                                moves.add(it)
                            }
                        }
                    }
                    movesCounter--
                }
            }
        )
    }
}