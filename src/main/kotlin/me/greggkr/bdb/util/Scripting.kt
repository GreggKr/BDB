package me.greggkr.bdb.util

import javax.script.ScriptEngineManager

val engine = ScriptEngineManager().getEngineByExtension("kts")!!

fun evalImports(imports: List<String>) {
    eval(imports.joinToString(";import ", "import "))
}

fun eval(script: String, values: Map<String, Any> = emptyMap()): Any? {

    for (e in values) {
        engine.put(e.key, e.value)
    }

    return engine.eval(script)
}