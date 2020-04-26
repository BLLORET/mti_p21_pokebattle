package mti.p21.pokefight.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import mti.p21.pokefight.model.PokemonModel

interface IListPokemonContainer {
    var listPokemon: MutableLiveData<List<PokemonModel>?>
    fun executeWhenListPokemonIsLoaded(owner: LifecycleOwner, lambda: (List<PokemonModel>) -> Unit)
}