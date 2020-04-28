package mti.p21.pokefight.utils

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.gson.GsonBuilder
import mti.p21.pokefight.R
import mti.p21.pokefight.fragment.BattleInteractionsInterface
import mti.p21.pokefight.fragment.FragmentInteractionsInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

abstract class AbstractActivity : AppCompatActivity(),
    IListPokemonContainer,
    FragmentInteractionsInterface,
    BattleInteractionsInterface
{

    abstract val layoutResource: Int

    lateinit var retrofit: Retrofit
    inline fun <reified T> service(): T = retrofit.create(T::class.java)

    abstract fun onInit()

    fun toast(msg: String) =
        Toast.makeText(this.applicationContext, msg, Toast.LENGTH_SHORT).show()

    fun toastLong(msg: String) =
        Toast.makeText(this.applicationContext, msg, Toast.LENGTH_LONG).show()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResource)
        onInit()
    }

    fun setupRetrofitWithUrl(url: String) {
        val jsonConverter = GsonConverterFactory.create(GsonBuilder().create())
        retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(jsonConverter)
            .build()
    }


    inline fun <reified T : AbstractFragment> replaceFragment(addToBackStack: Boolean = true) =
        supportFragmentManager
            .beginTransaction()
            .apply { if (addToBackStack) this.addToBackStack(null) }
            .replace(
                R.id.main_container,
                T::class.java.getConstructor(AbstractActivity::class.java)
                    .newInstance(this)
            )
            .commit()

    fun replaceFragment(fragment: AbstractFragment, addToBackStack: Boolean = true) =
        supportFragmentManager
            .beginTransaction()
            .apply { if (addToBackStack) this.addToBackStack(null) }
            .replace(R.id.main_container, fragment)
            .commit()

    fun addFragment(fragment: AbstractFragment, addToBackStack: Boolean = true) =
        supportFragmentManager
            .beginTransaction()
            .apply { if (addToBackStack) this.addToBackStack(null) }
            .add(R.id.main_container, fragment)
            .commit()

    inline fun <reified T : AbstractFragment> addFragment(addToBackStack: Boolean = true) =
        supportFragmentManager
            .beginTransaction()
            .apply { if (addToBackStack) this.addToBackStack(null) }
            .add(
                R.id.main_container,
                T::class.java.getConstructor(AbstractActivity::class.java)
                    .newInstance(this)
            )
            .commit()


    fun backActivity() {
        if (supportFragmentManager.backStackEntryCount > 0)
            supportFragmentManager.popBackStack()
    }

    fun resetApp() {
        clearBackStack()
        onInit()
    }

    fun clearBackStack() {
        while (supportFragmentManager.backStackEntryCount > 0)
            supportFragmentManager.popBackStackImmediate()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0)
            supportFragmentManager.popBackStack()
        else
            super.onBackPressed()
    }

    fun hideKeyBoard(): Boolean {
        val v: View? = currentFocus
        if (v != null) {
            val imm: InputMethodManager? =
                ContextCompat.getSystemService(applicationContext!!, InputMethodManager::class.java)
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
        }
        return true
    }

}