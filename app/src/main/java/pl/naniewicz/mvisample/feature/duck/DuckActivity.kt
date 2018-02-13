package pl.naniewicz.mvisample.feature.duck

import pl.naniewicz.mvisample.R
import pl.naniewicz.mvisample.feature.base.FragmentHolderActivity


class DuckActivity : FragmentHolderActivity() {
    override fun createFragmentInstance() = DuckFragment.newInstance()

    override fun getActivityTitle(): String = getString(R.string.activity_duck)

}
