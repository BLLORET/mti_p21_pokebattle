package mti.p21.pokefight.webServiceInterface

import mti.p21.pokefight.model.PokemonDetailsModel
import mti.p21.pokefight.model.TypeModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PokeApiInterface {
    @GET("type")
    fun getDamageRelations(@Query("name") name : String) : Call<TypeModel>

    @GET("pokemon")
    fun getPokemonDetails(@Query("name") name : String) : Call<PokemonDetailsModel>
}