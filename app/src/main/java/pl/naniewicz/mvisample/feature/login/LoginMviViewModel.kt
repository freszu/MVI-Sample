package pl.naniewicz.mvisample.feature.login

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.withLatestFrom
import pl.naniewicz.mvisample.common.applyIoSchedulers
import pl.naniewicz.mvisample.feature.base.mvi.BaseMviViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class LoginMviViewModel @Inject constructor(
    private val loginApi: FakeLoginApi,
    private val tokenRepository: FakeTokenRepository,
    private val loginDataValidator: LoginDataValidator
) : BaseMviViewModel<LoginView, LoginViewState>() {

    override fun bindIntents() {
        val emailObservable = intent { it.emailTextChange }
        val passwordObservable = intent { it.passwordTextChange }

        val viewStateObservable = intent { it.loginClicks }
            .withLatestFrom(emailObservable, passwordObservable) { _, email, password ->
                Pair(email, password)
            }
            .switchMap { (email, password) ->
                val isMailCorrect = loginDataValidator.isCorrectMail(email)
                val isPasswordLongEnough = loginDataValidator.isPasswordLongEnough(password)

                if (isMailCorrect && isPasswordLongEnough) {
                    login(
                        email, password,
                        createCancelWrongCredentialsObservable(emailObservable, passwordObservable)
                    )
                } else {
                    showPasswordEmailInputError(
                        isMailCorrect, isPasswordLongEnough,
                        emailObservable, passwordObservable
                    )
                }
            }
            .scan<LoginViewState>(LoginViewState.State()) { oldViewState, partialChange ->
                when (oldViewState) {
                    is LoginViewState.State -> reduceState(oldViewState, partialChange)
                    LoginViewState.Success -> LoginViewState.Success
                }
            }

        subscribeViewState(viewStateObservable.applyIoSchedulers()) { view, viewState ->
            view.render(viewState)
        }
    }

    private fun showPasswordEmailInputError(
        isMailCorrect: Boolean, isPasswordLongEnough: Boolean,
        emailObservable: Observable<String>, passwordObservable: Observable<String>
    ): Observable<PartialStateChange> = Observable.just<PartialStateChange>(
        PartialStateChange.CredentialsValidationFailed(
            if (isMailCorrect) null else MailInputError.INCORRECT,
            if (isPasswordLongEnough) null else PasswordInputError.TO_SHORT
        )
    ).concatWith(
        Single.merge(
            emailObservable.firstOrError()
                .map { PartialStateChange.CancelMailInputError },
            passwordObservable.firstOrError()
                .map { PartialStateChange.CancelPasswordInputError }
        ).toObservable()
    )


    private fun login(
        email: String, password: String,
        cancelWrongCredentialsErrorObservable: Observable<PartialStateChange.CancelApiWrongCredentials>
    ) = loginApi.login(email, password)
        .flatMapCompletable { tokenRepository.saveToken(it) }
        .toSingleDefault<PartialStateChange>(PartialStateChange.Success)
        .toObservable()
        .startWith(PartialStateChange.StartedLoggingIn)
        .onErrorResumeNext { error: Throwable ->
            parseLoginError(error, cancelWrongCredentialsErrorObservable)
        }

    private fun parseLoginError(
        error: Throwable, cancelWrongCredentialsObservable: Observable<PartialStateChange.CancelApiWrongCredentials>
    ): Observable<PartialStateChange> =
        when (error) {
            is AuthenticationError -> inputCancellableWrongCredentialsError(
                cancelWrongCredentialsObservable
            )
            else -> timedUnknownErrorObservable()
        }

    private fun inputCancellableWrongCredentialsError(
        cancelWrongCredentialsObservable: Observable<PartialStateChange.CancelApiWrongCredentials>
    ): Observable<PartialStateChange> {
        return Observable.just<PartialStateChange>(PartialStateChange.ApiWrongCredentials)
            .concatWith(cancelWrongCredentialsObservable.take(1))
    }

    private fun timedUnknownErrorObservable(): Observable<PartialStateChange> {
        return Observable.timer(TOAST_ERROR_SHOW_TIME_SECONDS, TimeUnit.SECONDS)
            .map<PartialStateChange> {
                PartialStateChange.CancelUnknownError
            }
            .startWith(PartialStateChange.ApiUnknownError)
    }

    private fun createCancelWrongCredentialsObservable(
        emailObservable: Observable<String>, passwordObservable: Observable<String>
    ): Observable<PartialStateChange.CancelApiWrongCredentials> {
        return Observable.merge(
            emailObservable.map { PartialStateChange.CancelMailInputError },
            passwordObservable.map { PartialStateChange.CancelPasswordInputError }
        )
            .take(1)
            .map { PartialStateChange.CancelApiWrongCredentials }
    }

    private fun reduceState(
        oldState: LoginViewState.State, partialChange: PartialStateChange
    ) = when (partialChange) {
        is PartialStateChange.CredentialsValidationFailed -> oldState.copy(
            mailInputError = partialChange.mailInputError,
            passwordInputError = partialChange.passwordInputError
        )
        PartialStateChange.CancelMailInputError -> oldState.copy(
            mailInputError = null
        )
        PartialStateChange.CancelPasswordInputError -> oldState.copy(
            passwordInputError = null
        )
        PartialStateChange.StartedLoggingIn -> LoginViewState.State(
            progressState = ProgressState.LOGGING_IN
        )
        PartialStateChange.ApiWrongCredentials -> oldState.copy(
            progressState = ProgressState.WAITS,
            credentialsError = CredentialsError.INCORRECT
        )
        PartialStateChange.CancelApiWrongCredentials -> oldState.copy(
            credentialsError = null
        )
        PartialStateChange.ApiUnknownError -> oldState.copy(
            progressState = ProgressState.WAITS,
            otherErrors = OtherErrors.UNKNOWN
        )
        PartialStateChange.CancelUnknownError -> oldState.copy(
            otherErrors = null
        )

        PartialStateChange.Success -> LoginViewState.Success
    }

    companion object {
        private const val TOAST_ERROR_SHOW_TIME_SECONDS = 2L
    }
}
