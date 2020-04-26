package mti.p21.pokefight

import android.content.res.Resources
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_battle.*
import kotlinx.android.synthetic.main.fragment_battle_interaction.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mti.p21.pokefight.model.DamageRelations
import mti.p21.pokefight.model.MoveModel
import mti.p21.pokefight.model.PokemonDetailsModel
import mti.p21.pokefight.model.SimplifiedPokemonDetails
import mti.p21.pokefight.utils.AbstractActivity
import mti.p21.pokefight.webServiceInterface.PokeApiInterface
import retrofit2.Callback
import java.io.Serializable
import kotlin.math.max

class GameManager (
    private val mainActivity: AbstractActivity,
    val team: List<SimplifiedPokemonDetails>,
    private val opponentTeam: List<SimplifiedPokemonDetails>,
    private val resources: Resources
) : Serializable, ViewModel() {

    var currentPokemonIndex = 0
    private var currentOpponentIndex = 0
    private val delayTime = 2000L

    init {
        team.forEach { pokemon ->
            pokemon.detailsCounter++
            loadOnPokemonAPI(pokemon, pokemon.loadCallBackPokemonDetails(mainActivity))
            loadOnPokemonAPI(pokemon, pokemon.loadCallBackMoves(mainActivity))
        }
        opponentTeam.forEach { pokemon ->
            pokemon.detailsCounter++
            loadOnPokemonAPI(pokemon, pokemon.loadCallBackPokemonDetails(mainActivity))
            loadOnPokemonAPI(pokemon, pokemon.loadCallBackMoves(mainActivity))
        }

        // Coroutine wait for loading
        viewModelScope.launch {
            while (!team.all { it.detailsCounter == 0 && it.movesCounter == 0 } ||
                   !opponentTeam.all { it.detailsCounter == 0 && it.movesCounter == 0}) {
                delay(500)
            }
            loadCurrentPokemonInformation()
            loadOpponentPokemonInformation()

            mainActivity.btn_battle_pokemon?.isEnabled = true
            mainActivity.btn_battle_attack?.isEnabled = true
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
        mainActivity.service<PokeApiInterface>().getPokemonDetails(pokemon.name).enqueue(
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
            loadOpponentPokemonInformation()
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
            loadCurrentPokemonInformation()
        }
    }

    /**
     * Make pokemon hit them
     */
    private suspend fun actionTurn(chosenMove: MoveModel) {
        if (currentPokemon.speed >= currentOpponent.speed) {
            currentPokemonAttackTurn(chosenMove)
            delay(delayTime)
            if (currentOpponent.hp > 0)
                currentOpponentAttackTurn()
        } else {
            currentOpponentAttackTurn()
            delay(delayTime)
            if (currentPokemon.hp > 0)
                currentPokemonAttackTurn(chosenMove)
        }
    }

    /**
     * Determine if a pokemon is dead or alive, display information if he is dead
     */
    private fun checkPokemonIsDead(
        pokemon: SimplifiedPokemonDetails,
        idPokemonImage: Int
    ) : Boolean {
        if (pokemon.hp != 0) {
            return false
        }
        val infoDead = "${pokemon.name} is dead."
        mainActivity.informations_textView?.text = infoDead
        mainActivity.findViewById<ImageView>(idPokemonImage)?.setImageResource(0)

        return true
    }

    /**
     * Make the battle over and display a message to explain the player have lost or won.
     */
    private suspend fun battleOver(win: Boolean) {
        val infoText = if (win) mainActivity.getString(R.string.label_won)
                       else mainActivity.getString(R.string.label_lost)
        mainActivity.informations_textView?.text = infoText
        //TODO change this
        delay(delayTime * 4)
        mainActivity.backActivity()
    }

    /**
     * Represent the turn of battle
     */
    fun battleTurn(chosenMove: MoveModel) {

        viewModelScope.launch {

            actionTurn(chosenMove)
            delay(delayTime)

            if (checkPokemonIsDead(currentPokemon, R.id.currentPokemon_imageView)) {
                // Make the pokemon image empty
                delay(delayTime)
                if (!gameIsFinished()) {
                    mainActivity.onPokemonButtonClicked(this@GameManager)
                } else {
                    battleOver(false)
                }
            }
            else if (checkPokemonIsDead(currentOpponent, R.id.opponentPokemon_imageView)) {
                delay(delayTime)
                if (!gameIsFinished()) {
                    setNextOpponent()
                    loadOpponentPokemonInformation()
                } else {
                    battleOver(true)
                }
            }

            // Enables button when the turn is finished and set the information view
            val infoText = mainActivity.getString(R.string.interaction_select_action)
            mainActivity.informations_textView?.text = infoText
            mainActivity.btn_battle_attack?.isEnabled = true
            mainActivity.btn_battle_pokemon?.isEnabled = true
        }
    }

    /**
     * Load damages relation from a pokeType
     */
    private fun loadDamageRelations(pokeTypeName: String) : DamageRelations? {
        //TODO change this
            return mainActivity.service<PokeApiInterface>()
                .getDamageRelations(pokeTypeName)
                .execute().body()?.damage_relations
    }

    /**
     * Return the real damages of a move against another pokemon from an attacker.
     */
    private fun getDamagesOfMove(
        pokemonAttacker: SimplifiedPokemonDetails,
        pokemonDefender: SimplifiedPokemonDetails,
        move: MoveModel
    ): Int {
        val damageRelations: DamageRelations? = loadDamageRelations(move.type.name)

        var damages: Int = calculateDamage(
            pokemonAttacker = pokemonAttacker,
            pokemonDefender = pokemonDefender,
            move = move,
            damageRelations = damageRelations!!,
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
        return pokemonAttacker.moves.toList().maxBy { move ->
            getDamagesOfMove(
                pokemonAttacker,
                pokemonDefender,
                move
            )
        }!!
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

        var infoMove = "${pokemonAttacker.name} use ${currentMove.name}"
        val infoTextView: TextView? = mainActivity.informations_textView

        // Check if the move failed
        val randomNumber = (0..100).shuffled().first()
        if (randomNumber > currentMove.accuracy) {
            infoMove = "${pokemonAttacker.name} failed its move ${currentMove.name}"
            infoTextView?.text = infoMove
            return
        }

        infoTextView?.text = infoMove

        val damages: Int = getDamagesOfMove(pokemonAttacker, pokemonDefender, currentMove)

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

        val attackModifier: Float =
            if (physicalDamage)
                pokemonAttacker.attack.toFloat()
            else
                pokemonAttacker.attackSpe.toFloat()

        val defenseModifier: Float =
            if (physicalDamage)
                pokemonDefender.defense.toFloat()
            else
                pokemonDefender.defenseSpe.toFloat()


        return ((attackModifier / 10F + move.power - defenseModifier) * defTypeModifier).toInt()
    }


    /**
     * Load information of the selected pokemon with correct ids
     */
    private fun loadPokemonInformation(
        pokemon: SimplifiedPokemonDetails,
        idPokemonHP: Int,
        idPokemonName: Int,
        idPokemonImage: Int,
        idPokemonType1Image: Int,
        idPokemonType2Image: Int
    ) {
        mainActivity.findViewById<TextView>(idPokemonName)?.text = pokemon.name
        mainActivity.findViewById<TextView>(idPokemonHP)?.text = pokemon.hp.toString()

        val pokemonImageView: ImageView? = mainActivity.findViewById(idPokemonImage)

        if (pokemonImageView != null) {
            Glide
                .with(mainActivity)
                .load(pokemon.sprite)
                .into(pokemonImageView)
        }

        val pokemonType1ImageView: ImageView? = mainActivity.findViewById(idPokemonType1Image)
        pokemonType1ImageView?.setImageResource(pokemon.types[0].getPictureID(resources, mainActivity))

        val pokemonType2ImageView: ImageView? = mainActivity.findViewById(idPokemonType2Image)
        pokemonType2ImageView?.setImageResource(
            if (pokemon.types.size > 1)
                pokemon.types[1].getPictureID(resources, mainActivity)
            else
                0
        )
    }

    /**
     * Load information about the current selected pokemon,
     */
    fun loadCurrentPokemonInformation() {
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
     * Load information about the current opponent
     */
    private fun loadOpponentPokemonInformation() {
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