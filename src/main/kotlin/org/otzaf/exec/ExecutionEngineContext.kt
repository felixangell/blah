package org.otzaf.exec

import org.otzaf.exec.instr.Instruction

const val BYTE = 1
const val KB = 1024 * BYTE
const val MB = KB * 1024

const val INT_SIZE = 4 * BYTE
const val LONG_SIZE = 8 * BYTE

const val REGISTER_SIZE = LONG_SIZE
const val REGISTER_COUNT = 32

class StackOverflow : RuntimeException()
class StackUnderflow : RuntimeException()

interface RegisterPool {
    fun loadInt(index: Int): Int
    fun storeInt(index: Int, value: Int)
}

class ByteRegisterPool : RegisterPool {
    var mem = Array<Byte>(REGISTER_SIZE * REGISTER_COUNT) { 0 }

    override fun loadInt(index: Int): Int {
        val bytes = ByteArray(INT_SIZE)
        repeat((1..INT_SIZE).count()) { bytes[it] = mem[(index * INT_SIZE) + it] }
        return ByteConverter.bytesToInt(bytes)
    }

    override fun storeInt(index: Int, value: Int) {
        // what about if we were to store something smaller
        // than a int, e.g. a short... would we have to clear
        // the value beforehand?
        ByteConverter.intToBytes(value)
            .forEachIndexed { i, byte -> this.mem[(index * INT_SIZE) + i] = byte }
    }
}

private typealias Index = Int

enum class PoolType {
    INT_POOL
}

typealias PoolReadWriteHistoryItem = Pair<Index, PoolType>

class MockRegisterPool : RegisterPool {
    private val history = mutableListOf<PoolReadWriteHistoryItem>()
    var mem = hashMapOf<Int, Int>()

    override fun loadInt(index: Int): Int {
        history.add(PoolReadWriteHistoryItem(index, PoolType.INT_POOL))
        return mem[index]!!
    }

    override fun storeInt(index: Int, value: Int) {
        history.add(PoolReadWriteHistoryItem(index, PoolType.INT_POOL))
        mem[index] = value
    }

    fun getValueAt(it: Pair<Index, PoolType>): Int? = when (it.second) {
        PoolType.INT_POOL -> loadInt(it.first)
    }

    // last read or write action
    fun lastAction(): Pair<Index, PoolType> = history.last()
}

class ExecutionEngineContext(val mem: RegisterPool) {
    // instruction pointer, i.e.
    // where in the program we are.
    var ip = 0

    // stack pointer
    var sp = -1

    var stack = Array<Byte>(2 * MB) { 0 }

    fun push(b: Byte) {
        sp++
        if (sp >= stack.size) {
            throw StackOverflow()
        }
        stack[sp] = b
    }

    fun pop(): Byte {
        if (empty()) {
            throw StackUnderflow()
        }

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
}

class BasicExecutionEngine(private val registerPool: RegisterPool) : ExecutionEngine {
    val context = ExecutionEngineContext(registerPool)

    override fun execute(instr: Instruction) {
        instr.execute(context)
    }

    override fun executeProgram(instrSet: List<Instruction>) {
        while (context.ip < instrSet.size) {
            execute(instrSet[context.ip++])
        }
    }

}