package org.otzaf.gen

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.otzaf.exec.BasicExecutionEngine
import org.otzaf.exec.ExecutionEngine
import org.otzaf.exec.MockRegisterPool
import org.otzaf.ir.*

internal class ByteCodeGeneratorTest {
    private lateinit var regPool: MockRegisterPool
    private lateinit var execEngine: ExecutionEngine

    @BeforeEach
    fun setup() {
        regPool = MockRegisterPool()
        execEngine = BasicExecutionEngine(regPool)
    }

    @Test
    fun `a simple arithmetic program can be compiled into bytecode and executed`() {
        val leftVal = 15
        val rightVal = 30
        val expected = leftVal + rightVal

        // given a simple program built with the dsl
        val program = compilationUnit {
            block {
                local("name", IntegerValue(leftVal))
                local("another", IntegerValue(rightVal))

                // TODO binary expr can be in the dsl
                // as can reference
                local("result", BinaryExpression(LocalReference("name"), Operation.ADD, LocalReference("another")))
            }
        }

        // when we run the program
        runProgram(program)

        // the last register we wrote to contains the
        // result of the value we expect
        val actual = regPool.getValueAt(regPool.lastAction())
        assertThat(actual, equalTo(expected))
    }

    fun runProgram(program: CompilationUnit) {
        val programInstructions = ByteCodeGenerator().generateCode(program)
        // println(programInstructions.joinToString(separator = "\n") { it.toString() })
        assertDoesNotThrow {
            execEngine.executeProgram(programInstructions)
        }
    }
}