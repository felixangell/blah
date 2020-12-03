package org.otzaf.front

data class Token(val lexeme: String, val type: TokenType)

fun singleToken(lexeme: String, type: TokenType): List<Token> = listOf(Token(lexeme, type))

enum class TokenType {
    Identifier,
    Decimal,
    Floating,
    Symbol
}

class Tokenizer(private val input: String) {
    val tokens by lazy {
        input.split("\t", " ").filter { it.isNotBlank() }.map { tokenize(it) }.flatten()
    }

    private fun tokenize(it: String): List<Token> {
        return when {
            matchesIdentifier(it) -> singleToken(it, TokenType.Identifier)
            matchesDecimalNumber(it) -> singleToken(it, TokenType.Decimal)
            matchesFloatingNumber(it) -> singleToken(it, TokenType.Floating)
            else -> tokenizeSymbolOrJunk(it)
        }
    }

    private fun tokenizeSymbolOrJunk(it: String): List<Token> {
        var result = mutableListOf<Token>()
        val badChars = mutableListOf<Pair<Char, Int>>()

        it.forEachIndexed { index, ch ->
            if (isValidSymbol(ch)) {
                result.add(Token(ch.toString(), TokenType.Symbol))
            } else {
                // not a valid token! we need to re-process this.
                badChars.add(Pair(ch, index))
            }
        }

        return result
    }
}

fun isValidSymbol(ch: Char): Boolean =
    hashSetOf('+', '-', '/', '=', '*').contains(ch)

fun matchesDecimalNumber(it: String): Boolean = it.matches(Regex("[-+]?([0-9]+)"))

fun matchesFloatingNumber(it: String): Boolean = it.matches(Regex("[-+]?([0-9]*\\.[0-9]+|[0-9]+)"))

// questionable regex used here...
fun matchesIdentifier(it: String): Boolean =
    it.matches(Regex("([a-z]|[A-Z])+((_|[0-9]|[a-z]|[A-Z])*)"))
