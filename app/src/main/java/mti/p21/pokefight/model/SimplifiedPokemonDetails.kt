package mti.p21.pokefight.model

import android.util.Log
import com.google.gson.GsonBuilder
import mti.p21.pokefight.webServiceInterface.PokeApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.Serializable

data class SimplifiedPokemonDetails(
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

    init {
        val baseURL = "https://pokeapi.co/api/v2/"
        val jsonConverter = GsonConverterFactory.create(GsonBuilder().create())
        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(jsonConverter)
            .build()

        val service : PokeApiInterface = retrofit.create(PokeApiInterface::class.java)

        val wsServiceCallback : Callback<PokemonDetailsModel> = object :
            Callback<PokemonDetailsModel> {
            override fun onFailure(call: Call<PokemonDetailsModel>, t: Throwable) {
                Log.w("PokeApi", "Cannot load statistics of the $name pokemon")
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
            }
        }
        service.getPokemonDetails(name).enqueue(wsServiceCallback)
    }
}