package pl.naniewicz.mvisample.feature.duck

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import kotlinx.android.synthetic.main.fragment_duck.*
import pl.naniewicz.mvisample.R
import pl.naniewicz.mvisample.feature.base.mvi.BaseMviFragment


class DuckFragment : BaseMviFragment<DuckView, DuckMviViewModel>(),
    DuckView {

    companion object {
        fun newInstance() = DuckFragment()
    }

    override val rotateLeftClicks by lazy { rotateLeft.clicks() }
    override val rotateRightClicks by lazy { rotateRight.clicks() }
    override val rotateBy by lazy { rotateByTextIput.textChanges() }

    override fun getMviViewModel() = ViewModelProviders.of(this)
        .get(DuckMviViewModel::class.java)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_duck, container, false)

    override fun render(duckViewState: DuckViewState) {
        duckImage.rotation = duckViewState.rotation
    }
}
