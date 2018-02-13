package pl.naniewicz.mvisample.feature.base.mvi

import android.support.v4.app.Fragment


abstract class BaseMviFragment<V : MviView, out VM : BaseMviViewModel<V, *>> : Fragment(),
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
