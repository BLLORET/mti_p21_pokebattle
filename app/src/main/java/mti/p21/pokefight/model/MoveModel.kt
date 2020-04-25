package mti.p21.pokefight.model

/**
 * Data class for storing informations about a move.
 */
data class MoveModel (
    val name: String,
    val power: Int,
    val damage_class: DataName,
    val type: PokeType,
    val accuracy: Int
)