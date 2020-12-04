package org.otzaf.ir

@DslMarker
annotation class IRMarker

fun compilationUnit(init: CompilationUnit.() -> Unit) = CompilationUnit().apply{ init() }

fun CompilationUnit.block(init: Block.() -> Unit) = Block().also {
    it.init()
    children.add(it)
}

fun Block.local(name: String, value: Value) {
    children.add(Local(name, value))
}