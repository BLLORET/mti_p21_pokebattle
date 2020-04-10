package mti.p21.pokefight.model

import android.content.res.Resources
import android.graphics.drawable.Drawable

/**
 * Class that represent the pokemon type.
 */
data class PokeType (val name : String) {

    /**
     * Return the string path picture associated with the specific PokeType.
     */
    fun getPicture(resources : Resources): Drawable {

        val id = resources.getIdentifier("@drawable/" + name, null, null)
        return resources.getDrawable(id, null)
    }
}