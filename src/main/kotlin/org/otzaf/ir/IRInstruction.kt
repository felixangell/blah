package org.otzaf.ir

interface IRInstruction {
    override fun toString(): String
}

var BlockID = 0

@IRMarker
class Local(val name: String, val value: Value) : IRInstruction {
    override fun toString(): String {
        return "local $name = $value"
    }
}

// block contains many instructions
@IRMarker
class Block : IRInstruction {
    val children = mutableListOf<IRInstruction>()
    private val id = BlockID++;

    override fun toString(): String {
        return """
            |block($id){
            |${children.map { it.toString() }.joinToString(separator = ";\t\n")}}
            |}""".trimMargin()
    }

    fun getLocal(name: String): Local? =
        children.filterIsInstance<Local>().firstOrNull { it.name == name }
}

@IRMarker
class CompilationUnit : IRInstruction {
    val children = mutableListOf<IRInstruction>()

    override fun toString(): String {
        return """
            |compilationUnit {
            |${children.map { it.toString() }.joinToString(separator = "\n:")}}
            |}
        """.trimMargin()
    }
}