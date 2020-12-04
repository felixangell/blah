package org.otzaf.gen

import org.otzaf.exec.instr.*
import org.otzaf.ir.*

/**
 * notes we can probably ensure that no nested blocks, etc.
 * exist here but that could maybe exist as a separate phase,
 * i.e. we generate the IR (eg with DSL), then we verify it,
 * then we generate code for it?
 */

interface Address {}

data class RegAddress(val index: Int) : Address
data class StackAddress(val index: Int) : Address

class ByteCodeGenerator {
    private val instructions = mutableListOf<Instruction>()
    private val localAddrMap = hashMapOf<String, Address>()

    private var freeRegister = 0

    private fun getCurrentAddr() = instructions.size
    private fun takeRegister() = RegAddress(freeRegister++)

    private fun emit(instr: Instruction): Int {
        val instrAddr = getCurrentAddr()
        instructions.add(instr)
        return instrAddr
    }

    private fun expr(v: Value) {
        when (v) {
            is BinaryExpression -> {
                expr(v.left)
                expr(v.right)
                when (v.operand) {
                    Operation.ADD -> emit(AddI())
                    Operation.MULTIPLY -> emit(MulI())
                    Operation.SUBTRACT -> emit(SubI())
                    Operation.DIVIDE -> emit(DivI())
                    Operation.REMAINDER -> emit(RemI())
                }
            }
            is IntegerValue -> emit(PushI(v.value))
            is LocalReference -> {
                val addr = localAddrMap[v.refName]
                loadValueFromAddr(addr)
            }
            else -> throw IllegalArgumentException("unhandled value $v")
        }
    }

    private fun loadValueFromAddr(addr: Address?) {
        when (addr) {
            is RegAddress -> {
                emit(LoadI(addr.index))
            }
            else -> throw IllegalArgumentException("unhandled addr $addr")
        }
    }

    private fun local(local: Local) {
        expr(local.value)

        // we should probably approach
        // register allocation differently LOL
        val reg = takeRegister()
        emit(StoreI(reg.index))

        localAddrMap[local.name] = reg
    }

    private fun gen(block: Block) {
        block.children.forEach { genInstruction(it) }
    }

    private fun genInstruction(ir: IRInstruction) {
        when (ir) {
            is Block -> gen(ir)
            is Local -> local(ir)

            else -> throw IllegalArgumentException("unhandled instruction $ir")
        }
    }

    fun generateCode(compilationUnit: CompilationUnit): MutableList<Instruction> {
        compilationUnit.children.forEach { genInstruction(it) }
        return instructions
    }
}