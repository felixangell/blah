package org.otzaf.exec

import org.otzaf.exec.instr.Instruction

interface ExecutionEngine {
    fun execute(instr: Instruction)
    fun executeProgram(instrSet: List<Instruction>)
}