package mti.p21.pokefight

import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_battle.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mti.p21.pokefight.fragment.BattleFragment
import mti.p21.pokefight.fragment.BattleInteractionFragment
import mti.p21.pokefight.model.DamageRelations
import mti.p21.pokefight.model.MoveModel
import mti.p21.pokefight.model.SimplifiedPokemonDetails
import mti.p21.pokefight.utils.AbstractActivity
import mti.p21.pokefight.utils.CounterAction
import mti.p21.pokefight.utils.ExceptionDuringSuccess
import mti.p21.pokefight.utils.call
import mti.p21.pokefight.webServiceInterface.PokeApiInterface
import kotlin.math.max

object GameManager : ViewModel() {

    lateinit var mainActivity: AbstractActivity
    lateinit var battleFragment: BattleFragment
    lateinit var interactFragment: BattleInteractionFragment
    lateinit var team: List<SimplifiedPokemonDetails>
    lateinit var opponentTeam: List<SimplifiedPokemonDetails>

    var currentPokemonIndex = 0
    private var currentOpponentIndex = 0
    private const val delayTime = 2000L

    private val counterAction = CounterAction()

    fun init(mainActivity: AbstractActivity,
        team: List<SimplifiedPokemonDetails>,
        opponentTeam: List<SimplifiedPokemonDetails>
    ) {
        currentPokemonIndex = 0
        currentOpponentIndex = 0
        this.mainActivity = mainActivity
        this.team = team
        this.opponentTeam = opponentTeam
        counterAction.reset()
    }

