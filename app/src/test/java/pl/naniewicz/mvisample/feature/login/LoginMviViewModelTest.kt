package pl.naniewicz.mvisample.feature.login

import com.nhaarman.mockito_kotlin.*
import io.reactivex.Completable
import io.reactivex.subjects.SingleSubject
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.naniewicz.mvisample.RxSchedulersOverrideRule
import java.util.concurrent.TimeUnit

@Suppress("IllegalIdentifier")
class LoginMviViewModelTest {

    @Rule
    @JvmField
    val overrideSchedulersRule = RxSchedulersOverrideRule()

    private val loginSingleSubject = SingleSubject.create<UserToken>()
    private val loginApiMock = mock<FakeLoginApi> {
        whenever(it.login(any(), any())) doReturn loginSingleSubject
    }

    private val tokenRepositoryMock = mock<FakeTokenRepository> {
        whenever(it.saveToken(any())) doReturn Completable.complete()
    }

    private val loginDataValidator = LoginDataValidator()

    private val loginMviViewModel = LoginMviViewModel(
        loginApi = loginApiMock,
        tokenRepository = tokenRepositoryMock,
        loginDataValidator = loginDataValidator
    )

    private lateinit var loginViewRobot: LoginViewRobot

    @Before
    fun beforeEachTest() {
        loginViewRobot = LoginViewRobot(loginMviViewModel)
    }

    @After
    fun afterEachTest() {
        loginViewRobot.destroyView()
    }

    @Test
    fun `Should show error when pressing login with empty fields and clear up after modifying given input`() {
        loginViewRobot.enterEmail("")
        loginViewRobot.enterPassword("")
        loginViewRobot.clickLogin()
        loginViewRobot.enterEmail("a")
        loginViewRobot.enterPassword("v")

        loginViewRobot.assertViewStatesRendered(
            LoginViewState.State(),
            LoginViewState.State(
                mailInputError = MailInputError.INCORRECT,
                passwordInputError = PasswordInputError.TO_SHORT
            ),
            LoginViewState.State(
                passwordInputError = PasswordInputError.TO_SHORT
            ),
            LoginViewState.State()
        )
    }

    @Test
    fun `Should validate email before making request`() {
        loginViewRobot.enterEmail("wrong")
        loginViewRobot.enterPassword(CORRECT_PASSWORD)
        loginViewRobot.clickLogin()

        loginViewRobot.assertViewStatesRendered(
            LoginViewState.State(),
            LoginViewState.State(
                mailInputError = MailInputError.INCORRECT
            )
        )
    }

    @Test
    fun `Should show progressbar and login when correct password`() {
        loginViewRobot.enterEmail(CORRECT_MAIL)
        loginViewRobot.enterPassword(CORRECT_PASSWORD)
        loginViewRobot.clickLogin()
        loginSingleSubject.onSuccess(USER_TOKEN)

        loginViewRobot.assertViewStatesRendered(
            LoginViewState.State(),
            LoginViewState.State(
                progressState = ProgressState.LOGGING_IN
            ),
            LoginViewState.Success
        )

        verify(tokenRepositoryMock).saveToken(USER_TOKEN)
    }

    @Test
    fun `Should show incorrect credentials when authentication error and clean up if password modified`() {
        loginViewRobot.enterEmail(CORRECT_MAIL)
        loginViewRobot.enterPassword(CORRECT_PASSWORD)
        loginViewRobot.clickLogin()
        loginSingleSubject.onError(AuthenticationError("Error"))
        loginViewRobot.enterPassword(CORRECT_PASSWORD)

        loginViewRobot.assertViewStatesRendered(
            LoginViewState.State(),
            LoginViewState.State(
                progressState = ProgressState.LOGGING_IN
            ),
            LoginViewState.State(
                credentialsError = CredentialsError.INCORRECT
            ),
            LoginViewState.State()
        )
    }

    @Test
    fun `Should show incorrect credentials when authentication error and clean up if email modified`() {
        loginViewRobot.enterEmail(CORRECT_MAIL)
        loginViewRobot.enterPassword(CORRECT_PASSWORD)
        loginViewRobot.clickLogin()
        loginSingleSubject.onError(AuthenticationError("Error"))
        loginViewRobot.enterEmail(CORRECT_MAIL)

        loginViewRobot.assertViewStatesRendered(
            LoginViewState.State(),
            LoginViewState.State(
                progressState = ProgressState.LOGGING_IN
            ),
            LoginViewState.State(
                credentialsError = CredentialsError.INCORRECT
            ),
            LoginViewState.State()
        )
    }

    @Test
    fun `Should show unknown error for 2 seconds if unknown error occurs while logging in`() {
        loginViewRobot.enterEmail(CORRECT_MAIL)
        loginViewRobot.enterPassword(CORRECT_PASSWORD)
        loginViewRobot.clickLogin()

        loginSingleSubject.onError(Throwable("Unknown"))
        overrideSchedulersRule.testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        loginViewRobot.enterEmail(CORRECT_MAIL)

        loginViewRobot.assertViewStatesRendered(
            LoginViewState.State(),
            LoginViewState.State(
                progressState = ProgressState.LOGGING_IN
            ),
            LoginViewState.State(
                otherErrors = OtherErrors.UNKNOWN
            ),
            LoginViewState.State()
        )
    }

    companion object {
        private const val CORRECT_MAIL = "me@gmail.com"
        private const val CORRECT_PASSWORD = "123456"

        private val USER_TOKEN = UserToken("1", "2")
    }

}
