package pl.naniewicz.mvisample.feature.login

import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import pl.naniewicz.mvisample.feature.MviViewRobotBase


class LoginViewRobot(private val presenter: LoginMviViewModel) :
    MviViewRobotBase<LoginViewState>(), LoginView {

    override val loginClicks: Subject<Unit> = PublishSubject.create()
    override val emailTextChange: Subject<String> = PublishSubject.create()
    override val passwordTextChange: Subject<String> = PublishSubject.create()

    init {
        presenter.attachView(this)
    }

    override fun render(viewState: LoginViewState) {
        renderEvents.add(viewState)
    }

    override fun destroyView() = presenter.detachView()

    fun clickLogin() = loginClicks.onNext(Unit)

    fun enterEmail(email: String) = emailTextChange.onNext(email)

    fun enterPassword(password: String) = passwordTextChange.onNext(password)
}
