package mti.p21.pokefight.fragment

interface BattleInteractionsInterface {
    /**
     * Choose the interaction fragment to choose which action to execute.
     */
    fun chooseAction(enable: Boolean)

    /**
     * Change the interaction fragment to a list of moves.
     */
    fun onAttackButtonClicked()

    /**
     * Change the interaction fragment to a list of pokemon
     */
    fun onPokemonButtonClicked()
}