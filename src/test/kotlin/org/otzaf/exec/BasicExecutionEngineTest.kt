package org.otzaf.exec

import org.junit.jupiter.api.Test
import org.otzaf.exec.instr.StoreI

internal class BasicExecutionEngineTest {

    @Test
    fun `an integer can be stored on the stack`() {
        BasicExecutionEngine().execute(StoreI(5))
    }

    @Test
    fun `simple program can execute`() {
        val eng = BasicExecutionEngine()
        eng.executeProgram(arrayOf(
            // storei 5
            // storei 5
            // addi
        ))
    }

}