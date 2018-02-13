package pl.naniewicz.mvisample.feature.base

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_fragment_holder.*
import pl.naniewicz.mvisample.R


abstract class FragmentHolderActivity : AppCompatActivity() {

    abstract fun createFragmentInstance(): Fragment

    abstract fun getActivityTitle(): String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupContentView()
        if (savedInstanceState == null) replaceFragment(
            createFragmentInstance(),
            R.id.fragmentContainer
        )
    }

    private fun setupContentView() {
        setContentView(R.layout.activity_fragment_holder)
        setupActionBar()
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = getActivityTitle()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun replaceFragment(fragment: Fragment, @IdRes containerViewId: Int) {
        supportFragmentManager.beginTransaction()
            .replace(containerViewId, fragment)
            .commit()
    }
}
