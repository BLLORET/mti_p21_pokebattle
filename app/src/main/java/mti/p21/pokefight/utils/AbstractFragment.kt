package mti.p21.pokefight.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment

abstract class AbstractFragment(val mainActivity: AbstractActivity) : Fragment() {

    abstract val layoutResource: Int

    fun toast(msg: String) = Toast.makeText(this.context, msg, Toast.LENGTH_SHORT).show()
    fun toastLong(msg: String) = Toast.makeText(this.context, msg, Toast.LENGTH_LONG).show()

    inline fun <reified T> service(): T = mainActivity.retrofit.create(T::class.java)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(layoutResource, container, false)
    }

}