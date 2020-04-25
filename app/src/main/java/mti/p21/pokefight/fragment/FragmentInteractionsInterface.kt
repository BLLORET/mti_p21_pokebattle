package mti.p21.pokefight.fragment

import mti.p21.pokefight.model.PokeType
import mti.p21.pokefight.model.PokemonModel

interface FragmentInteractionsInterface {
    /**
     * Redirect to the Battle Lobby fragment.
     */
    fun onBattleButtonClicked()

    /**
     * Redirect to the pokedex list fragment.
     */
    fun onPokedexButtonClicked()

    /**
     * Redirect to the details pokemon fragment.
     */
    fun onPokedexRowClicked(pokemon: PokemonModel)

    /**
     * Open an help fragment.
     */
    fun onTypePictureClicked(pokeType: PokeType)

    /**
     * Open the battle fragment.
     */
    fun onFightButtonClicked(team : List<PokemonModel>, opponents : List<PokemonModel>)
}