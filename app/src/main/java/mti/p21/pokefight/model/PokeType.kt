package mti.p21.pokefight.model

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.Log
import mti.p21.pokefight.R

/**
 * Class that represent the pokemon type.
 */
data class PokeType (val name : String) {

    /**
     * Return the id of the picture associated with the specific PokeType.
     */
    fun getPictureID(resources : Resources, context: Context): Int {
        return resources.getIdentifier(name, "drawable", context.packageName)
    }
}