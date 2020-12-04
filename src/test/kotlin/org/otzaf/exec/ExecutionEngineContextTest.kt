package org.otzaf.exec

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IndexOutOfBoundsException

internal class ExecutionEngineContextTest {
    lateinit var ctx: ExecutionEngineContext

    @BeforeEach
    fun setup() {
        ctx = ExecutionEngineContext(ByteRegisterPool())
    }

    @Test
    fun `a value stored in register index 0 can be loaded and is intact`() {
        val expected = 123
        ctx.mem.storeInt(0, expected)
        assertThat(ctx.mem.loadInt(0), equalTo(expected))
    }

    @Test
    fun `multiple values can be stored in adjacent registers`() {
        val numValues = (REGISTER_COUNT * 2)

        repeat((1..numValues).count()) {
            ctx.mem.storeInt(it, it + 1)
        }

        repeat((1..numValues).count()) {
            assertThat(ctx.mem.loadInt(it), equalTo(it + 1))
        }
    }

    @Test
    fun `exception is thrown when we run out of registers`() {
        assertThrows<IndexOutOfBoundsException> {
            // 1 too many!
            val numValues = ((REGISTER_COUNT * REGISTER_SIZE) / INT_SIZE) + 1

            repeat((1..numValues).count()) {
                ctx.mem.storeInt(it, it + 1)
            }

            repeat((1..numValues).count()) {
                assertThat(ctx.mem.loadInt(it), equalTo(it + 1))
            }
        }
    }

    @Test
    fun `integer pushed to the stack can be popped`() {
        val expected = 123456
        ctx.pushInt(expected)
        assertThat(ctx.popInt(), equalTo(expected))
    }

    @Test
    fun `most recent integer is popped`() {
        // given a stack with stuff on it
        ctx.pushInt(123456)
        ctx.pushInt(Int.MAX_VALUE)

        val expected = 222
        ctx.pushInt(expected)
        assertThat(ctx.popInt(), equalTo(expected))
    }

    @Test
    fun `a value pushed to the stack can be popped and the value matches what we pushed`() {
        ctx.push(5.toByte())
        assertThat(5, equalTo(ctx.pop()))
    }

    @Test
    fun `the most recent value is popped`() {
        ctx.push(5.toByte())
        ctx.push(6.toByte())
        ctx.push(1.toByte())
        assertThat(1, equalTo(ctx.pop()))
    }

    @Test
    fun `the pushed values are popped in LIFO order`() {
        ctx.push(5.toByte())
        ctx.push(6.toByte())
        ctx.push(1.toByte())

        assertThat(1, equalTo(ctx.pop()))
        assertThat(6, equalTo(ctx.pop()))
        assertThat(5, equalTo(ctx.pop()))
    }
}