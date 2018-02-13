package pl.naniewicz.mvisample

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import pl.naniewicz.mvisample.feature.base.mvi.BaseMviActivity
import pl.naniewicz.mvisample.feature.base.mvi.BaseMviViewModel
import pl.naniewicz.mvisample.feature.base.mvi.MviView
import java.util.concurrent.TimeUnit

class TestMviActivity : BaseMviActivity<View, MainMviViewModel>(), View {
    override fun getMviViewModel() = ViewModelProviders.of(this).get(MainMviViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun render(viewState: ViewState) {
        count.text = viewState.int.toString()
    }

}

class MainMviViewModel : BaseMviViewModel<View, ViewState>() {

    override fun bindIntents() {
        val state = Observable.interval(0, 10, TimeUnit.MILLISECONDS)
            .takeWhile { it < 1000 }
            .concatWith(
                Observable.timer(5, TimeUnit.SECONDS)
                    .flatMap { Observable.interval(0, 10, TimeUnit.MILLISECONDS) }
            )
            .map { ViewState(it) }

        subscribeViewState(state.observeOn(AndroidSchedulers.mainThread())) { view, viewState ->
            view.render(viewState)
        }
    }
}

data class ViewState(val int: Long)

interface View : MviView {
    fun render(viewState: ViewState)
}
