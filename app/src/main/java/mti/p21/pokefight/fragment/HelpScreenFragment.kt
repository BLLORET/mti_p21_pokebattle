package mti.p21.pokefight.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_help_screen.*

import mti.p21.pokefight.R
import mti.p21.pokefight.model.PokeType

/**
 * A simple [Fragment] subclass.
 */
class HelpScreenFragment : Fragment() {

    private var pokeType : PokeType? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_help_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            pokeType = it.getSerializable("PokeType") as PokeType
            help_type_imageView.setImageResource(pokeType!!.getPictureID(resources, activity!!))
        }
    }
}
