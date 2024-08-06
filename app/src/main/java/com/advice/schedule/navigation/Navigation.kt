package com.advice.schedule.navigation

sealed class Navigation {
    data object Home : Navigation() {
        override fun route(): String = "home"
    }

    data object Maps : Navigation() {
        override fun route(): String = "maps"
    }

    data class News(val label: String = "News") : Navigation() {
        override fun route(): String = "news/{label}"
        override fun destination(): String = "news/$label"
    }

    data object Search : Navigation() {
        override fun route(): String = "search"
    }

    data class Locations(val label: String = "Locations") : Navigation() {
        override fun route(): String = "locations/{label}"
        override fun destination(): String = "locations/$label"
    }

    data class Event(val conference: String = "", val id: String = "", val session: String = "") :
        Navigation() {
        override fun route(): String = "event/{conference}/{id}"
        override fun destination(): String = "event/$conference/$id-$session"
    }

    data class Location(val id: Long = 0, val label: String = "") : Navigation() {
        override fun route(): String = "location/{id}/{label}"
        override fun destination(): String = "location/$id/$label"
    }

    data class Tag(val id: Long = 0, val label: String = "") : Navigation() {
        override fun route(): String = "tag/{id}/{label}"
        override fun destination(): String = "tag/$id/$label"
    }

    data class Schedule(val label: String = "", val ids: List<Long> = emptyList()) : Navigation() {
        override fun route(): String = "schedule/{label}/{ids}"
        override fun destination(): String {
            val ids = ids.joinToString(separator = ",")
            return "schedule/$label/$ids"
        }
    }

    @Deprecated("Use Event instead.")
    data class Content(val label: String = "") : Navigation() {
        override fun route(): String = "content/{label}"
        override fun destination(): String = "content/$label"
    }

    @Deprecated("Use Event instead.")
    data class ContentDetails(val conference: String = "", val id: String = "") : Navigation() {
        override fun route(): String = "content/{conference}/{id}"
        override fun destination(): String = "content/$conference/$id"
    }

    data class Speaker(val id: Long = 0, val name: String = "") : Navigation() {
        override fun route(): String = "speaker/{id}/{name}"
        override fun destination(): String = "speaker/$id/$name"
    }

    data object Settings : Navigation() {
        override fun route(): String = "settings"
    }

    data class Wifi(val id: Long = 0, val label: String = "") : Navigation() {
        override fun route(): String = "wifi/{id}/{label}"
        override fun destination(): String = "wifi/$id/$label"
    }

    data class Menu(val label: String = "", val id: Long = 0) : Navigation() {
        override fun route(): String = "menu/{label}/{id}"
        override fun destination(): String = "menu/$label/$id"
    }

    data class Function(val function: String = "", val label: String = "") : Navigation() {
        override fun route(): String = "{function}/{label}"
        override fun destination(): String = "$function/$label"
    }

    data class Document(val id: Long = 0) : Navigation() {
        override fun route(): String = "document/{id}"
        override fun destination(): String = "document/$id"
    }

    data class FAQ(val label: String = "") : Navigation() {
        override fun route(): String = "faq/{label}"
        override fun destination(): String = "faq/$label"
    }

    data class Organizations(val label: String = "", val id: Long = 0) : Navigation() {
        override fun route(): String = "organizations/{label}/{id}"
        override fun destination(): String = "organizations/$label/$id"
    }

    data class Organization(val id: Long = 0) : Navigation() {
        override fun route(): String = "organization/{id}"
        override fun destination(): String = "organization/$id"
    }

    data class People(val label: String = "") : Navigation() {
        override fun route(): String = "people/{label}"
        override fun destination(): String = "people/$label"
    }

    data class Products(val label: String = "") : Navigation() {
        override fun route(): String = "products/{label}"
        override fun destination(): String = "products/$label"
    }

    data class Product(val id: Long = 0L) : Navigation() {
        override fun route(): String = "product/{id}"
        override fun destination(): String = "product/$id"
    }

    data object ProductsSummary : Navigation() {
        override fun route(): String = "products/summary"
    }

    data class Feedback(val id: Long = 0L, val content: Long = 0L) : Navigation() {
        override fun route(): String = "feedback/{id}/{content}"
        override fun destination(): String = "feedback/$id/$content"
    }

    /**
     * The route for the navigation: e.g. "news/{label}"
     */
    abstract fun route(): String

    /**
     * The destination for the navigation: e.g. "news/63"
     */
    open fun destination(): String = route()
}
