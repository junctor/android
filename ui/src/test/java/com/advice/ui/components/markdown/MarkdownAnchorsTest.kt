package com.advice.ui.components.markdown

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MarkdownAnchorsTest {
    @Test
    fun `githubSlug lowercases and hyphenates`() {
        assertEquals("dates-and-times", githubSlug("Dates and Times"))
    }

    @Test
    fun `githubSlug strips parentheses`() {
        assertEquals("noc-wi-fi", githubSlug("NOC (Wi-Fi)"))
    }

    @Test
    fun `githubSlug strips other punctuation`() {
        assertEquals("food", githubSlug("Food!"))
        assertEquals("venue", githubSlug("Venue?"))
    }

    @Test
    fun `githubSlug doubles hyphen for ampersand`() {
        assertEquals("health--hygiene", githubSlug("Health & Hygiene"))
    }

    @Test
    fun `uniqueGithubSlug suffixes duplicates`() {
        val seen = mutableMapOf<String, Int>()
        assertEquals("food", uniqueGithubSlug("food", seen))
        assertEquals("food-1", uniqueGithubSlug("food", seen))
        assertEquals("food-2", uniqueGithubSlug("food", seen))
    }

    @Test
    fun `splitMarkdownByHeadings keeps preamble without slug`() {
        val markdown =
            """
            Intro text

            ## Dates and Times
            When it happens

            ## Food
            What to eat
            """.trimIndent()

        val sections = splitMarkdownByHeadings(markdown)
        assertEquals(3, sections.size)
        assertNull(sections[0].slug)
        assertTrue(sections[0].content.contains("Intro text"))
        assertEquals("dates-and-times", sections[1].slug)
        assertTrue(sections[1].content.startsWith("## Dates and Times"))
        assertEquals("food", sections[2].slug)
        assertTrue(sections[2].content.startsWith("## Food"))
    }

    @Test
    fun `splitMarkdownByHeadings handles DEF CON TOC style headings`() {
        val markdown =
            """
            - [Dates and Times](#dates-and-times)
            - [NOC (Wi-Fi)](#noc-wi-fi)

            ## Dates and Times
            Schedule details

            ## NOC (Wi-Fi)
            Network info
            """.trimIndent()

        val sections = splitMarkdownByHeadings(markdown)
        assertEquals("dates-and-times", sections[1].slug)
        assertEquals("noc-wi-fi", sections[2].slug)
    }

    @Test
    fun `splitMarkdownByHeadings ignores headings inside fenced code`() {
        val markdown =
            """
            Preamble

            ```
            ## Not A Heading
            ```

            ## Real Heading
            Body
            """.trimIndent()

        val sections = splitMarkdownByHeadings(markdown)
        assertEquals(2, sections.size)
        assertNull(sections[0].slug)
        assertTrue(sections[0].content.contains("## Not A Heading"))
        assertEquals("real-heading", sections[1].slug)
    }

    @Test
    fun `splitMarkdownByHeadings uniqueifies duplicate headings`() {
        val markdown =
            """
            ## Food
            A

            ## Food
            B
            """.trimIndent()

        val sections = splitMarkdownByHeadings(markdown)
        assertEquals("food", sections[0].slug)
        assertEquals("food-1", sections[1].slug)
    }

    @Test
    fun `splitMarkdownByHeadings empty input`() {
        val sections = splitMarkdownByHeadings("")
        assertEquals(1, sections.size)
        assertNull(sections[0].slug)
        assertEquals("", sections[0].content)
    }

    @Test
    fun `normalizeMarkdownForAnchors inserts newline before mid-line ATX`() {
        val normalized =
            normalizeMarkdownForAnchors(
                "see credits) ## Dates and Times\nBadge pickup",
            )
        assertTrue(normalized.contains(")\n## Dates and Times"))
    }

    @Test
    fun `normalizeMarkdownForAnchors does not break TOC fragment links`() {
        val input = "- [Dates and Times](#dates-and-times) - [Food](#food)"
        assertEquals(input, normalizeMarkdownForAnchors(input))
    }

    @Test
    fun `splitMarkdownByHeadings matches TOC when heading line includes body text`() {
        // Firebase-like collapsed source: heading title and body on one line after normalize.
        val markdown =
            "- [Dates and Times](#dates-and-times) - [Food](#food) " +
                "## Dates and Times Badge pickup begins on August 6. " +
                "## Food The LVCC West Hall has a food court."

        val sections = splitMarkdownByHeadings(markdown)
        val bySlug = sections.associateBy { it.slug }
        assertEquals("## Dates and Times\nBadge pickup begins on August 6.", bySlug["dates-and-times"]?.content)
        assertEquals("## Food\nThe LVCC West Hall has a food court.", bySlug["food"]?.content)
    }

    @Test
    fun `resolveHeadingSlug prefers TOC prefix over full line`() {
        val toc = setOf("dates-and-times", "food")
        val seen = mutableMapOf<String, Int>()
        assertEquals(
            "dates-and-times",
            resolveHeadingSlug("Dates and Times Badge pickup begins", toc, seen),
        )
    }
}
