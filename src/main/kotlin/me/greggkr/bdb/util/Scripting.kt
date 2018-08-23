package me.greggkr.bdb.util

import org.jetbrains.kotlin.cli.common.environment.setIdeaIoUseFallback
import javax.script.Bindings
import javax.script.ScriptContext
import javax.script.ScriptEngineManager

fun eval(script: String, values: Map<String, Any>): Any? {
    setIdeaIoUseFallback()

    val engine = ScriptEngineManager().getEngineByExtension("kts")

    for (e in values) {
        engine.put(e.key, e.value)
    }

    return engine.eval(script)
}