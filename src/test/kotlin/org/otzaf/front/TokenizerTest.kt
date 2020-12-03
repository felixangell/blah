package org.otzaf.front

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.hasItems
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class TokenizerTest {
    @Test
    fun `tokenizes words`() {
        val actual = Tokenizer("hello world this is a    test").tokens
        assertThat(
            actual, hasItems(
                Token("hello", TokenType.Identifier),
                Token("world", TokenType.Identifier),
                Token("this", TokenType.Identifier),
                Token("is", TokenType.Identifier),
                Token("a", TokenType.Identifier),
                Token("test", TokenType.Identifier)
            )
        )
    }

    @Test
    fun `tokenizes identifiers`() {
        val actual = Tokenizer("val foo_barBaz123").tokens
        assertThat(
            actual, hasItems(
                Token("foo_barBaz123", TokenType.Identifier),
                Token("val", TokenType.Identifier)
            )
        )
    }

    @Test
    fun `tokenizes identifiers and decimal numbers`() {
        val actual = Tokenizer("hello world 123 is not the same as 456").tokens
        assertThat(
            actual, hasItems(
                Token("hello", TokenType.Identifier),
                Token("world", TokenType.Identifier),
                Token("123", TokenType.Decimal),
                Token("456", TokenType.Decimal)
            )
        )
    }

    @Test
    fun `tokenizes decimal and floating point numbers`() {
        val actual = Tokenizer("world -123 is not the same as 456.123").tokens
        assertThat(
            actual, hasItems(
                Token("456.123", TokenType.Floating),
                Token("world", TokenType.Identifier),
                Token("-123", TokenType.Decimal)
            )
        )
    }

    @Test
    fun `legal identifiers are matched`() {
        assertTrue(matchesIdentifier("validIdentifier"))
        assertTrue(matchesIdentifier("another_valid_identifier"))
        assertTrue(matchesIdentifier("also_valid_123"))
        assertTrue(matchesIdentifier("and____this_is_valid__too123"))

        assertFalse(matchesIdentifier("___thisIsNotValid"))
        assertFalse(matchesIdentifier("123norIsThisValid"))
        assertFalse(matchesIdentifier("and this isnt valid either"))

        assertFalse(matchesIdentifier("nor'isthisvalid"))
    }

    @Test
    fun `tokenizes symbols squished together`() {
        val actual = Tokenizer("1+2/3*5==6").tokens

        assertThat(
            actual, equalTo(
                listOf(
                    Token("1", TokenType.Decimal),
                    Token("+", TokenType.Symbol),
                    Token("2", TokenType.Decimal),
                    Token("/", TokenType.Symbol),
                    Token("3", TokenType.Decimal),
                    Token("*", TokenType.Symbol),
                    Token("5", TokenType.Decimal),
                    Token("=", TokenType.Symbol),
                    Token("=", TokenType.Symbol),
                    Token("6", TokenType.Decimal)
                )
            )
        )
    }

    @Test
    fun `tokenizes symbols`() {
        val actual = Tokenizer("1 + 2 / 3 * 5 == 6").tokens

        assertThat(
            actual, equalTo(
                listOf(
                    Token("1", TokenType.Decimal),
                    Token("+", TokenType.Symbol),
                    Token("2", TokenType.Decimal),
                    Token("/", TokenType.Symbol),
                    Token("3", TokenType.Decimal),
                    Token("*", TokenType.Symbol),
                    Token("5", TokenType.Decimal),
                    Token("=", TokenType.Symbol),
                    Token("=", TokenType.Symbol),
                    Token("6", TokenType.Decimal)
                )
            )
        )
    }
}