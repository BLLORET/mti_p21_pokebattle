package mti.p21.pokefight.model

/**
 * Class that hold principal informations about a pokemon.
 */
data class PokemonModel (
    val id : Int,
    val name : String,
    val sprite : String,
    val types : List<PokeType>
)