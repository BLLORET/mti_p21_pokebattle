package mti.p21.pokefight.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_lobby.*
import mti.p21.pokefight.MainActivity

import mti.p21.pokefight.R
import mti.p21.pokefight.adapter.PokemonModelAdapter
import mti.p21.pokefight.model.PokeType
import mti.p21.pokefight.model.PokemonModel

/**
 * [LobbyFragment] Represent the fragment to prepare to the next battle.
 */
class LobbyFragment : Fragment() {

    private lateinit var pokemons : List<PokemonModel>
    private lateinit var pokemonsOpponents : List<PokemonModel>
    private var team : MutableList<PokemonModel?> = MutableList(3) { null }
    private var selectedPokemon : PokemonModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lobby, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the list au pokemon sorted by type1 then type2 if it exists
        pokemons = (activity as MainActivity).data.sortedBy {pokemon ->
            pokemon.types.map { type -> type.name }.reduce { acc, pokeType -> acc + pokeType}
        }

        btn_fight.setOnClickListener {
            (activity as MainActivity).onFightClicked(team as List<PokemonModel>, pokemonsOpponents)
        }

        // Opponent zone
        pokemonsOpponents = getOpponents()
        setFirstOpponentInformations(pokemonsOpponents[0])
        setFirstOpponentsListeners()

        // RecyclerView zone
        val onPokemonLineClickListener = createPokemonLineClickListener()
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = PokemonModelAdapter(pokemons, activity!!, resources,
                                                   onPokemonLineClickListener)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                activity,
                LinearLayoutManager.VERTICAL
            )
        )

        // Choose Pokemons Team zone
        setChoosePokemonsTeamListeners()
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
        val firstOpponentTypes : List<PokeType> = pokemonsOpponents[0].types

        firstOpponent_type1_imageView.setOnClickListener {
            (activity as MainActivity).onTypeClicked(firstOpponentTypes[0])
        }

        if (firstOpponentTypes.size > 1)
        {
            firstOpponent_type2_imageView.setOnClickListener {
                (activity as MainActivity).onTypeClicked(firstOpponentTypes[1])
            }
        }
    }

    /**
     * Set view associated to the corresponding to the wanted
     * pokemon position with the selected pokemon
     */
    private fun setPokemonTeam(teamNumber : Int,
                               imageView: ImageView,
                               pokemonNameTextView : TextView) {
        if (selectedPokemon != null) {
            team[teamNumber] = selectedPokemon

            Glide
                .with(activity!!)
                .load(selectedPokemon!!.sprite)
                .into(imageView)

            pokemonNameTextView.text = selectedPokemon!!.name

            activateButtonFight()
        }
    }

    /**
     * Get random opponents in the pokemon database
     */
    private fun getOpponents() : List<PokemonModel> {
        val randomPokemons : List<PokemonModel> = pokemons.shuffled()
        return listOf(randomPokemons[0],
                      randomPokemons[1],
                      randomPokemons[2])
    }

    /**
     * Set informations associated to the first opponent in the fragment.
     */
    private fun setFirstOpponentInformations(opponent: PokemonModel) {
        firstOpponent_name_textView.text = opponent.name

        Glide
            .with(activity!!)
            .load(opponent.sprite)
            .into(firstOpponent_imageView)

        firstOpponent_type1_imageView.setImageResource(
            opponent.types[0].getPictureID(resources, activity!!)
        )

        firstOpponent_type2_imageView.setImageResource(
            if (opponent.types.size > 1)
                opponent.types[1].getPictureID(resources, activity!!)
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
                selectedPokemon!!.types[0].getPictureID(resources, activity!!)
            )

            selectedPokemon_type2_imageView.setImageResource(
                if (selectedPokemon!!.types.size > 1)
                    selectedPokemon!!.types[1].getPictureID(resources, activity!!)
                else
                    0
            )
        }
    }

    /**
     * Activate the button fight if the team is complete
     */
    private fun activateButtonFight() {
        if (!team.contains(null) && btn_fight.visibility != View.VISIBLE)
            btn_fight.visibility = View.VISIBLE
    }

    interface LobbyTypeClicked {
        /**
         * Open an help fragment.
         */
        fun onTypeClicked(pokeType: PokeType)
    }

    interface FightClicked {
        /**
         * Open the battle fragment.
         */
        fun onFightClicked(team : List<PokemonModel>, opponentTeam: List<PokemonModel>)
    }
}
