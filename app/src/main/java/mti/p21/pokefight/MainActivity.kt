package mti.p21.pokefight

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.GsonBuilder
import mti.p21.pokefight.fragment.PokedexListFragment
import mti.p21.pokefight.fragment.SplashScreenFragment
import mti.p21.pokefight.model.PokemonModel
import mti.p21.pokefight.webServiceInterface.PokemonModelInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.Serializable

class MainActivity : AppCompatActivity(), SplashScreenFragment.SplashScreenButtonClicked {

    var data : List<PokemonModel> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentTransaction = supportFragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.main_container,
            SplashScreenFragment()
        )
        fragmentTransaction.commit()

        getPokemonModels()
    }

    private fun getPokemonModels() {
        val baseURL = "http://www.surleweb.xyz/api/"
        val jsonConverter = GsonConverterFactory.create(GsonBuilder().create())
        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(jsonConverter)
            .build()

        val service : PokemonModelInterface = retrofit.create(PokemonModelInterface::class.java)


        val wsServiceCallback : Callback<List<PokemonModel>> = object : Callback<List<PokemonModel>> {
            override fun onFailure(call: Call<List<PokemonModel>>, t: Throwable) {
                Log.w("Pokemon", "WebService call failed")
            }

            override fun onResponse(
                call: Call<List<PokemonModel>>,
                response: Response<List<PokemonModel>>
            ) {
                if (response.code() == 200) {
                    response.body().let {
                        data = it as List<PokemonModel>
                    }
                }
            }
        }
        service.getAllPokemons().enqueue(wsServiceCallback)
    }

    override fun onBattleClicked() {
        TODO("Not yet implemented")
    }

    override fun onPokedexClicked() {

        val jsonBuilder = GsonBuilder().create()
        val dataJson : String = jsonBuilder.toJson(data)

        val argumentBundle = Bundle()

        argumentBundle.putSerializable("Pokemons", dataJson)

        val pokedexListFragment = PokedexListFragment()
        pokedexListFragment.arguments = argumentBundle

        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.main_container, pokedexListFragment)
            .commit()
    }
}
