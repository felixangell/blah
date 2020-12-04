package org.otzaf.front

import kotlin.math.min

data class Token(val lexeme: String, val type: TokenType)

fun singleToken(lexeme: String, type: TokenType): List<Token> = listOf(Token(lexeme, type))

enum class TokenType {
    Identifier,
    Decimal,
    Floating,
    Symbol
}

class Tokenizer(private val input: String) {
    var pos = 0

    private fun peek() = input[pos]
    private fun consume() = input[pos++]

    private fun consumeWhile(pred: (it: Char) -> Boolean, type: TokenType): Token {
        val start = pos
        var end = start
        while (hasNext() && pred(peek())) {
            consume()
            end++
        }
        return Token(input.slice(start until min(end, input.length)), type)
    }

    fun tokenize(): List<Token> {
        val tokens = mutableListOf<Token>()
        while (hasNext()) {
            val curr = peek()
            val tok = when {
                isIdentifierStart(curr) -> {
                    consumeWhile(::isIdentifier, TokenType.Identifier)
                }
                isDecimalOrFloat(curr) -> {
                    consumeWhile(::isDecimalOrFloat, TokenType.Decimal)
                }
                isJunk(curr) -> skip()
                else -> {
                    tokenizeSymbolOrJunk("")
                    skip()
                }
            }
            tok?.let { tokens.add(it) }
        }
        return tokens
    }

    private fun skip(): Nothing? {
        consume()
        return null
    }

    private fun hasNext() = pos < input.length

    private fun tokenizeSymbolOrJunk(it: String): List<Token> {
        return emptyList()
    }
}

fun isValidSymbol(ch: Char): Boolean =
    hashSetOf('+', '-', '/', '=', '*').contains(ch)

fun isDecimalOrFloat(it: Char): Boolean = when (it) {
    in '0'..'9' -> true
    '.' -> true
    else -> false
}

fun isJunk(ch: Char): Boolean = when {
    ch <= ' ' -> true
    ch == '\t' -> true
    else -> false
}

fun isIdentifier(ch: Char): Boolean = when {
    isIdentifierStart(ch) -> true
    ch in '0'..'9' -> true
    else -> false
}

fun isIdentifierStart(it: Char): Boolean = when (it) {
    in 'a'..'z' -> true
    in 'A'..'Z' -> true
    '_' -> true
    else -> false
}
