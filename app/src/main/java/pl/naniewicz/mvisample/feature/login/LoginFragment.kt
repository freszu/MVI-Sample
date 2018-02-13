package pl.naniewicz.mvisample.feature.login

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.editorActions
import com.jakewharton.rxbinding2.widget.textChanges
import io.reactivex.Observable
import io.reactivex.functions.Predicate
import kotlinx.android.synthetic.main.fragment_login.*
import org.jetbrains.anko.support.v4.longToast
import pl.naniewicz.mvisample.R
import pl.naniewicz.mvisample.di.Injectable
import pl.naniewicz.mvisample.feature.base.mvi.BaseMviFragment
import pl.naniewicz.mvisample.feature.login.ProgressState.LOGGING_IN
import pl.naniewicz.mvisample.feature.login.ProgressState.WAITS
import javax.inject.Inject

class LoginFragment : BaseMviFragment<LoginView, LoginMviViewModel>(),
    LoginView, Injectable {

    companion object {
        fun newInstance() = LoginFragment()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override val loginClicks: Observable<Unit> by lazy {
        Observable.merge(
            loginButton.clicks(),
            passwordEditText.editorActions(Predicate { it == EditorInfo.IME_ACTION_DONE })
                .map { Unit }
        )
    }

    override val emailTextChange: Observable<String> by lazy {
        emailEditText.textChanges().map { it.toString() }
    }

    override val passwordTextChange: Observable<String> by lazy {
        passwordEditText.textChanges().map { it.toString() }
    }

    private var unknownErrorSnackbar: Snackbar? = null

    override fun getMviViewModel(): LoginMviViewModel = ViewModelProviders
        .of(this, viewModelFactory)
        .get(LoginMviViewModel::class.java)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_login, container, false)

    override fun render(viewState: LoginViewState) = when (viewState) {
        LoginViewState.Success -> finishWithsSuccess()
        is LoginViewState.State -> renderState(viewState)
    }

    private fun renderState(viewState: LoginViewState.State) = with(viewState) {
        emailTextInputLayout.error = when (mailInputError) {
            MailInputError.INCORRECT -> getString(R.string.error_email_incorrect)
            null -> null
        }
        passwordTextInputLayout.error = when (passwordInputError) {
            PasswordInputError.TO_SHORT -> getString(R.string.error_password_to_short)
            null -> null
        }
        credentialsErrorText.text = when (credentialsError) {
            CredentialsError.INCORRECT -> getString(R.string.error_incorrect_login_credentials)
            null -> null
        }
        when (otherErrors) {
            OtherErrors.UNKNOWN -> showUnknownError()
            null -> hideUnknownError()
        }
        progressBar.visibility = when (progressState) {
            LOGGING_IN -> View.VISIBLE
            WAITS -> View.GONE
        }
    }

    private fun showUnknownError() {
        unknownErrorSnackbar = Snackbar
            .make(rootLayout, R.string.error_unknown_error, Snackbar.LENGTH_INDEFINITE)
            .apply { show() }

    }

    private fun hideUnknownError() {
        unknownErrorSnackbar?.dismiss()
    }

    private fun finishWithsSuccess() {
        longToast(R.string.logged_in_successfully)
        activity!!.finish()
    }
}
