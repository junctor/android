package com.advice.core.local

/**
 * Flattens a location tree into a pre-order list (parent before children).
 */
fun Location.flatten(): List<Location> {
    val result = mutableListOf<Location>()
    val stack = ArrayDeque<Location>()
    stack.addLast(this)
    while (stack.isNotEmpty()) {
        val node = stack.removeLast()
        result.add(node)
        for (i in node.children.lastIndex downTo 0) {
            stack.addLast(node.children[i])
        }
    }
    return result
}

fun List<Location>.flattenTree(): List<Location> = flatMap { it.flatten() }
