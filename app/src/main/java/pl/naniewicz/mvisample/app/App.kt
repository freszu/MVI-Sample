package pl.naniewicz.mvisample.app

import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import pl.naniewicz.mvisample.BuildConfig
import pl.naniewicz.mvisample.di.applyAutoInjector
import timber.log.Timber


class App : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) return
        LeakCanary.install(this)

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        applyAutoInjector()
    }

    override fun applicationInjector(): AndroidInjector<App> =
        DaggerApplicationComponent.builder().create(this)

}
