package mti.p21.pokefight.fragment

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_lobby.*
import mti.p21.pokefight.R
import mti.p21.pokefight.adapter.PokemonModelAdapter
import mti.p21.pokefight.model.PokeType
import mti.p21.pokefight.model.PokemonModel
import mti.p21.pokefight.utils.AbstractActivity
import mti.p21.pokefight.utils.AbstractFragment

/**
 * [LobbyFragment] Represent the fragment to prepare to the next battle.
 */
class LobbyFragment(a: AbstractActivity) : AbstractFragment(a) {

    override val layoutResource = R.layout.fragment_lobby

    private lateinit var pokemons: List<PokemonModel>
    private lateinit var pokemonsOpponents: List<PokemonModel>
    private lateinit var team: MutableList<PokemonModel?>
    private var selectedPokemon: PokemonModel? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Reset after a battle
        selectedPokemon = null
        btn_fight.visibility = View.INVISIBLE
        team = MutableList(3) { null }

        mainActivity.executeWhenListPokemonIsLoaded(this) {
            // Get the list au pokemon sorted by type1 then type2 if it exists
            pokemons = it.sortedBy { pokemon ->
                pokemon.types.map { t -> t.name }.reduce { acc, pokeType -> acc + pokeType }
            }

            btn_fight.setOnClickListener {
                // Cast mutable? to list
                val teamList = listOf(team[0]!!, team[1]!!, team[2]!!)
                mainActivity.onFightButtonClicked(teamList, pokemonsOpponents)
            }

            // Opponent zone
            pokemonsOpponents = getOpponents()
            setFirstOpponentInformation(pokemonsOpponents[0])
            setFirstOpponentsListeners()

            // RecyclerView zone
            val onPokemonLineClickListener = createPokemonLineClickListener()
            recyclerView.layoutManager = LinearLayoutManager(mainActivity)
            recyclerView.adapter =
                PokemonModelAdapter(pokemons, mainActivity, resources, onPokemonLineClickListener)
            recyclerView.addItemDecoration(
                DividerItemDecoration(mainActivity, LinearLayoutManager.VERTICAL))

            // Choose Pokemons Team zone
            setChoosePokemonsTeamListeners()
        }
    }

    /**
     * Set listeners about the pokemon team zone.
     */
    private fun setChoosePokemonsTeamListeners() {
        btn_choosePokemon1.setOnClickListener {
            setPokemonTeam(0, chosenPokemon1_imageView, chosenPokemon1_name_textView)
        }

        btn_choosePokemon2.setOnClickListener {
            setPokemonTeam(1, chosenPokemon2_imageView, chosenPokemon2_name_textView)
        }

        btn_choosePokemon3.setOnClickListener {
            setPokemonTeam(2, chosenPokemon3_imageView, chosenPokemon3_name_textView)
        }
    }

    /**
     * Set listeners on the first opponent to see help associated.
     */
    private fun setFirstOpponentsListeners() {
        val firstOpponentTypes: List<PokeType> = pokemonsOpponents.first().types

        firstOpponent_type1_imageView.setOnClickListener {
            mainActivity.onTypePictureClicked(firstOpponentTypes[0])
        }

        if (firstOpponentTypes.size > 1)
        {
            firstOpponent_type2_imageView.setOnClickListener {
                mainActivity.onTypePictureClicked(firstOpponentTypes[1])
            }
        }
    }

    /**
     * Set view associated to the corresponding to the wanted
     * pokemon position with the selected pokemon
     */
    private fun setPokemonTeam(
        teamNumber: Int,
        imageView: ImageView,
        pokemonNameTextView: TextView
    ) {
        if (selectedPokemon != null) {
            team[teamNumber] = selectedPokemon

            Glide
                .with(mainActivity)
                .load(selectedPokemon!!.sprite)
                .into(imageView)

            pokemonNameTextView.text = selectedPokemon!!.name

            activateButtonFight()
        }
    }

    /**
     * Get random opponents in the pokemon database
     */
    private fun getOpponents(): List<PokemonModel> {
        return pokemons.shuffled().subList(0, 3)
    }

    /**
     * Set information associated to the first opponent in the fragment.
     */
    private fun setFirstOpponentInformation(opponent: PokemonModel) {
        firstOpponent_name_textView.text = opponent.name

        Glide
            .with(mainActivity)
            .load(opponent.sprite)
            .into(firstOpponent_imageView)

        firstOpponent_type1_imageView.setImageResource(
            opponent.types[0].getPictureID(resources, mainActivity)
        )

        firstOpponent_type2_imageView.setImageResource(
            if (opponent.types.size > 1)
                opponent.types[1].getPictureID(resources, mainActivity)
            else 0
        )
    }

    /**
     * Create a listener on line in the recycler view to set the selected pokemon.
     */
    private fun createPokemonLineClickListener() : View.OnClickListener {
        return View.OnClickListener { clickedView ->
            selectedPokemon = pokemons[clickedView.tag as Int]

            selectedPokemon_name_textView.text = selectedPokemon!!.name

            selectedPokemon_type1_imageView.setImageResource(
                selectedPokemon!!.types[0].getPictureID(resources, mainActivity)
            )

            selectedPokemon_type2_imageView.setImageResource(
                if (selectedPokemon!!.types.size > 1)
                    selectedPokemon!!.types[1].getPictureID(resources, mainActivity)
                else
                    0
            )
        }
    }

    /**
     * Activate the button fight if the team is complete
     */
    private fun activateButtonFight() {
        if (!team.contains(null) && btn_fight.visibility != View.VISIBLE) {
            btn_fight.visibility = View.VISIBLE
        }
    }
}
