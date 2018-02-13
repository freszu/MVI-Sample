package pl.naniewicz.mvisample.feature.login


sealed class PartialStateChange {
    data class CredentialsValidationFailed(
        val mailInputError: MailInputError?,
        val passwordInputError: PasswordInputError?
    ) : PartialStateChange()

    object CancelMailInputError : PartialStateChange()
    object CancelPasswordInputError : PartialStateChange()

    object StartedLoggingIn : PartialStateChange()

    object ApiWrongCredentials : PartialStateChange()
    object CancelApiWrongCredentials : PartialStateChange()

    object ApiUnknownError : PartialStateChange()
    object CancelUnknownError : PartialStateChange()

    object Success : PartialStateChange()
}

sealed class LoginViewState {

    data class State(
        val mailInputError: MailInputError? = null,
        val passwordInputError: PasswordInputError? = null,
        val credentialsError: CredentialsError? = null,
        val otherErrors: OtherErrors? = null,
        val progressState: ProgressState = ProgressState.WAITS
    ) : LoginViewState()

    object Success : LoginViewState()
}

enum class MailInputError { INCORRECT }

enum class PasswordInputError { TO_SHORT }

enum class CredentialsError { INCORRECT }

enum class OtherErrors { UNKNOWN }

enum class ProgressState { LOGGING_IN, WAITS }
