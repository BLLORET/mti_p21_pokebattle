package mti.p21.pokefight.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_help_screen.*
import mti.p21.pokefight.R
import mti.p21.pokefight.adapter.HelpTypeAdapter
import mti.p21.pokefight.model.DamageRelations
import mti.p21.pokefight.model.PokeType
import mti.p21.pokefight.utils.AbstractActivity
import mti.p21.pokefight.utils.AbstractFragment
import mti.p21.pokefight.utils.ExceptionDuringSuccess
import mti.p21.pokefight.utils.call
import mti.p21.pokefight.webServiceInterface.PokeApiInterface

/**
 * A simple [Fragment] subclass.
 */
class HelpScreenFragment(a: AbstractActivity) : AbstractFragment(a) {

    override val layoutResource = R.layout.fragment_help_screen

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val pokeType = it.getSerializable("PokeType") as PokeType

            loadDamageRelations(pokeType.name)
            helpType_imageView.setImageResource(pokeType.getPictureID(resources, activity!!))
        }
    }

    /**
     * Load damage relations of a specific type in pokeApi and display them in the fragment
     */
    private fun loadDamageRelations(pokeTypeName: String) {
        service<PokeApiInterface>().getDamageRelations(pokeTypeName).call {
            onSuccess = {
                val typeModel = it.body()
                    ?: throw ExceptionDuringSuccess("Body is null!")
                setGridViewAdapters(typeModel.damage_relations)
            }
        }
    }

    /**
     * Set a grid view adapter with the good list of pokeType
     */
    private fun setGridViewAdapter(recyclerView: RecyclerView, damages: List<PokeType>) {
        recyclerView.layoutManager = GridLayoutManager(mainActivity, 3)
        recyclerView.adapter = HelpTypeAdapter(damages, mainActivity, resources)
    }

    /**
     * Set all grid view in the current fragment
     */
    private fun setGridViewAdapters(damageRelations: DamageRelations) {
        // x2
        setGridViewAdapter(doubleDamageFrom_recyclerView, damageRelations.double_damage_from)
        setGridViewAdapter(doubleDamageTo_recyclerView, damageRelations.double_damage_to)

        // x0.5
        setGridViewAdapter(halfDamageFrom_recyclerView, damageRelations.half_damage_from)
        setGridViewAdapter(halfDamageTo_recyclerView, damageRelations.half_damage_to)

        // X0
        setGridViewAdapter(noDamageFrom_recyclerView, damageRelations.no_damage_from)
        setGridViewAdapter(noDamageTo_recyclerView, damageRelations.no_damage_to)
    }
}