    fun start(battleFragment: BattleFragment) {
        this.battleFragment = battleFragment
        counterAction.onCounterEnd = {
            loadCurrentPokemonInformation()
            loadOpponentPokemonInformation()
            interactFragment.buttons(true)
        }

        team.forEach { pokemon ->
            counterAction.increment()
            loadOnPokemonAPI(pokemon)
        }
        opponentTeam.forEach { pokemon ->
            counterAction.increment()
            loadOnPokemonAPI(pokemon)
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
    private fun loadOnPokemonAPI(pokemon: SimplifiedPokemonDetails) {
        mainActivity.service<PokeApiInterface>().getPokemonDetails(pokemon.name).call {
            onSuccess = {
                val pokemonDetail = it.body()
                    ?: throw ExceptionDuringSuccess("Body is null")
                pokemon.loadDetails(pokemonDetail)
                pokemonDetail.moves.forEach { moveObject ->
                    viewModelScope.launch {
                        counterAction.increment()
                        mainActivity.service<PokeApiInterface>()
                            .getMoveDetails(moveObject.move.name).call {
                                onSuccess = {res ->
                                    val moveModel = res.body()
                                        ?: throw ExceptionDuringSuccess("Body is null")
                                    if (moveModel.power > 0)
                                        pokemon.moves.add(moveModel)
                                    counterAction.decrement()
                                }
                                onFailure = {
                                    mainActivity.toastLong("Failed to load ${moveObject.move.name}")
                                }
                                onAnyErrorNoArg = { counterAction.decrement() }
                            }
                    }
                }
                counterAction.decrement()
            }
            onFailure = {
                mainActivity.toastLong("Failed to load stats of ${pokemon.name}")
                Log.w("PokeApi",
                    "Cannot load statistics of the ${pokemon.name} pokemon: $it")
            }
            onAnyErrorNoArg = { counterAction.decrement() }
        }
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
    private fun pokemonAttackTurn(
        pokemonAttacker: SimplifiedPokemonDetails,
        move: MoveModel?,
        pokemonDefender: SimplifiedPokemonDetails,
        loadPokemonInformationFunction: () -> Unit
    ) {
        // TODO : il serait interessant de faire autrement qu'avec un delay ici
        // Cette fonction n'est pas bloquante
        doDamages(pokemonAttacker, move, pokemonDefender, loadPokemonInformationFunction)
        //donc ce delay a interet à etre assez long!
    }

    /**
     * Make the current pokemon attack.
     */
    private fun currentPokemonAttackTurn(chosenMove: MoveModel) {
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
    private fun currentOpponentAttackTurn() {
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
        val infoText =
            if (win)
                mainActivity.getString(R.string.label_won)
            else
                mainActivity.getString(R.string.label_lost)
        mainActivity.informations_textView?.text = infoText
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
                if (!gameIsFinished())
                    mainActivity.onPokemonButtonClicked()
                else
                    battleOver(false)
            }
            else if (checkPokemonIsDead(currentOpponent, R.id.opponentPokemon_imageView)) {
                delay(delayTime)
                if (!gameIsFinished()) {
                    setNextOpponent()
                    loadOpponentPokemonInformation()
                } else
                    battleOver(true)
            }

            // Enables button when the turn is finished and set the information view
            val infoText = mainActivity.getString(R.string.interaction_select_action)
            battleFragment.informations_textView?.text = infoText
            interactFragment.buttons(true)
        }
    }

    /**
     * Load damages relation from a pokeType
     */
    private fun loadDamageRelations(pokeTypeName: String) : LiveData<DamageRelations> {
        val liveData = MutableLiveData<DamageRelations>()
        mainActivity.service<PokeApiInterface>().getDamageRelations(pokeTypeName).call {
            onSuccess = {
                liveData.value = it.body()?.damage_relations
                    ?: throw ExceptionDuringSuccess("Body is null!")
            }
        }
        return liveData
    }

    /**
     * Return the real damages of a move against another pokemon from an attacker.
     */
    private fun getDamagesOfMove(
        pokemonAttacker: SimplifiedPokemonDetails,
        pokemonDefender: SimplifiedPokemonDetails,
        move: MoveModel
    ): LiveData<Int> {
        val liveInt = MutableLiveData<Int>()
        val liveDamageRelations = loadDamageRelations(move.type.name)
        liveDamageRelations.observe(mainActivity, Observer {
            var damages: Int = calculateDamage(
                pokemonAttacker = pokemonAttacker,
                pokemonDefender = pokemonDefender,
                move = move,
                damageRelations = it,
                defenderType = pokemonDefender.types[0].name
            )

            if (pokemonDefender.types.size > 1) {
                damages = minOf(
                    damages,
                    calculateDamage(
                        pokemonAttacker = pokemonAttacker,
                        pokemonDefender = pokemonDefender,
                        move = move,
                        damageRelations = it,
                        defenderType = pokemonDefender.types[1].name
                    )
                )
            }
            liveInt.value = damages
        })
        return liveInt
    }

    /**
     * Get the most powerful move of the IA against us.
     */
    private fun getBetterIAMove(
        pokemonAttacker: SimplifiedPokemonDetails,
        pokemonDefender: SimplifiedPokemonDetails
    ) : LiveData<MoveModel> {
        val liveMove = MutableLiveData<MoveModel>()

        val counter = CounterAction()

        val models: List<Pair<MoveModel, LiveData<Int>>> = pokemonAttacker.moves.toList()
            .map {move ->
                counter.increment()
                val liveInt =
                    getDamagesOfMove(pokemonAttacker, pokemonDefender, move)

                liveInt.observe(mainActivity, Observer {
                    counter.decrement()
                })
                move to liveInt
            }

        counter.onCounterEnd = {
            liveMove.value = models.maxBy { it.second.value ?: -1 }?.first
        }

        return liveMove
    }

    /**
     * Apply damages from a pokemon to another with a specific move.
     * If move is null, the algorithm choose the most powerful attack.
     */
    private fun doDamages(
        pokemonAttacker: SimplifiedPokemonDetails,
        move: MoveModel?,
        pokemonDefender: SimplifiedPokemonDetails,
        then: () -> Unit
    ) {
        fun doDamages(currentMove: MoveModel) {
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

            val damagesLive: LiveData<Int> =
                getDamagesOfMove(pokemonAttacker, pokemonDefender, currentMove)

            damagesLive.observe(mainActivity, Observer {
                // Protect hp to become negative
                pokemonDefender.hp = max(0, pokemonDefender.hp - it)
                viewModelScope.launch {
                    delay(delayTime)
                    then()
                }
            })
        }

        // If move is null because it an IA, we find it's move
        if (move == null) {
            val moveModel = getBetterIAMove(pokemonAttacker, pokemonDefender)
            moveModel.observe(mainActivity, Observer {
                doDamages(it)
            })
        }
        else
            doDamages(move)
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
        pokemonType1ImageView?.setImageResource(
            pokemon.types[0].getPictureID(mainActivity.resources, mainActivity))

        val pokemonType2ImageView: ImageView? = mainActivity.findViewById(idPokemonType2Image)
        pokemonType2ImageView?.setImageResource(
            if (pokemon.types.size > 1)
                pokemon.types[1].getPictureID(mainActivity.resources, mainActivity)
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