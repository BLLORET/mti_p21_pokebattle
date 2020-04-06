package mti.p21.pokefight.model

/**
 * Class that represent the pokemon type.
 */
data class PokeType (val name : String) {

    /**
     * Return the string path picture associated with the specific PokeType.
     */
    fun getPicture(): String {
        return this.name + ".png"
    }
}