package mti.p21.pokefight.model

import java.io.Serializable

data class PokemonModel (
    val id : Int,
    val name : String,
    val sprite : String,
    val types : List<PokeType>
)