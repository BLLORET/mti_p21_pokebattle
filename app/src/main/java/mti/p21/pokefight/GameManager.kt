package mti.p21.pokefight

import android.content.res.Resources
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mti.p21.pokefight.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable
import kotlin.math.max

class GameManager (
    val team: List<SimplifiedPokemonDetails>,
    private val opponentTeam: List<SimplifiedPokemonDetails>,
    var currentPokemonIndex: Int = 0,
    private var currentOpponentIndex: Int = 0,
    private val fragment: FragmentActivity,
    private val resources: Resources
) : Serializable, ViewModel() {

    init {
        team.forEach { pokemon ->
            pokemon.detailsCounter++
            loadOnPokemonAPI(pokemon, pokemon.loadCallBackPokemonDetails(fragment))
            loadOnPokemonAPI(pokemon, pokemon.loadCallBackMoves(fragment))
        }
        opponentTeam.forEach { pokemon ->
            pokemon.detailsCounter++
            loadOnPokemonAPI(pokemon, pokemon.loadCallBackPokemonDetails(fragment))
            loadOnPokemonAPI(pokemon, pokemon.loadCallBackMoves(fragment))
        }

        // Coroutine wait for loading
        viewModelScope.launch {
            while (!team.all { it.detailsCounter == 0 && it.movesCounter == 0 } ||
                   !opponentTeam.all { it.detailsCounter == 0 && it.movesCounter == 0}) {
                delay(500)
            }
            loadCurrentPokemonInformations()
            loadOpponentPokemonInformations()

            fragment.findViewById<Button>(R.id.btn_battle_pokemon).isEnabled = true
            fragment.findViewById<Button>(R.id.btn_battle_attack).isEnabled = true
        }
    }

    // Properties
    val currentPokemon: SimplifiedPokemonDetails
        get() = team[currentPokemonIndex]

    val currentOpponent: SimplifiedPokemonDetails
        get() = opponentTeam[currentOpponentIndex]

    /**
     * Use a Callback function on the pokemon API to a specif pokemon
     */
    private fun loadOnPokemonAPI(
        pokemon: SimplifiedPokemonDetails,
        loadFunction: Callback<PokemonDetailsModel>
    ) {
        pokemon.getPokeAPIService().getPokemonDetails(pokemon.name).enqueue(
            loadFunction
        )
    }

    /**
     * Set the next opponent
     */
    private fun setNextOpponent() {
        ++currentOpponentIndex
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
    fun battleTurn(chosenMove: MoveModel, fragment: FragmentActivity) {

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

            val isFinished: Boolean = gameIsFinished()

            if (currentPokemon.hp == 0) {
                val informationTextView: TextView = fragment.findViewById(R.id.informations_textView)
                val infoDead = "${currentPokemon.name} is dead."
                informationTextView.text = infoDead
                delay(delayTime)
                if (!isFinished)
                    (fragment as MainActivity).onPokemonButtonClicked(this@GameManager)
                else {
                    val lostString = "You have lost the battle..."
                    informationTextView.text = lostString
                    delay(delayTime * 4)
                    (fragment as MainActivity).supportFragmentManager.popBackStack()
                }
            }
            else if (currentOpponent.hp == 0) {
                val informationTextView: TextView = fragment.findViewById(R.id.informations_textView)
                val infoDead = "${currentOpponent.name} is dead."
                informationTextView.text = infoDead

                delay(delayTime)
                if (!isFinished) {
                    setNextOpponent()
                    loadOpponentPokemonInformations()
                } else {
                    val wonString = "You have won the battle..."
                    informationTextView.text = wonString
                    delay(delayTime * 4)
                    (fragment as MainActivity).supportFragmentManager.popBackStack()
                }
            }

            fragment.findViewById<Button>(R.id.btn_battle_attack).isEnabled = true
            fragment.findViewById<Button>(R.id.btn_battle_pokemon).isEnabled = true
        }
    }

    private fun getDamagesOfMove(
        pokemonAttacker: SimplifiedPokemonDetails,
        pokemonDefender: SimplifiedPokemonDetails,
        move: MoveModel,
        damageRelations: DamageRelations
    ): Int {
        var damages: Int = calculateDamage(
            pokemonAttacker = pokemonAttacker,
            pokemonDefender = pokemonDefender,
            move = move,
            damageRelations = damageRelations,
            defenderType = pokemonDefender.types[0].name
        )

        if (pokemonDefender.types.size > 1) {
            damages = minOf(
                damages,
                calculateDamage(
                    pokemonAttacker = pokemonAttacker,
                    pokemonDefender = pokemonDefender,
                    move = move,
                    damageRelations = damageRelations,
                    defenderType = pokemonDefender.types[1].name
                )
            )
        }

        return damages
    }

    private fun doDamages(
        pokemonAttacker: SimplifiedPokemonDetails,
        move: MoveModel,
        pokemonDefender: SimplifiedPokemonDetails,
        fragment: FragmentActivity,
        isPlayer: Boolean) {

        val informationTextView: TextView = fragment.findViewById(R.id.informations_textView)
        val infoMove = "${pokemonAttacker.name} use ${move.name}"
        informationTextView.text = infoMove

        val attackerType: String = move.type.name


        // pokeAPI is attached to a pokemon
        pokemonAttacker.getPokeAPIService().getDamageRelations(attackerType).enqueue(
            object : Callback<TypeModel> {
                override fun onFailure(call: Call<TypeModel>, t: Throwable) {
                    Log.w("PokeApi", "Cannot load type $attackerType")
                }

                override fun onResponse(call: Call<TypeModel>, response: Response<TypeModel>) {
                    Log.w("Response: ", response.code().toString())
                    if (response.code() == 200) {
                        response.body()?.let { typeModel ->

                            val damages = getDamagesOfMove(
                                pokemonAttacker = pokemonAttacker,
                                pokemonDefender = pokemonDefender,
                                move = move,
                                damageRelations = typeModel.damage_relations
                            )

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
        )
    }

    /**
     * Get the coefficient of defender type.
     */
    private fun getDefenderTypeModifier(damages: DamageRelations, defType: String) : Float {
        return when {
            damages.double_damage_to.any { it.name == defType } -> 2F
            damages.half_damage_to.any { it.name == defType } -> 0.5F
            damages.no_damage_to.any { it.name == defType } -> 0F
            else -> 1F
        }
    }

    /**
     * Calculate damages from an attack to a pokemon with a specific type.
     */
    private fun calculateDamage(
        pokemonAttacker: SimplifiedPokemonDetails,
        pokemonDefender: SimplifiedPokemonDetails,
        move: MoveModel,
        damageRelations: DamageRelations,
        defenderType: String
    ) : Int {

        val defTypeModifier = getDefenderTypeModifier(damageRelations, defenderType)
        val physicalDamage : Boolean = move.damage_class.name == "physical"

        val attackModifier: Float = if (physicalDamage) pokemonAttacker.attack.toFloat()
                                    else pokemonAttacker.attackSpe.toFloat()

        val defenseModifier: Float = if (physicalDamage) pokemonDefender.defense.toFloat()
                                     else pokemonDefender.defenseSpe.toFloat()


        return ((attackModifier / 10F + move.power - defenseModifier) * defTypeModifier).toInt()
    }


    /**
     * Load informations of the selected pokemon with correct ids
     */
    private fun loadPokemonInformation(
        pokemon: SimplifiedPokemonDetails,
        idPokemonHP: Int,
        idPokemonName: Int,
        idPokemonImage: Int,
        idPokemonType1Image: Int,
        idPokemonType2Image: Int
    ) {
        fragment.findViewById<TextView>(idPokemonName).text = pokemon.name
        fragment.findViewById<TextView>(idPokemonHP).text = pokemon.hp.toString()

        Glide
            .with(fragment)
            .load(pokemon.sprite)
            .into(fragment.findViewById(idPokemonImage))

        val pokemonType1ImageView: ImageView = fragment.findViewById(idPokemonType1Image)
        pokemonType1ImageView.setImageResource(pokemon.types[0].getPictureID(resources, fragment))

        val pokemonType2ImageView: ImageView = fragment.findViewById(idPokemonType2Image)
        pokemonType2ImageView.setImageResource(
            if (pokemon.types.size > 1) pokemon.types[1].getPictureID(resources, fragment)
            else 0
        )
    }

    /**
     * Load information about the current selected pokemon,
     */
    fun loadCurrentPokemonInformations() {
        loadPokemonInformation(
            pokemon = currentPokemon,
            idPokemonHP = R.id.currentPokemonHP_textView,
            idPokemonName = R.id.currentPokemonName_textView,
            idPokemonImage = R.id.currentPokemon_imageView,
            idPokemonType1Image = R.id.currentPokemonType1_imageView,
            idPokemonType2Image = R.id.currentPokemonType2_imageView
        )
    }

    /**
     * Load informations about the current opponent
     */
    private fun loadOpponentPokemonInformations() {
        loadPokemonInformation(
            pokemon = currentOpponent,
            idPokemonHP = R.id.opponentPokemonHP_textView,
            idPokemonName = R.id.opponentPokemonName_textView,
            idPokemonImage = R.id.opponentPokemon_imageView,
            idPokemonType1Image = R.id.opponentPokemonType1_imageView,
            idPokemonType2Image = R.id.opponentPokemonType2_imageView
        )
    }
}