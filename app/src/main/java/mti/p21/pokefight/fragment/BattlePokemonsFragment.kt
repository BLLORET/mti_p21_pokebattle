package mti.p21.pokefight.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_list.*
import mti.p21.pokefight.GameManager
import mti.p21.pokefight.MainActivity
import mti.p21.pokefight.R
import mti.p21.pokefight.adapter.BattlePokemonAdapter
import mti.p21.pokefight.model.SimplifiedPokemonDetails

/**
 * [BattlePokemonsFragment] that represent the fragment to choose the new pokemon to call.
 */
class BattlePokemonsFragment() : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let { bundle ->
            val gameManager = bundle.getSerializable("GameManager") as GameManager
            val pokemons: List<SimplifiedPokemonDetails> = gameManager.team

            val informationTextView: TextView = activity!!.findViewById(R.id.informations_textView)
            informationTextView.text = getString(R.string.interaction_select_pokemon)

            val onClickedPokemonListener = View.OnClickListener {
                val newPokemonIndex = it.tag as Int

                if (gameManager.currentPokemonIndex != newPokemonIndex &&
                    gameManager.team[newPokemonIndex].hp != 0) {

                    gameManager.currentPokemonIndex = newPokemonIndex
                    gameManager.loadCurrentPokemonInformations()
                    (activity as MainActivity).chooseAction(gameManager, true)
                }
            }

            recyclerView_container.layoutManager = LinearLayoutManager(activity)
            recyclerView_container.adapter = BattlePokemonAdapter(pokemons, activity!!, resources,
                                                                  onClickedPokemonListener)
            recyclerView_container.addItemDecoration(
                DividerItemDecoration(
                    activity,
                    LinearLayoutManager.VERTICAL
                )
            )
        }
    }
}
