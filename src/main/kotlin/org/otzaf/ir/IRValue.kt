package org.otzaf.ir

interface Value

data class IntegerValue(val value: Int) : Value

data class BinaryExpression(val left: Value, val operand: Operation, val right: Value) : Value

data class LocalReference(val refName: String) : Value

enum class Operation {
    ADD,
    MULTIPLY,
    SUBTRACT,
    DIVIDE,
    REMAINDER,
}
