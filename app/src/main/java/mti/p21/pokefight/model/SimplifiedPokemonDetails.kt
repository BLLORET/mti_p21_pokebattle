package mti.p21.pokefight.model

import java.io.Serializable

/**
 * Make a simplified class of the pokemon through different poke API calls
 */
class SimplifiedPokemonDetails(
    val name: String,
    val sprite: String,
    val types: List<PokeType>
) : Serializable {
    var height : Int = 0
    var weight : Int = 0
    var defense: Int = 0
    var attack: Int = 0
    var hp: Int = 0
    var speed: Int = 0
    var attackSpe: Int = 0
    var defenseSpe: Int = 0
    var moves : MutableList<MoveModel> = arrayListOf()

    /**
     * Load details from PokemonDetailsModel
     */
    fun loadDetails(pokemon: PokemonDetailsModel) {
        val stats = pokemon.stats
        height = pokemon.height
        weight = pokemon.weight
        attack = stats.find { st -> st.stat.name == "attack" }!!.base_stat
        speed = stats.find{ st -> st.stat.name == "speed"}!!.base_stat
        attackSpe = stats.find{ st -> st.stat.name == "special-attack"}!!.base_stat
        defenseSpe = stats.find{ st -> st.stat.name == "special-defense"}!!.base_stat
        defense = stats.find{ st -> st.stat.name == "defense"}!!.base_stat
        hp = stats.find{ st -> st.stat.name == "hp"}!!.base_stat
    }

}