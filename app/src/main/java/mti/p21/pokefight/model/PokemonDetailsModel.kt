package mti.p21.pokefight.model

/**
 * Data class that hold complementary informations about a pokemon
 */
data class PokemonDetailsModel (
    val height : Int,
    val weight : Int,
    val stats : List<Stat>
)