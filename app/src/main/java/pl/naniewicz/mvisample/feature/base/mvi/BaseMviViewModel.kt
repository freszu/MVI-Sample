package pl.naniewicz.mvisample.feature.base.mvi

import android.arch.lifecycle.ViewModel
import android.support.annotation.CallSuper
import android.support.annotation.MainThread
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

typealias ViewStateConsumer<V, VS> = (V, VS) -> Unit

typealias ViewIntentBinder<V, I> = (V) -> Observable<I>

abstract class BaseMviViewModel<V : MviView, VS> : ViewModel() {

    private val viewStateBehaviorSubject = BehaviorSubject.create<VS>()
    private val intentDisposals = CompositeDisposable()
    private var viewStateDisposable: Disposable = Disposables.empty()

    private var wasSubscribeViewStateCalled = false
    private var viewAttachedFirstTime = true
    private val intentRelaysBinders = mutableListOf<IntentRelayBinderPair<*>>()
    private var viewStateConsumer: ViewStateConsumer<V, VS>? = null

    private var viewRelayConsumerDisposable: Disposable =
        Disposables.empty()

    private inner class IntentRelayBinderPair<I>(
        val intentRelaySubject: PublishSubject<I>, val intentBinder: ViewIntentBinder<V, I>
    )

    @CallSuper
    fun attachView(view: V) {
        if (viewAttachedFirstTime) {
            bindIntents()
            viewAttachedFirstTime = false
        }

        //
        // Build the chain from bottom to top:
        // 1. Subscribe to ViewState
        // 2. Subscribe intents
        //
        if (viewStateConsumer != null) subscribeViewStateConsumerActually(view)

        intentRelaysBinders.forEach { bindIntentActually<Any>(view, it) }
    }

    fun detachView() {
        viewRelayConsumerDisposable.dispose()
        intentDisposals.clear()
    }

    override fun onCleared() {
        super.onCleared()
        viewStateDisposable.dispose()

        unbindIntents()
    }

    protected abstract fun bindIntents()

    protected open fun unbindIntents() {}

    protected fun subscribeViewState(
        viewStateObservable: Observable<VS>, consumer: ViewStateConsumer<V, VS>
    ) {
        if (wasSubscribeViewStateCalled) {
            error("subscribeViewState() method is only allowed to be called once")
        }
        wasSubscribeViewStateCalled = true

        this.viewStateConsumer = consumer

        viewStateDisposable = viewStateObservable.subscribeWith(
            DisposableViewStateObserver(viewStateBehaviorSubject)
        )
    }

    @MainThread
    private fun subscribeViewStateConsumerActually(view: V) {
        viewStateConsumer?.let {
            viewRelayConsumerDisposable = viewStateBehaviorSubject.subscribe { vs ->
                it(view, vs)
            }
        } ?: throw NullPointerException("View state consumer is null.")
    }

    @MainThread
    protected fun <I> intent(binder: ViewIntentBinder<V, I>): Observable<I> {
        val intentRelay = PublishSubject.create<I>()
        intentRelaysBinders.add(IntentRelayBinderPair(intentRelay, binder))
        return intentRelay
    }


    @Suppress("UNCHECKED_CAST")
    @MainThread
    private fun <I> bindIntentActually(
        view: V, relayBinderPair: IntentRelayBinderPair<*>
    ): Observable<I> {

        val intentRelay = relayBinderPair.intentRelaySubject as (PublishSubject<I>)

        val intentBinder = relayBinderPair.intentBinder as (ViewIntentBinder<V, I>)
        val intent = intentBinder(view)

        intentDisposals.add(
            intent.subscribeWith(
                DisposableIntentObserver(intentRelay)
            )
        )
        return intentRelay
    }
}
