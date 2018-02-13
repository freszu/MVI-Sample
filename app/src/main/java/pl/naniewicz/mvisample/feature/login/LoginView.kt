package pl.naniewicz.mvisample.feature.login

import io.reactivex.Observable
import pl.naniewicz.mvisample.feature.base.mvi.MviView

interface LoginView : MviView {
    val loginClicks: Observable<Unit>
    val emailTextChange: Observable<String>
    val passwordTextChange: Observable<String>

    fun render(viewState: LoginViewState)
}
