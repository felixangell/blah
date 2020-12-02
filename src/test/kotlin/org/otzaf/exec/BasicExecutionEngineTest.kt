package org.otzaf.exec

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.otzaf.exec.instr.*

internal class BasicExecutionEngineTest {
    lateinit var eng: BasicExecutionEngine

    @BeforeEach
    fun setup() {
        eng = BasicExecutionEngine()
    }

    @Test
    fun `an integer can be stored on the stack`() {
        val expected = 5
        eng.execute(StoreI(expected))
        assertThat(expected, equalTo(eng.context.popInt()))
    }

    @Test
    fun `multiplication instructions produces the correct result`() {
        // given a basic program
        val a = 8
        val b = 8

        val program: Array<Instruction> = arrayOf(
            StoreI(a),
            StoreI(b),
            MulI()
        )

        // when we execute
        eng.executeProgram(program)

        // then the last value on the stack is the result
        val expected = a * b
        assertThat(eng.context.popInt(), equalTo(expected))
    }

    @Test
    fun `division instructions produces the correct result`() {
        // given a basic program
        val a = 8
        val b = 2

        val program: Array<Instruction> = arrayOf(
            StoreI(a),
            StoreI(b),
            DivI()
        )

        // when we execute
        eng.executeProgram(program)

        // then the last value on the stack is the result
        val expected = a / b
        assertThat(eng.context.popInt(), equalTo(expected))
    }

    @Test
    fun `subtraction instructions produces the correct result`() {
        // given a basic program
        val a = 9
        val b = 2

        val program: Array<Instruction> = arrayOf(
            StoreI(a),
            StoreI(b),
            SubI()
        )

        // when we execute
        eng.executeProgram(program)

        // then the last value on the stack is the result
        val expected = a - b
        assertThat(eng.context.popInt(), equalTo(expected))
    }

    @Test
    fun `addition instructions produces the correct result`() {
        // given a basic program
        val a = 5
        val b = 5

        val program: Array<Instruction> = arrayOf(
            StoreI(a),
            StoreI(b),
            AddI()
        )

        // when we execute
        eng.executeProgram(program)

        // then the last value on the stack is the result
        assertThat(eng.context.popInt(), equalTo(a + b))
    }

}