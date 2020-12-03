package org.otzaf.exec

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.otzaf.exec.instr.*

/*
    todo list:
    - negation
    - bit shift left, right
    - array instructions? store, load, indexing
    - byte instructions
    - longs
    - doubles
    - conversion of int to double and vice versa?
    - goto address
    - registers?
    - increment, decrement
    - ifeq, ifne, iflt, ifle, ifgt, ifge - if<cond> is true then jump to given addr (param1)

 */
internal class BasicExecutionEngineTest {
    lateinit var eng: BasicExecutionEngine

    @BeforeEach
    fun setup() {
        eng = BasicExecutionEngine()
    }

    @Test
    fun `stack values can be stored in registers`() {
        val program = arrayOf(
            PushI(15),
            PushI(12),
            MulI(),             // stack contains 180 = 15 * 12
            StoreI(1)    // 180 goes in reg 1
        )
        eng.executeProgram(program)

        assertThat(eng.context.loadInt(1), equalTo(180))
        assertThat("stack should be empty", eng.context.empty())
    }

    @Test
    fun `an integer can be stored on the stack`() {
        val expected = 5
        eng.execute(PushI(expected))
        assertThat(expected, equalTo(eng.context.popInt()))
    }

    @ParameterizedTest
    @CsvSource(
        "0,0,0",
        "0,1,1",
        "1,0,1",
        "1,1,0"
    )
    fun `xor boolean operator`(a: Int, b: Int, expected: Int) {
        val program = arrayOf(
            PushI(a),
            PushI(b),
            XorI()
        )
        eng.executeProgram(program)
        assertThat(eng.context.popInt(), equalTo(expected))
    }

    @ParameterizedTest
    @CsvSource(
        "0,0,0",
        "0,1,1",
        "1,0,1",
        "1,1,1"
    )
    fun `or boolean operator`(a: Int, b: Int, expected: Int) {
        val program = arrayOf(
            PushI(a),
            PushI(b),
            OrI()
        )
        eng.executeProgram(program)
        assertThat(eng.context.popInt(), equalTo(expected))
    }

    @ParameterizedTest
    @CsvSource(
        "0,0,0",
        "0,1,0",
        "1,0,0",
        "1,1,1"
    )
    fun `and boolean operator`(a: Int, b: Int, expected: Int) {
        val program = arrayOf(
            PushI(a),
            PushI(b),
            AndI()
        )
        eng.executeProgram(program)
        assertThat(eng.context.popInt(), equalTo(expected))
    }

    @ParameterizedTest
    @CsvSource(
        "30,8,1",
        "8,30,-1",
        "30,30,0"
    )
    fun `greater than instruction pushes true on the stack`(a: Int, b: Int, expected: Int) {
        val program = arrayOf(
            PushI(a),
            PushI(b),
            CmpI()
        )

        eng.executeProgram(program)

        assertThat(eng.context.popInt(), equalTo(expected))
    }

    @Test
    fun `multiple arithmetic instructions can be used in combination`() {
        val a1 = 6
        val a2 = 8
        val b1 = 5
        val b2 = 5

        val program = arrayOf(
            PushI(a1),
            PushI(a2),
            MulI(), // 6 * 8 = 48

            PushI(b1),
            PushI(b2),
            MulI(), // 5 * 5 = 25

            SubI() // 48 - 25 = 23
        )

        eng.executeProgram(program)

        assertThat(
            eng.context.popInt(), equalTo(
                (a1 * a2) - (b1 * b2)
            )
        )
    }

    @Test
    fun `multiplication instructions produces the correct result`() {
        // given a basic program
        val a = 8
        val b = 8

        val program: Array<Instruction> = arrayOf(
            PushI(a),
            PushI(b),
            MulI()
        )

        // when we execute
        eng.executeProgram(program)

        // then the last value on the stack is the result
        val expected = a * b
        assertThat(eng.context.popInt(), equalTo(expected))
    }

    @Test
    fun `remainder instructions produces the correct result`() {
        // given a basic program
        val a = 8
        val b = 3

        val program: Array<Instruction> = arrayOf(
            PushI(a),
            PushI(b),
            RemI()
        )

        // when we execute
        eng.executeProgram(program)

        // then the last value on the stack is the result
        val expected = a % b
        assertThat(eng.context.popInt(), equalTo(expected))
    }

    @Test
    fun `division instructions produces the correct result`() {
        // given a basic program
        val a = 8
        val b = 2

        val program: Array<Instruction> = arrayOf(
            PushI(a),
            PushI(b),
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
            PushI(a),
            PushI(b),
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
            PushI(a),
            PushI(b),
            AddI()
        )

        // when we execute
        eng.executeProgram(program)

        // then the last value on the stack is the result
        assertThat(eng.context.popInt(), equalTo(a + b))
    }

    @Test
    fun `stack cannot handle many megabytes of values`() {
        val maxIntCapacity = (eng.context.stack.size / INT_SIZE) + 1

        // index oob is thrown
        assertThrows<IndexOutOfBoundsException> {
            // given a lot of instructions
            // (32,767 * 4) * 16 => ~2MB of integers
            val instructions = (1..(maxIntCapacity)).map { PushI(it) }

            // when we execute them
            eng.executeProgram(instructions.toTypedArray())
        }
    }

    @Test
    fun `stack can handle megabytes of values`() {
        // given a lot of instructions
        // 32,767 * 4 * 8 = > 1MB of integers. approx more than half of the stack allocation
        val instructions = (1..Short.MAX_VALUE).map { PushI(it) }

        // when we execute them
        eng.executeProgram(instructions.toTypedArray())

        // then the stack contains all of these values
        // because we are iterating from 1 to MAX_VALUE, none of these
        // values should be zero
        repeat((1..Short.MAX_VALUE).count()) {
            assertThat(eng.context.popInt(), not(equalTo(0)))
        }
    }

}