package mti.p21.pokefight.utils

class CounterAction {

    private var counter = 0

    var onCounterEnd: (() -> Unit)? = null

    fun increment() {
        counter += 1
    }

    fun decrement() {
        counter -= 1
        if (counter == 0)
            onCounterEnd?.invoke()
    }

    fun reset() {
        counter = 0
        onCounterEnd = null
    }

}