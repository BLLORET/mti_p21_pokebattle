package mti.p21.pokefight

import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mti.p21.pokefight.model.MoveModel
import mti.p21.pokefight.model.SimplifiedPokemonDetails
import mti.p21.pokefight.model.TypeModel
import mti.p21.pokefight.webServiceInterface.PokeApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.Serializable
import kotlin.math.max

class GameManager (
    val team : List<SimplifiedPokemonDetails>,
    private val opponentTeam : List<SimplifiedPokemonDetails>,
    var currentPokemonIndex : Int = 0,
    private var currentOpponentIndex : Int = 0,
    private val fragment: FragmentActivity
) : Serializable, ViewModel() {

    // Properties
    val currentPokemon : SimplifiedPokemonDetails
        get() = team[currentPokemonIndex]

    val currentOpponent : SimplifiedPokemonDetails
        get() = opponentTeam[currentOpponentIndex]

    /**
     * Set the next opponent
     */
    fun setNextOpponent() {
        ++currentOpponentIndex
    }

    /**
     * Determine if the player has won or if he has lost
     */
    private fun playerHasWon() : Boolean {
        return team.find { pokemon -> pokemon.hp != 0 } != null
    }

    /**
     * Determine if a team has all of it's pokemon ko.
     */
    private fun gameIsFinished() : Boolean {
        return team.all { pokemon -> pokemon.hp == 0 } ||
                opponentTeam.all { pokemon -> pokemon.hp == 0 }
    }

    /**
     * Represent the turn of battle
     */
    fun battleTurn(chosenMove : MoveModel, fragment: FragmentActivity) {

        viewModelScope.launch {
            val delayTime = 2000L

            // FIXME with the move of enemy
            val opponentMove: MoveModel = chosenMove

            // Action turn
            if (currentPokemon.speed >= currentOpponent.speed) {
                doDamages(currentPokemon, chosenMove, currentOpponent, fragment, true)

                delay(delayTime)

                if (currentOpponent.hp > 0) {
                    doDamages(currentOpponent, opponentMove, currentPokemon, fragment, false)
                }
            } else {
                doDamages(currentOpponent, opponentMove, currentPokemon, fragment, false)
                delay(delayTime)
                if (currentPokemon.hp > 0) {
                    doDamages(currentPokemon, chosenMove, currentOpponent, fragment, true)
                }
            }
            delay(delayTime)

            fragment.findViewById<Button>(R.id.btn_battle_attack).isEnabled = true
            fragment.findViewById<Button>(R.id.btn_battle_pokemon).isEnabled = true
        }
    }

    private fun doDamages(pokemonAttacker : SimplifiedPokemonDetails, move : MoveModel,
                          pokemonDefender: SimplifiedPokemonDetails, fragment : FragmentActivity,
                          isPlayer : Boolean) {

        val informationTextView : TextView = fragment.findViewById(R.id.informations_textView)
        val infoMove = "${pokemonAttacker.name} use ${move.name}"
        informationTextView.text = infoMove

        // Begin to get defense modifier
        var defenderTypeModifier = 1F

        val attackerType = move.type.name
        // FIXME with the professor mail
        val defenderType = pokemonDefender.types[0].name

        // Call the api for it
        val baseURL = "https://pokeapi.co/api/v2/"
        val jsonConverter = GsonConverterFactory.create(GsonBuilder().create())
        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(jsonConverter)
            .build()

        val service : PokeApiInterface = retrofit.create(PokeApiInterface::class.java)

        val wsServiceCallback : Callback<TypeModel> = object : Callback<TypeModel> {
            override fun onFailure(call: Call<TypeModel>, t: Throwable) {
                Log.w("PokeApi", "Cannot load type $attackerType")
            }

            override fun onResponse(call: Call<TypeModel>, response: Response<TypeModel>) {
                Log.w("Response: ", response.code().toString())
                if (response.code() == 200) {
                    response.body()?.let { typeModel ->


                        val damageRelations = typeModel.damage_relations

                        when {
                            damageRelations.double_damage_to.any { it.name == defenderType }
                                -> defenderTypeModifier = 2F
                            damageRelations.half_damage_to.any { it.name == defenderType }
                                -> defenderTypeModifier = 0.5F
                            damageRelations.no_damage_to.any { it.name == defenderType }
                                -> defenderTypeModifier = 0F
                        }

                        // Set to special by default
                        var attackModifier : Float = pokemonAttacker.attackSpe.toFloat()
                        var defenseModifier : Float = pokemonDefender.defenseSpe.toFloat()

                        // Set it to physical if the move is a physical attack
                        if (move.damage_class.name == "physical") {
                            attackModifier = pokemonAttacker.attack.toFloat()
                            defenseModifier = pokemonDefender.defense.toFloat()
                        }

                        val damages : Int = ((attackModifier / 10F + move.power - defenseModifier)
                                            * defenderTypeModifier).toInt()

                        // Protect hp to become negative
                        pokemonDefender.hp = max(0, pokemonDefender.hp - damages)

                        if (isPlayer) {
                            loadOpponentPokemonInformations()
                        } else {
                            loadCurrentPokemonInformations()
                        }
                    }
                }
            }
        }

        service.getDamageRelations(attackerType).enqueue(wsServiceCallback)
    }

    /**
     * Load informations of the selected pokemon with correct ids
     */
    private fun loadPokemonInformation(pokemon: SimplifiedPokemonDetails,
                                       idPokemonHP : Int, idPokemonName : Int,
                                       idPokemonImage : Int, loadAPI : Boolean) {
        val pokemonHpTextView: TextView = fragment.findViewById(idPokemonHP)

        // Do not load if it is already loaded in api
        if (!loadAPI) {
            pokemonHpTextView.text = pokemon.hp.toString()
        } else {
            pokemon.loadDetails {
                pokemonHpTextView.text = pokemon.hp.toString()
            }

            pokemon.loadMoves(fragment)
        }

        val pokemonNameTextView : TextView = fragment.findViewById(idPokemonName)
        pokemonNameTextView.text= pokemon.name

        val pokemonImageView : ImageView = fragment.findViewById(idPokemonImage)

        Glide
            .with(fragment)
            .load(pokemon.sprite)
            .into(pokemonImageView)
    }

    /**
     * Load information about the current selected pokemon,
     * the parameter loadAPI tell the function if it need to load the API
     */
    fun loadCurrentPokemonInformations(loadAPI: Boolean = false) {

        loadPokemonInformation(
            pokemon = currentPokemon,
            idPokemonHP = R.id.currentPokemonHP_textView,
            idPokemonName = R.id.currentPokemonName_textView,
            idPokemonImage = R.id.currentPokemon_imageView,
            loadAPI = loadAPI
        )
    }

    /**
     * Load informations about the current
     */
    fun loadOpponentPokemonInformations(loadAPI: Boolean = false) {
        loadPokemonInformation(
            pokemon = currentOpponent,
            idPokemonHP = R.id.opponentPokemonHP_textView,
            idPokemonName = R.id.opponentPokemonName_textView,
            idPokemonImage = R.id.opponentPokemon_imageView,
            loadAPI = loadAPI
        )
    }
}