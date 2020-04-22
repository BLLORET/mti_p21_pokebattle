package mti.p21.pokefight.model

data class MoveModel (
    val name : String,
    val power : Int,
    val damage_class : DamageClass,
    val type : PokeType
)