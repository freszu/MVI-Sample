package pl.naniewicz.mvisample.feature.base.mvi

import android.support.v7.app.AppCompatActivity


abstract class BaseMviActivity<V : MviView, out VM : BaseMviViewModel<V, *>> :
    AppCompatActivity(),
    MviView {

    private lateinit var viewModel: VM
    abstract fun getMviViewModel(): VM

    override fun onStart() {
        super.onStart()
        viewModel = getMviViewModel()
        @Suppress("UNCHECKED_CAST")
        viewModel.attachView(
            this as? V ?: error("Couldn't cast the View to the corresponding View interface.")
        )
    }

    override fun onStop() {
        super.onStop()
        viewModel.detachView()
    }
}

