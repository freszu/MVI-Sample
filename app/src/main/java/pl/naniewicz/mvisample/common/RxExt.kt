package pl.naniewicz.mvisample.common

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Flowables
import io.reactivex.schedulers.Schedulers


fun <T> Flowables.createFlowable(
    mode: BackpressureStrategy,
    source: (FlowableEmitter<T>) -> Unit
): Flowable<T> {
    return Flowable.create(source, mode)
}

fun <T> Observable<T>.applyIoSchedulers(): Observable<T> = this.subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

fun <T> Flowable<T>.applyIoSchedulers(): Flowable<T> = this.subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

