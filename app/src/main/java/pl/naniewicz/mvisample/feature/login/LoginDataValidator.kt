package pl.naniewicz.mvisample.feature.login

import android.support.v4.util.PatternsCompat
import dagger.Reusable
import javax.inject.Inject

@Reusable
class LoginDataValidator @Inject constructor() {

    private val androidPatternMatcher by lazy {
        PatternsCompat.EMAIL_ADDRESS
    }

    fun isCorrectMail(email: String) = androidPatternMatcher.matcher(email).matches()

    fun isPasswordLongEnough(password: String) = password.length >= PASSWORD_MINIMAL_LENGTH

    companion object {
        const val PASSWORD_MINIMAL_LENGTH = 6
    }
}
