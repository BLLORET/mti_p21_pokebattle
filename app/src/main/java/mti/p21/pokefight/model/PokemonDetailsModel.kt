package mti.p21.pokefight.model

/**
 * Data class that hold complementary information about a pokemon
 */
data class PokemonDetailsModel (
    val height: Int,
    val weight: Int,
    val stats: List<Stat>,
    val moves: List<MoveObject>
)