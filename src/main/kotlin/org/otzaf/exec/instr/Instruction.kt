package org.otzaf.exec.instr

import org.otzaf.exec.ExecutionEngineContext

interface Instruction {
    fun execute(context: ExecutionEngineContext)
}

class StoreI(val value: Int) : Instruction {
    override fun execute(context: ExecutionEngineContext) {
        // store bytes of value on stack
    }
}