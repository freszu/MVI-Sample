package pl.naniewicz.mvisample.feature

import org.junit.Assert


abstract class MviViewRobotBase<VS> {
    protected val renderEvents = mutableListOf<VS>()

    fun assertViewStatesRendered(vararg viewStates: VS) {
        Assert.assertEquals(
            "View states list should be the same",
            viewStates.asList(),
            renderEvents
        )
    }

    abstract fun destroyView()
}
