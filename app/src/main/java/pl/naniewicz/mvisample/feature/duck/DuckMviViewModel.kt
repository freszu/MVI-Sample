package pl.naniewicz.mvisample.feature.duck

import io.reactivex.Observable
import io.reactivex.rxkotlin.withLatestFrom
import pl.naniewicz.mvisample.feature.base.mvi.BaseMviViewModel

class DuckMviViewModel : BaseMviViewModel<DuckView, DuckViewState>() {

    override fun bindIntents() {
        val rotateByObservable = intent { it.rotateBy }
            .map { it.toString().toFloatOrNull() ?: 0.0f }

        val rotateLeftObservable = intent { it.rotateLeftClicks }
            .withLatestFrom(rotateByObservable) { _, rotation -> rotation }
            .map { -it }

        val rotateRightObservable = intent { it.rotateRightClicks }
            .withLatestFrom(rotateByObservable) { _, rotation -> rotation }

        val stateObservable = Observable.merge(
            rotateLeftObservable,
            rotateRightObservable
        )
            .scan(DuckViewState()) { oldState, rotationChange ->
                oldState.copy(rotation = oldState.rotation + rotationChange)
            }

        subscribeViewState(stateObservable) { view, viewState ->
            view.render(viewState)
        }
    }
}
