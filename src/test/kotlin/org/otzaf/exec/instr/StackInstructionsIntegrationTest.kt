package org.otzaf.exec.instr

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.otzaf.exec.ExecutionEngineContext
import java.nio.ByteBuffer
import java.nio.ByteOrder

internal class StackInstructionsIntegrationTest {
    lateinit var context: ExecutionEngineContext

    @BeforeEach
    fun setup() {
        context = ExecutionEngineContext()
    }

    @Test
    fun `stack malformation means that the integer we pushed cannot be read`() {
        // given we push an int value to the stack
        val initialValue = Int.MAX_VALUE - 2
        StoreI(initialValue).execute(context)

        // and we malform the stack
        context.push(5.toByte())

        // the value is not what we expect
        assertThat(context.popInt(), not(equalTo(initialValue)))

        // and a value is still present as we malformed the stack
        assertThat("stack is empty", !context.empty())
    }

    @Test
    fun `StoreI pushes a big integer to the stack which can be popped and interpreted as an int`() {
        val expected = Int.MAX_VALUE - 2
        StoreI(expected).execute(context)
        assertThat(context.popInt(), equalTo(expected))
    }
}

fun ExecutionEngineContext.popInt(): Int {
    val bytes = ByteArray(4)
    val buff = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
    repeat((1..4).count()) { buff.put(this.pop()) }
    // we need to reverse because we pop the bytes off
    // and need to put them back into the store order
    bytes.reverse()
    return buff.getInt(0)
}