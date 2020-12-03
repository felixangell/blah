package org.otzaf.exec

import java.nio.ByteBuffer
import java.nio.ByteOrder

class ByteConverter {
    companion object {
        fun intToBytes(value: Int): ByteArray {
            val bytes = ByteArray(INT_SIZE)
            ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).putInt(value)
            return bytes
        }

        fun bytesToInt(data: ByteArray): Int {
            val bytes = ByteArray(INT_SIZE)
            val buff = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
            buff.put(data)
            return buff.getInt(0)
        }
    }
}