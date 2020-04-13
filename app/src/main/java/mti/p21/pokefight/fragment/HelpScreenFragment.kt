package mti.p21.pokefight.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_help_screen.*

import mti.p21.pokefight.R
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

    private var pokeType : PokeType? = null
    private lateinit var damageRelations : DamageRelations

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

            loadDamageRelations(pokeType!!.name)
            help_type_imageView.setImageResource(pokeType!!.getPictureID(resources, activity!!))
        }
    }

    /**
     * Load damage relations of a specific type in pokeApi and store it in [damageRelations]
     */
    private fun loadDamageRelations(name : String) {
        val baseURL = "https://pokeapi.co/api/v2/"
        val jsonConverter = GsonConverterFactory.create(GsonBuilder().create())
        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(jsonConverter)
            .build()

        val service : PokeApiInterface = retrofit.create(PokeApiInterface::class.java)

        val wsServiceCallback : Callback<TypeModel> = object : Callback<TypeModel> {
            override fun onFailure(call: Call<TypeModel>, t: Throwable) {
                Log.w("PokeApi", "Cannot load type $name")
            }

            override fun onResponse(call: Call<TypeModel>, response: Response<TypeModel>) {
                Log.w("Response: ", response.code().toString())
                if (response.code() == 200) {
                    response.body().let {
                        damageRelations = it!!.damage_relations
                        Log.w("it", it.toString())
                    }
                }
            }
        }

        service.getDamageRelations(pokeType!!.name).enqueue(wsServiceCallback)
    }
}
