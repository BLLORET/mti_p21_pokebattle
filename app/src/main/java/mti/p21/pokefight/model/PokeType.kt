package mti.p21.pokefight.model

import android.content.Context
import android.content.res.Resources
import java.io.Serializable

/**
 * Data class that represent the pokemon type.
 */
data class PokeType (val name: String) : Serializable {

    /**
     * Return the id of the picture associated with the specific PokeType.
     */
    fun getPictureID(resources: Resources, context: Context): Int {
        return resources.getIdentifier(name, "drawable", context.packageName)
    }
}