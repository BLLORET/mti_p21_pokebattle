package mti.p21.pokefight

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.gson.GsonBuilder
import mti.p21.pokefight.fragment.HelpScreenFragment
import mti.p21.pokefight.fragment.LobbyFragment
import mti.p21.pokefight.fragment.PokedexListFragment
import mti.p21.pokefight.fragment.SplashScreenFragment
import mti.p21.pokefight.model.PokeType
import mti.p21.pokefight.model.PokemonModel
import mti.p21.pokefight.webServiceInterface.PokemonModelInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(),
                     SplashScreenFragment.SplashScreenButtonClicked,
                     LobbyFragment.LobbyTypeClicked {

    lateinit var data : List<PokemonModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadPokemonModels()

        setContentView(R.layout.activity_main)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container, SplashScreenFragment())
            .commit()
    }

    /**
     * Load Pokemons on the internet and store it in [data]
     */
    private fun loadPokemonModels() {
        val baseURL = "https://www.surleweb.xyz/api/"
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
                        data = (it as List<PokemonModel>).sortedBy {pokemon ->
                            pokemon.name
                        }
                    }
                }
            }
        }
        service.getAllPokemons().enqueue(wsServiceCallback)
    }

    override fun onBattleClicked() {
        goToFragment(LobbyFragment())
    }

    override fun onPokedexClicked() {
        goToFragment(PokedexListFragment())
    }

    private fun goToFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.main_container, fragment)
            .commit()
    }

    override fun onTypeClicked(pokeType : PokeType) {

        val argumentsBundle = Bundle()
        argumentsBundle.putSerializable("PokeType", pokeType)

        val helpScreenFragment = HelpScreenFragment()
        helpScreenFragment.arguments = argumentsBundle

        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .add(R.id.main_container, helpScreenFragment)
            .commit()
    }
}
