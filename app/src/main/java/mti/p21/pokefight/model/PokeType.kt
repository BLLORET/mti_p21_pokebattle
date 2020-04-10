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
     * Return the string path picture associated with the specific PokeType.
     */
    fun getPicture(resources : Resources, context: Context): Int {
        return resources.getIdentifier(name, "drawable", context.packageName)
    }
}