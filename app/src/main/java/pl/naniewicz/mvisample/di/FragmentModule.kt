package pl.naniewicz.mvisample.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import pl.naniewicz.mvisample.feature.login.LoginFragment


@Module
abstract class FragmentModule {
    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginFragment
}
