package com.advice.ui.components.markdown

/**
 * A markdown slice used for in-document heading anchors.
 *
 * [slug] is null for the preamble before the first ATX heading.
 */
data class MarkdownSection(
    val slug: String?,
    val content: String,
)

private val atxHeadingRegex = Regex("""^(#{1,6})[ \t]+(.*?)(?:[ \t]+#{1,6})?[ \t]*$""")
private val fenceOpenRegex = Regex("""^([`~]{3,})""")
private val tocFragmentRegex = Regex("""\]\(#([^)]+)\)""")

/**
 * Inserts newlines before ATX headings that appear mid-line, and normalizes CRLF.
 *
 * Link destinations like `(#dates-and-times)` are safe: they use `#slug` with no
 * space after `#`, while ATX requires `#{1,6}` followed by whitespace.
 */
fun normalizeMarkdownForAnchors(markdown: String): String =
    markdown
        .replace("\r\n", "\n")
        .replace('\r', '\n')
        .replace(Regex("""([^\n])[ \t]+(#{1,6}[ \t]+)"""), "$1\n$2")

/**
 * GitHub-style heading slug (github-slugger / GFM).
 *
 * Lowercases, strips punctuation except word chars / spaces / hyphens,
 * turns spaces into hyphens, and collapses repeated hyphens.
 */
fun githubSlug(headingText: String): String =
    headingText
        .lowercase()
        .replace(Regex("""[^\p{L}\p{N}\s_-]"""), "")
        .trim()
        // One hyphen per space (do not collapse); "Health & Hygiene" → "health--hygiene".
        .replace(" ", "-")
        .trim('-')

/**
 * Unique-ifies [base] against [seen], matching GitHub's `-1`, `-2`, … suffixes.
 */
fun uniqueGithubSlug(
    base: String,
    seen: MutableMap<String, Int>,
): String {
    val count = seen[base] ?: 0
    seen[base] = count + 1
    return if (count == 0) base else "$base-$count"
}

/**
 * Resolves a heading line to a slug.
 *
 * When [tocSlugs] is non-empty and the heading line also contains body text
 * (common when source newlines were collapsed), prefers the longest word-prefix
 * whose GitHub slug appears in the document's TOC fragments.
 */
fun resolveHeadingSlug(
    headingText: String,
    tocSlugs: Set<String>,
    seen: MutableMap<String, Int>,
): String = resolveHeadingParts(headingText, tocSlugs, seen).slug

/**
 * @return slug, heading title to keep on the ATX line, and any leftover body text.
 */
internal data class HeadingParts(
    val slug: String,
    val title: String,
    val remainder: String?,
)

internal fun resolveHeadingParts(
    headingText: String,
    tocSlugs: Set<String>,
    seen: MutableMap<String, Int>,
): HeadingParts {
    val words = headingText.trim().split(Regex("""\s+"""))
    val full = githubSlug(headingText)
    if (tocSlugs.isEmpty() || full in tocSlugs) {
        return HeadingParts(uniqueGithubSlug(full, seen), headingText.trim(), null)
    }

    for (count in words.size - 1 downTo 1) {
        val title = words.take(count).joinToString(" ")
        val candidate = githubSlug(title)
        if (candidate.isNotEmpty() && candidate in tocSlugs) {
            val remainder = words.drop(count).joinToString(" ").ifBlank { null }
            return HeadingParts(uniqueGithubSlug(candidate, seen), title, remainder)
        }
    }

    return HeadingParts(uniqueGithubSlug(full, seen), headingText.trim(), null)
}

fun extractTocSlugs(markdown: String): Set<String> = tocFragmentRegex.findAll(markdown).map { it.groupValues[1] }.toSet()

/**
 * Splits markdown into sections at ATX headings (`#`…`###### `), respecting fenced
 * code blocks so headings inside fences are not treated as section breaks.
 */
fun splitMarkdownByHeadings(markdown: String): List<MarkdownSection> {
    val normalized = normalizeMarkdownForAnchors(markdown)
    if (normalized.isEmpty()) {
        return listOf(MarkdownSection(slug = null, content = ""))
    }

    val tocSlugs = extractTocSlugs(normalized)
    val lines = normalized.split('\n')
    val sections = mutableListOf<MarkdownSection>()
    val seenSlugs = mutableMapOf<String, Int>()
    val buffer = StringBuilder()
    var currentSlug: String? = null
    var fenceMarker: String? = null

    fun flush() {
        // Skip an empty preamble when the document starts with a heading.
        if (currentSlug == null && buffer.isEmpty()) {
            return
        }
        sections.add(MarkdownSection(slug = currentSlug, content = buffer.toString()))
        buffer.clear()
    }

    fun appendLine(line: String) {
        if (buffer.isNotEmpty()) buffer.append('\n')
        buffer.append(line)
    }

    for (line in lines) {
        val trimmedStart = line.trimStart()
        val fenceMatch = fenceOpenRegex.find(trimmedStart)
        if (fenceMatch != null) {
            val ticks = fenceMatch.groupValues[1]
            val openFence = fenceMarker
            if (openFence == null) {
                fenceMarker = ticks
            } else if (ticks[0] == openFence[0] && ticks.length >= openFence.length) {
                val rest = trimmedStart.drop(ticks.length).trim()
                if (rest.isEmpty()) {
                    fenceMarker = null
                }
            }
            appendLine(line)
            continue
        }

        if (fenceMarker == null) {
            val headingMatch = atxHeadingRegex.matchEntire(line)
            if (headingMatch != null) {
                flush()
                val marks = headingMatch.groupValues[1]
                val headingText = headingMatch.groupValues[2].trim()
                val (slug, title, remainder) = resolveHeadingParts(headingText, tocSlugs, seenSlugs)
                currentSlug = slug
                appendLine("$marks $title")
                if (remainder != null) {
                    appendLine(remainder)
                }
                continue
            }
        }

        appendLine(line)
    }

    flush()
    return sections
}
