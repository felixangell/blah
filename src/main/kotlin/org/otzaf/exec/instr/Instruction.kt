package org.otzaf.exec.instr

import org.otzaf.exec.ExecutionEngineContext
import kotlin.experimental.and

interface Instruction {
    fun execute(context: ExecutionEngineContext)
}

// pops top two stack values, compares them and pushes
// a result onto the operand stack

// if a > b result = 1
// if a == b result = 0
// if a < b result = -1
class CmpI : Instruction {
    override fun execute(context: ExecutionEngineContext) {
        val b = context.popInt()
        val a = context.popInt()
        val result = when {
            a > b -> 1
            a == b -> 0
            a < b -> -1
            else -> throw RuntimeException("a is not >, ==, < than b")
        }
        context.pushInt(result)
    }
}

class AndI : Instruction {
    override fun execute(context: ExecutionEngineContext) {
        val b = context.popInt()
        val a = context.popInt()
        context.pushInt(a.toByte().and(b.toByte()).toInt())
    }
}

// store the given integer on the stack
class StoreI(val value: Int) : Instruction {
    override fun execute(context: ExecutionEngineContext) {
        context.pushInt(value)
    }
}

// pop two integers, add them, push result onto stack
class AddI : Instruction {
    override fun execute(context: ExecutionEngineContext) {
        val b = context.popInt()
        val a = context.popInt()
        context.pushInt(a + b)
    }
}

class SubI : Instruction {
    override fun execute(context: ExecutionEngineContext) {
        val b = context.popInt()
        val a = context.popInt()
        context.pushInt(a - b)
    }
}

class DivI : Instruction {
    override fun execute(context: ExecutionEngineContext) {
        val b = context.popInt()
        val a = context.popInt()
        context.pushInt(a / b)
    }
}

class MulI : Instruction {
    override fun execute(context: ExecutionEngineContext) {
        val b = context.popInt()
        val a = context.popInt()
        context.pushInt(a * b)
    }
}