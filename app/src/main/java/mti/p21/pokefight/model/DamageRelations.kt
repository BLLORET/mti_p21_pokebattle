package mti.p21.pokefight.model

/**
 * Data class that hold damage relations between types
 */
data class DamageRelations (
    val no_damage_to: List<PokeType>,
    val half_damage_to: List<PokeType>,
    val double_damage_to: List<PokeType>,
    val no_damage_from: List<PokeType>,
    val half_damage_from: List<PokeType>,
    val double_damage_from: List<PokeType>
)