package pl.naniewicz.mvisample.feature.login

import android.support.v4.app.Fragment
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import pl.naniewicz.mvisample.R
import pl.naniewicz.mvisample.feature.base.FragmentHolderActivity
import pl.naniewicz.mvisample.di.Injectable
import javax.inject.Inject


class LoginActivity : FragmentHolderActivity(), HasSupportFragmentInjector, Injectable {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun createFragmentInstance() = LoginFragment.newInstance()

    override fun getActivityTitle(): String = getString(R.string.login_activity)

    override fun supportFragmentInjector(): DispatchingAndroidInjector<Fragment> =
        dispatchingAndroidInjector
}
