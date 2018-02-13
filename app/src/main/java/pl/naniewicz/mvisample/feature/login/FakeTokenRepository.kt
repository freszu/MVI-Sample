package pl.naniewicz.mvisample.feature.login

import dagger.Reusable
import io.reactivex.Completable
import timber.log.Timber
import javax.inject.Inject


@Reusable
class FakeTokenRepository @Inject constructor() {

    fun saveToken(userToken: UserToken): Completable = Completable.complete()
        .doOnComplete { Timber.d("User token saved: $userToken") }

}
