package org.otzaf.exec.instr

import org.otzaf.exec.ExecutionEngineContext
import java.nio.ByteBuffer
import java.nio.ByteOrder

interface Instruction {
    fun execute(context: ExecutionEngineContext)
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