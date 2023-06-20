package com.advice.schedule.extensions

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.transition.Fade
import com.advice.schedule.utils.TimeUtils
import com.google.firebase.Timestamp
import timber.log.Timber
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

fun Date.isToday(): Boolean {
    val current = Calendar.getInstance().now()

    val cal = Calendar.getInstance()
    cal.time = this

    return cal.get(Calendar.YEAR) == current.get(Calendar.YEAR)
            && cal.get(Calendar.DAY_OF_YEAR) == current.get(Calendar.DAY_OF_YEAR)
}

fun Date.isTomorrow(): Boolean {
    val cal1 = Calendar.getInstance().now()
    cal1.roll(Calendar.DAY_OF_YEAR, true)

    val cal2 = Calendar.getInstance()
    cal2.time = this

    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(
        Calendar.DAY_OF_YEAR
    )
}

fun Date.getDateDifference(date: Date, timeUnit: TimeUnit): Long {
    return timeUnit.convert(date.time - this.time, TimeUnit.MILLISECONDS)
}


fun Calendar.now(): Calendar {
    this.time = TimeUtils.now()
    return this
}

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}

fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int) {
    supportFragmentManager.inTransaction { add(frameId, fragment) }
}


fun AppCompatActivity.replaceFragment(
    fragment: Fragment,
    frameId: Int,
    hasAnimation: Boolean = false,
    backStack: Boolean = true,
) {
    supportFragmentManager.inTransaction {

        if (hasAnimation) {

            val fadeDuration = 300L


            fragment.apply {
                enterTransition = Fade().apply {
                    duration = fadeDuration
                }


                returnTransition = Fade().apply {
                    duration = fadeDuration
                }
            }
        }

        val transaction = replace(frameId, fragment)
        if (backStack) {
            transaction.addToBackStack(null)
        }
        return@inTransaction transaction
    }
}

fun Timestamp.toDate(): Date {
    return Date(seconds * 1000)
}


fun FragmentActivity.showKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun FragmentActivity.hideKeyboard() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    val view = currentFocus ?: View(this)
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun <T> List<Fragment>.get(clazz: Class<T>): T {
    return first { it::class.java == clazz } as T
}


inline fun SearchView.onQueryTextChanged(crossinline listener: (String) -> Unit) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            listener(newText.orEmpty())
            return true
        }
    })
}

val Number.toPx
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()

fun Context.getTintedDrawable(resource: Int, tint: Int): Drawable? {
    return ContextCompat.getDrawable(this, resource)?.mutate()?.apply {
        setTint(tint)
    }
}

@Composable
inline fun <reified VM : ViewModel> NavHostController.navGraphViewModel(): VM {
    val navBackStackEntry by currentBackStackEntryAsState()
    return navBackStackEntry?.let {
        viewModel(viewModelStoreOwner = it)
    } ?: viewModel<VM>().also {
        Timber.e("Creating new ViewModel: ${VM::class.java.simpleName}")
    }
}