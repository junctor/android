import com.advice.core.utils.HtmlParser
import com.advice.core.utils.Tag
import org.junit.Assert.assertEquals
import org.junit.Test

class HtmlParserTest {
    @Test
    fun `has one link tag`() {
        val input = "Hello world, welcome to my <a href=\"https://example.com\">website</a>!"

        val tags = HtmlParser.findHtmlTags(input)

        println(tags)

        assertEquals(1, tags.size)
        assert(tags.first() is Tag.LinkTag)
    }

    @Test
    fun `has one phone number tag`() {
        val input = "Here's my phone number: <a href=\"tel:555-1234\">555-1234</a>."

        val tags = HtmlParser.findHtmlTags(input)

        println(tags)

        assertEquals(1, tags.size)
        assertEquals(Tag.PhoneTag::class.java, tags.first().javaClass)
    }

    @Test
    fun `has one email tag`() {
        val input = "You can email me at <a href=\"mailto:john@example.com\">john@example.com</a>."

        val tags = HtmlParser.findHtmlTags(input)

        println(tags)

        assertEquals(1, tags.size)
        assertEquals(Tag.EmailTag::class.java, tags.first().javaClass)
    }

    @Test
    fun `has one bold tag`() {
        val input = "I am <b>very</b> happy to meet you."

        val tags = HtmlParser.findHtmlTags(input)

        println(tags)

        assertEquals(1, tags.size)
        assertEquals(Tag.UnknownTag::class.java, tags.first().javaClass)
    }

    @Test
    fun `replace phone tag with phone number`() {
        val input = "Here's my phone number: <a href=\"tel:555-1234\">555-1234</a>."

        val tags = HtmlParser.findHtmlTags(input)
        val result = HtmlParser.replaceHtmlTags(input, tags)

        assertEquals(1, tags.size)
        assertEquals(Tag.PhoneTag::class.java, tags.first().javaClass)
        assertEquals("Here's my phone number: 555-1234.", result)
    }
}