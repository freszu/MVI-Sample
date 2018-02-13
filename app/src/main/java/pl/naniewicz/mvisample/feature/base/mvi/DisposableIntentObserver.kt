package pl.naniewicz.mvisample.feature.base.mvi

import io.reactivex.observers.DisposableObserver
import io.reactivex.subjects.PublishSubject


internal class DisposableIntentObserver<I>(private val subject: PublishSubject<I>) :
    DisposableObserver<I>() {

    override fun onNext(value: I) = subject.onNext(value)

    override fun onError(e: Throwable) =
        throw IllegalStateException("View intents must not throw errors", e)

    override fun onComplete() = subject.onComplete()
}
