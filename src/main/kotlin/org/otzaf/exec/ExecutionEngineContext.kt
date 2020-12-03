package org.otzaf.exec

import org.otzaf.exec.instr.Instruction

const val BYTE = 1
const val KB = 1024 * BYTE
const val MB = KB * 1024

const val INT_SIZE = 4 * BYTE
const val LONG_SIZE = 8 * BYTE

const val REGISTER_SIZE = LONG_SIZE
const val REGISTER_COUNT = 32

class ExecutionEngineContext {
    // instruction pointer, i.e.
    // where in the program we are.
    var ip = 0

    // stack pointer
    var sp = -1

    var mem = Array<Byte>(REGISTER_SIZE * REGISTER_COUNT) { 0 }
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
        val bytes = ByteArray(INT_SIZE)
        (INT_SIZE - 1 downTo 0).forEach {
            bytes[it] = pop()
        }
        return ByteConverter.bytesToInt(bytes)
    }

    fun pushInt(value: Int) {
        ByteConverter.intToBytes(value).forEach { this.push(it) }
    }

    fun loadInt(index: Int): Int {
        val bytes = ByteArray(INT_SIZE)
        repeat((1..INT_SIZE).count()) { bytes[it] = mem[(index * INT_SIZE) + it] }
        return ByteConverter.bytesToInt(bytes)
    }

    fun storeInt(index: Int, value: Int) {
        // what about if we were to store something smaller
        // than a int, e.g. a short... would we have to clear
        // the value beforehand?
        ByteConverter.intToBytes(value)
            .forEachIndexed { i, byte -> this.mem[(index * INT_SIZE) + i] = byte }
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