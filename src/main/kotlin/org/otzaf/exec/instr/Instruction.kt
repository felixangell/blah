package org.otzaf.exec.instr

import org.otzaf.exec.ExecutionEngineContext
import java.nio.ByteBuffer
import java.nio.ByteOrder

interface Instruction {
    fun execute(context: ExecutionEngineContext)
}

class StoreI(val value: Int) : Instruction {
    override fun execute(context: ExecutionEngineContext) {
        val bytes = ByteArray(4)
        ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).putInt(value)
        bytes.forEach { context.push(it) }
    }
}