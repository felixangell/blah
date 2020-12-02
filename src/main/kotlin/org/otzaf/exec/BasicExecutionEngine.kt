package org.otzaf.exec

import org.otzaf.exec.instr.Instruction
import org.otzaf.exec.instr.StoreI
import java.nio.ByteBuffer
import java.nio.ByteOrder

const val KB = 1024
const val MB = KB * 1000

class ExecutionEngineContext {
    // instruction pointer, i.e.
    // where in the program we are.
    var ip = 0

    var sp = -1
    var stack = Array<Byte>(2 * MB) { 0 }

    fun push(b: Byte) {
        stack[++sp] = b
    }

    fun pop(): Byte {
        val v = stack[sp]
        sp--
        return v
    }

    fun empty(): Boolean = sp == -1

    fun popInt(): Int {
        val bytes = ByteArray(4)
        val buff = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
        repeat((1..4).count()) { buff.put(this.pop()) }
        // we need to reverse because we pop the bytes off
        // and need to put them back into the store order
        bytes.reverse()
        return buff.getInt(0)
    }

    fun pushInt(value: Int) {
        val bytes = ByteArray(4)
        ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).putInt(value)
        bytes.forEach { this.push(it) }
    }
}

class BasicExecutionEngine : ExecutionEngine {
    val context = ExecutionEngineContext()

    override fun execute(instr: Instruction) {
        instr.execute(context)
    }

    override fun executeProgram(instrSet: Array<Instruction>) {
        while (context.ip < instrSet.size) {
            execute(instrSet[context.ip++])
        }
    }

}