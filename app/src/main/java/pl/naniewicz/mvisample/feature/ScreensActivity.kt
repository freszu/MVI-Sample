package pl.naniewicz.mvisample.feature

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_screens.*
import org.jetbrains.anko.startActivity
import pl.naniewicz.mvisample.R
import pl.naniewicz.mvisample.TestMviActivity
import pl.naniewicz.mvisample.feature.duck.DuckActivity
import pl.naniewicz.mvisample.feature.login.LoginActivity

class ScreensActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screens)

        bindButtons()
    }

    private fun bindButtons() {
        duckActivity.setOnClickListener { startActivity<DuckActivity>() }
        loginActivity.setOnClickListener { startActivity<LoginActivity>() }
        testActivity.setOnClickListener { startActivity<TestMviActivity>() }
    }
}
