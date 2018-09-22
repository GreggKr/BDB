package me.greggkr.bdb.util

import javax.script.ScriptEngineManager

val scriptingEngine = ScriptEngineManager().getEngineByExtension("kts")!!

fun evalImports(imports: List<String>) {
    eval(imports.joinToString(";import ", "import "))
}

fun eval(script: String, values: Map<String, Any> = emptyMap()): Any? {
    for (e in values) {
        scriptingEngine.put(e.key, e.value)
    }

    return scriptingEngine.eval(script)
}