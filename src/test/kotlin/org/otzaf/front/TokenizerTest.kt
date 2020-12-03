package org.otzaf.front

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.hasItems
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.junit.jupiter.api.Test

internal class TokenizerTest {
    @Test
    fun `skips space after identifier`() {
        val tokens = Tokenizer("hello ").tokenize()
        assertThat(tokens.first().lexeme, equalTo("hello"))
    }

    @Test
    fun `tokenizes words`() {
        val actual = Tokenizer("hello world this is a    test").tokenize()

        val lexemes = actual.map { it.lexeme }
        assertThat(lexemes, contains("hello", "world", "this", "is", "a", "test"))

        assertThat(
            actual, contains(
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
        val actual = Tokenizer("val foo_barBaz123").tokenize()
        assertThat(
            actual, hasItems(
                Token("foo_barBaz123", TokenType.Identifier),
                Token("val", TokenType.Identifier)
            )
        )
    }

    @Test
    fun `tokenizes identifiers and decimal numbers`() {
        val actual = Tokenizer("hello world 123 is not the same as 456").tokenize()
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
        val actual = Tokenizer("world -123 is not the same as 456.123").tokenize()
        assertThat(
            actual, contains(
                Token("456.123", TokenType.Floating),
                Token("world", TokenType.Identifier),
                Token("-123", TokenType.Decimal)
            )
        )
    }

    @Test
    fun `tokenizes symbols squished together`() {
        val actual = Tokenizer("1+2/3*5==6").tokenize()

        assertThat(
            actual, contains(
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
    }

    @Test
    fun `tokenizes symbols`() {
        val actual = Tokenizer("1 + 2 / 3 * 5 == 6").tokenize()

        assertThat(
            actual, contains(
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
    }
}