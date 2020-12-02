package org.otzaf.exec

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ExecutionEngineContextTest {
    lateinit var ctx: ExecutionEngineContext

    @BeforeEach
    fun setup() {
        ctx = ExecutionEngineContext()
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