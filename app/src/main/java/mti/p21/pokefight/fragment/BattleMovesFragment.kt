package mti.p21.pokefight.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_list.*
import mti.p21.pokefight.R
import mti.p21.pokefight.adapter.BattleMovesAdapter
import mti.p21.pokefight.model.MoveModel

/**
 * A simple [Fragment] subclass.
 */
class BattleMovesFragment(private val moves: List<MoveModel>) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView_container.layoutManager = LinearLayoutManager(activity)
        recyclerView_container.adapter = BattleMovesAdapter(moves, activity!!, resources)
        recyclerView_container.addItemDecoration(
            DividerItemDecoration(
                activity,
                LinearLayoutManager.VERTICAL
            )
        )
    }
}
