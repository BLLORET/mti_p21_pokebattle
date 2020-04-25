package mti.p21.pokefight.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_help_screen.*

import mti.p21.pokefight.R
import mti.p21.pokefight.adapter.HelpTypeAdapter
import mti.p21.pokefight.model.DamageRelations
import mti.p21.pokefight.model.PokeType
import mti.p21.pokefight.model.TypeModel
import mti.p21.pokefight.webServiceInterface.PokeApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * A simple [Fragment] subclass.
 */
class HelpScreenFragment : Fragment() {

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
            val pokeType = it.getSerializable("PokeType") as PokeType

            loadDamageRelations(pokeType.name)
            helpType_imageView.setImageResource(pokeType.getPictureID(resources, activity!!))
        }
    }

    /**
     * Load damage relations of a specific type in pokeApi and display them in the fragment
     */
    private fun loadDamageRelations(pokeTypeName: String) {
        val service = Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(
                GsonConverterFactory.create(GsonBuilder().create())
            )
            .build()
            .create(PokeApiInterface::class.java)

        val wsServiceCallback = object: Callback<TypeModel> {
            override fun onFailure(call: Call<TypeModel>, t: Throwable) {
                Log.w("PokeApi", "Cannot load type $pokeTypeName")
            }

            override fun onResponse(call: Call<TypeModel>, response: Response<TypeModel>) {
                Log.w("Response: ", response.code().toString())
                if (response.code() == 200) {
                    response.body()?.let { typeModel ->
                        setGridViewAdapters(typeModel.damage_relations)
                    }
                }
            }
        }

        service.getDamageRelations(pokeTypeName).enqueue(wsServiceCallback)
    }

    /**
     * Set a grid view adapter with the good list of pokeType
     */
    private fun setGridViewAdapter(recyclerView: RecyclerView, damages: List<PokeType>) {
        recyclerView.layoutManager = GridLayoutManager(activity!!, 3)
        recyclerView.adapter = HelpTypeAdapter(damages, activity!!, resources)
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
