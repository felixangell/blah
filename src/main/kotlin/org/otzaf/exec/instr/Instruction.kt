package org.otzaf.exec.instr

import org.otzaf.exec.ExecutionEngineContext
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.experimental.xor

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

class XorI : Instruction {
    override fun execute(context: ExecutionEngineContext) {
        val b = context.popInt()
        val a = context.popInt()
        context.pushInt(a.toByte().xor(b.toByte()).toInt())
    }
}

class OrI : Instruction {
    override fun execute(context: ExecutionEngineContext) {
        val b = context.popInt()
        val a = context.popInt()
        context.pushInt(a.toByte().or(b.toByte()).toInt())
    }
}

class AndI : Instruction {
    override fun execute(context: ExecutionEngineContext) {
        val b = context.popInt()
        val a = context.popInt()
        context.pushInt(a.toByte().and(b.toByte()).toInt())
    }

    override fun toString(): String {
        return "andi"
    }
}

// ConstI
// StoreI but with a constant value rather than taking from the stack.

class StoreI(private val index: Int) : Instruction {
    override fun execute(context: ExecutionEngineContext) {
        val a = context.popInt()
        context.mem.storeInt(index, a)
    }

    override fun toString(): String {
        return "storei $index"
    }
}

class LoadI(private val index: Int) : Instruction {
    override fun execute(context: ExecutionEngineContext) {
        val a = context.mem.loadInt(index)
        context.pushInt(a)
    }

    override fun toString(): String {
        return "loadi $index"
    }
}

class Goto(private val addr: Int) : Instruction {
    override fun execute(context: ExecutionEngineContext) {
        context.ip = addr
    }
}

// push the given integer on the stack
class PushI(private val value: Int) : Instruction {
    override fun execute(context: ExecutionEngineContext) {
        context.pushInt(value)
    }

    override fun toString(): String {
        return "pushi $value"
    }
}

// pop two integers, add them, push result onto stack
class AddI : Instruction {
    override fun execute(context: ExecutionEngineContext) {
        val b = context.popInt()
        val a = context.popInt()
        context.pushInt(a + b)
    }

    override fun toString(): String {
        return "addi"
    }
}

class SubI : Instruction {
    override fun execute(context: ExecutionEngineContext) {
        val b = context.popInt()
        val a = context.popInt()
        context.pushInt(a - b)
    }
}

class RemI : Instruction {
    override fun execute(context: ExecutionEngineContext) {
        val b = context.popInt()
        val a = context.popInt()
        context.pushInt(a % b)
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