package org.otzaf.exec

import org.otzaf.exec.instr.Instruction
import org.otzaf.exec.instr.StoreI

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