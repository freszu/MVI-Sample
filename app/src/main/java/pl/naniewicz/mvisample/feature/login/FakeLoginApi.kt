package pl.naniewicz.mvisample.feature.login

import dagger.Reusable
import io.reactivex.Single
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@Reusable
class FakeLoginApi @Inject constructor() {

    private val random = Random()

    fun login(email: String, password: String): Single<UserToken> = Single.timer(
        LOGIN_DELAY_SECONDS,
        TimeUnit.SECONDS
    )
        .map { if (shoudldRandomlyFail()) error("Something bad happened") }
        .map { validateUserCredentials(email, password) }

    private fun validateUserCredentials(email: String, password: String): UserToken {
        return if (email == CORRECT_MAIL && password == CORRECT_PASSWORD) {
            UserToken(REFRESH_TOKEN, ACCESS_TOKEN)
        } else {
            throw AuthenticationError("Wrong credentials")
        }
    }

    private fun shoudldRandomlyFail() = random.nextBoolean()

    companion object {
        const val CORRECT_MAIL = "me@gmail.com"
        const val CORRECT_PASSWORD = "123456"

        const val REFRESH_TOKEN = "1"
        const val ACCESS_TOKEN = "2"
        const val LOGIN_DELAY_SECONDS = 3L
    }
}

data class UserToken(
    val refreshToken: String,
    val accessToken: String
)

class AuthenticationError(message: String) : Throwable(message)
