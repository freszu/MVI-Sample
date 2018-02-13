package pl.naniewicz.mvisample.feature.duck

import io.reactivex.Observable
import pl.naniewicz.mvisample.feature.base.mvi.MviView

interface DuckView : MviView {
    val rotateLeftClicks: Observable<Unit>
    val rotateRightClicks: Observable<Unit>
    val rotateBy: Observable<CharSequence>

    fun render(duckViewState: DuckViewState)
}
