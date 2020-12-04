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
    lateinit var regPool: MockRegisterPool
    lateinit var execEngine: ExecutionEngine

    @BeforeEach
    fun setup() {
        regPool = MockRegisterPool()
        execEngine = BasicExecutionEngine(regPool)
    }

    @Test
    fun `a simple arithmetic program can be compiled into bytecode and executed`() {
        val lval = 15
        val rval = 30

        // given a simple program built with the dsl
        val program = compilationUnit {
            block {
                local("name", IntegerValue(lval))
                local("another", IntegerValue(rval))

                // TODO binary expr can be in the dsl
                // as can reference
                local("result", BinaryExpression(LocalReference("name"), Operation.ADD, LocalReference("another")))
            }
        }

        // when we generate code for this program
        val programInstructions = ByteCodeGenerator().generateCode(program)

        println(programInstructions.joinToString(separator = "\n") { it.toString() })

        // then the program executes without throwing
        // any errors on the execution engine
        assertDoesNotThrow {
            execEngine.executeProgram(programInstructions)
        }

        // and the last register we wrote to contains the
        // result of the value we expect
        val expected = lval + rval
        val lastRegEntry = regPool.history.last()
        val actual = regPool.getValueAt(lastRegEntry)
        assertThat(actual, equalTo(expected))
    }
}