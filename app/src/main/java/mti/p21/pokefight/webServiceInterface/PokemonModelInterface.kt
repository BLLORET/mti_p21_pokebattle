package mti.p21.pokefight.webServiceInterface

import mti.p21.pokefight.model.PokemonModel
import retrofit2.Call
import retrofit2.http.GET

interface PokemonModelInterface {
    @GET("pokemons.json")
    fun getAllPokemons() : Call<List<PokemonModel>>
}