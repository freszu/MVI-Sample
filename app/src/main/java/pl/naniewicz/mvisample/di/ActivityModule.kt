package pl.naniewicz.mvisample.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import pl.naniewicz.mvisample.feature.login.LoginActivity


@Module
abstract class ActivityModule {
    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeLoginActivity(): LoginActivity
}
