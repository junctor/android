package com.advice.schedule

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.transition.Fade
import com.advice.schedule.models.firebase.*
import com.advice.schedule.models.local.*
import com.advice.schedule.utilities.MyClock
import com.advice.schedule.utilities.now
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern

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
    this.time = MyClock().now()
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
    backStack: Boolean = true
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

fun FirebaseConference.toConference() = Conference(
    id,
    name,
    description,
    codeofconduct,
    code,
    maps,
    start_timestamp.toDate(),
    end_timestamp.toDate(),
    timezone
)

fun FirebaseType.toType(): Type {
    val actions = ArrayList<Action>()
    if (discord_url?.isNotBlank() == true) {
        actions.add(Action(Action.getLabel(discord_url), discord_url))
    }
    if (subforum_url?.isNotBlank() == true) {
        actions.add(Action(Action.getLabel(subforum_url), subforum_url))
    }

    return Type(
        id,
        name,
        conference,
        color,
        description,
        actions
    )
}

fun FirebaseLocation.toLocation() = Location(
    name,
    hotel,
    conference
)

fun FirebaseEvent.toEvent(): Event {
    val element = type.toType()
    val links = if (conference.contains("DEFCON") && links.isEmpty()) {
        val urls = extractUrls(description)
        urls.map { it.toAction() }
    } else {
        links.map { it.toAction() }
    }

    val types = listOf(element)

    val body = if (android_description.isNotBlank()) {
        android_description
    } else {
        description
    }

    return Event(
        id,
        conference,
        title,
        body,
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(begin),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(end),
        link,
        updated,
        speakers.map { it.toSpeaker() },
        types,
        location.toLocation(),
        links
    )
}

private fun String.toAction() =
    Action(Action.getLabel(this), this)

private fun FirebaseAction.toAction() =
    Action(this.label, this.url)

private fun extractUrls(text: String): List<String> {
    val containedUrls: MutableList<String> =
        ArrayList()
    val urlRegex =
        "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)"
    val pattern: Pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE)
    val urlMatcher: Matcher = pattern.matcher(text)
    while (urlMatcher.find()) {
        containedUrls.add(
            text.substring(
                urlMatcher.start(0),
                urlMatcher.end(0)
            )
        )
    }
    return containedUrls
}

fun FirebaseSpeaker.toSpeaker() = Speaker(
    id,
    name,
    description,
    link,
    twitter,
    title
)

fun FirebaseVendor.toVendor() = Vendor(
    id,
    name,
    description,
    link,
    partner
)

fun FirebaseArticle.toArticle() = Article(
    id,
    name,
    text
)

fun FirebaseFAQ.toFAQ() = FAQ(
    id,
    question,
    answer
)

fun FragmentActivity.showKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun FragmentActivity.hideKeyboard() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    val view = currentFocus ?: View(this)
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun <T> QuerySnapshot.toObjectsOrEmpty(@NonNull clazz: Class<T>): List<T> {
    return try {
        toObjects(clazz)
    } catch (ex: Exception) {
        Log.e("Extensions", "Could not map data to objects: " + ex.message)
        return emptyList()
    }
}

fun <T> DocumentSnapshot.toObjectOrNull(@NonNull clazz: Class<T>): T? {
    return try {
        toObject(clazz)
    } catch (ex: Exception) {
        Log.e("Extensions", "Could not map data to objects: " + ex.message)
        return null
    }
}