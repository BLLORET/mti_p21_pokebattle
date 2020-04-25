package mti.p21.pokefight.fragment

import mti.p21.pokefight.GameManager

interface BattleInteractionsInterface {
    /**
     * Choose the interaction fragment to choose which action to execute.
     */
    fun chooseAction(gameManager: GameManager, enableButtons : Boolean = false)

    /**
     * Change the interaction fragment to a list of moves.
     */
    fun onAttackButtonClicked(gameManager: GameManager)

    /**
     * Change the interaction fragment to a list of pokemon
     */
    fun onPokemonButtonClicked(gameManager: GameManager)
}