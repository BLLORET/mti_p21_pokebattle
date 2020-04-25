package mti.p21.pokefight

import android.content.res.Resources
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
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable
import kotlin.math.max

class GameManager (
    val team: List<SimplifiedPokemonDetails>,
    private val opponentTeam: List<SimplifiedPokemonDetails>,
    private val fragment: FragmentActivity,
    private val resources: Resources
) : Serializable, ViewModel() {

    var currentPokemonIndex: Int = 0
    private var currentOpponentIndex: Int = 0
    private val delayTime: Long = 2000L

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

    private val currentOpponent: SimplifiedPokemonDetails
        get() = opponentTeam[currentOpponentIndex]

    /**
     * Use a Callback function on the pokemon API to a specif pokemon
     */
    private fun loadOnPokemonAPI(
        pokemon: SimplifiedPokemonDetails,
        loadFunction: Callback<PokemonDetailsModel>
    ) {
        pokemon.pokeApiInterface.getPokemonDetails(pokemon.name).enqueue(
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
     * Make the pokemon attack.
     */
    private suspend fun pokemonAttackTurn(
        pokemonAttacker: SimplifiedPokemonDetails,
        move: MoveModel?,
        pokemonDefender: SimplifiedPokemonDetails,
        loadPokemonInformationFunction: () -> Unit
    ) {
        doDamages(pokemonAttacker, move, pokemonDefender)
        delay(delayTime)
        loadPokemonInformationFunction()
    }

    /**
     * Make the current pokemon attack.
     */
    private suspend fun currentPokemonAttackTurn(chosenMove: MoveModel) {
        pokemonAttackTurn(
            pokemonAttacker = currentPokemon,
            pokemonDefender = currentOpponent,
            move = chosenMove) {
            loadOpponentPokemonInformations()
        }
    }

    /**
     * Make the opponent attack.
     */
    private suspend fun currentOpponentAttackTurn() {
        pokemonAttackTurn(
            pokemonAttacker = currentOpponent,
            pokemonDefender = currentPokemon,
            move = null) {
            loadCurrentPokemonInformations()
        }
    }

    /**
     * Make pokemon hit them
     */
    private suspend fun actionTurn(chosenMove: MoveModel) {
        if (currentPokemon.speed >= currentOpponent.speed) {
            currentPokemonAttackTurn(chosenMove)
            delay(delayTime)
            if (currentOpponent.hp > 0) {
                currentOpponentAttackTurn()
            }
        } else {
            currentOpponentAttackTurn()
            delay(delayTime)
            if (currentPokemon.hp > 0) {
                currentPokemonAttackTurn(chosenMove)
            }
        }
    }

    /**
     * Determine if a pokemon is dead or alive, display information if he is dead
     */
    private fun checkPokemonIsDead(pokemon: SimplifiedPokemonDetails) : Boolean {
        if (pokemon.hp != 0) {
            return false
        }
        val infoDead = "${pokemon.name} is dead."
        fragment.findViewById<TextView>(R.id.informations_textView).text = infoDead

        return true
    }

    /**
     * Make the battle over and display a message to explain the player have lost or won.
     */
    private suspend fun battleOver(win: Boolean) {
        val infoText = if (win) fragment.getString(R.string.label_won)
                       else fragment.getString(R.string.label_lost)
        fragment.findViewById<TextView>(R.id.informations_textView).text = infoText
        delay(delayTime * 4)
        (fragment as MainActivity).supportFragmentManager.popBackStack()
    }

    /**
     * Represent the turn of battle
     */
    fun battleTurn(chosenMove: MoveModel, fragment: FragmentActivity) {

        viewModelScope.launch {

            actionTurn(chosenMove)
            delay(delayTime)

            if (checkPokemonIsDead(currentPokemon)) {
                delay(delayTime)
                if (!gameIsFinished())
                    (fragment as MainActivity).onPokemonButtonClicked(this@GameManager)
                else {
                    battleOver(false)
                }
            }
            else if (checkPokemonIsDead(currentOpponent)) {
                delay(delayTime)
                if (!gameIsFinished()) {
                    setNextOpponent()
                    loadOpponentPokemonInformations()
                } else {
                    battleOver(true)
                }
            }

            // Enables button when the turn is finished
            fragment.findViewById<Button>(R.id.btn_battle_attack).isEnabled = true
            fragment.findViewById<Button>(R.id.btn_battle_pokemon).isEnabled = true
        }
    }

    /**
     * Load damages relation from a pokeType, BE CAREFUL! THIS IS SYNCHRONOUS!!!!
     */
    private fun loadDamageRelations(pokeTypeName: String) : DamageRelations {
        // Make it synchronous
        lateinit var request : Response<TypeModel>
        do {
            request = currentPokemon.pokeApiInterface.getDamageRelations(pokeTypeName).execute()
        } while (!request.isSuccessful || request.code() != 200)

        return request.body()!!.damage_relations
    }

    /**
     * Return the real damages of a move against another pokemon from an attacker.
     */
    private fun getDamagesOfMove(
        pokemonAttacker: SimplifiedPokemonDetails,
        pokemonDefender: SimplifiedPokemonDetails,
        move: MoveModel
    ): Int {
        val damageRelations = loadDamageRelations(move.type.name)

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

    /**
     * Get the most powerful move of the IA against us.
     */
    private fun getBetterIAMove(
        pokemonAttacker: SimplifiedPokemonDetails,
        pokemonDefender: SimplifiedPokemonDetails
    ) : MoveModel {

        return pokemonAttacker.moves.toList().sortedByDescending {move ->
            getDamagesOfMove(
                pokemonAttacker,
                pokemonDefender,
                move
            )
        }[0]
    }

    /**
     * Apply damages from a pokemon to another with a specific move.
     * If move is null, the algorithm choose the most powerful attack.
     */
    private fun doDamages(
        pokemonAttacker: SimplifiedPokemonDetails,
        move: MoveModel?,
        pokemonDefender: SimplifiedPokemonDetails
    ) {
        // If move is null because it an IA, we find it's move
        val currentMove = move ?: getBetterIAMove(pokemonAttacker, pokemonDefender)

        val infoMove = "${pokemonAttacker.name} use ${currentMove.name}"
        fragment.findViewById<TextView>(R.id.informations_textView).text = infoMove

        val damages: Int = getDamagesOfMove(
            pokemonAttacker = pokemonAttacker,
            pokemonDefender = pokemonDefender,
            move = currentMove
        )

        // Protect hp to become negative
        pokemonDefender.hp = max(0, pokemonDefender.hp - damages)
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