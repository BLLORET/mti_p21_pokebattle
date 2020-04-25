package mti.p21.pokefight.webServiceInterface

import mti.p21.pokefight.model.MoveModel
import mti.p21.pokefight.model.PokemonDetailsModel
import mti.p21.pokefight.model.TypeModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface PokeApiInterface {
    @GET("type/{name}")
    fun getDamageRelations(@Path("name") name: String) : Call<TypeModel>

    @GET("pokemon/{name}")
    fun getPokemonDetails(@Path("name") name: String) : Call<PokemonDetailsModel>

    @GET("move/{name}")
    fun getMoveDetails(@Path("name") name: String) : Call<MoveModel>
}