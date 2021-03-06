package apacheexec

import org.apache.commons.exec.CommandLine
import org.apache.commons.exec.DefaultExecutor
import org.apache.commons.exec.ExecuteException
import java.io.IOException
import java.lang.Runtime
import kotlin.concurrent.thread


const val MEGABYTE = 1024L * 1024L
var exit = false
fun main(args: Array<String>) {
    print("Command: ")

    // setup runtime
    val runtime = Runtime.getRuntime()
    runtime.gc()

    // get performance number before running external call
    val cmd = readLine()
    val memoryBefore = (runtime.totalMemory() - runtime.freeMemory()) / MEGABYTE
    val timeBefore = System.nanoTime()
    val threadsBefore = Thread.getAllStackTraces().keys.size
    thread {
        while(!exit) {
            println(Thread.getAllStackTraces().keys)
        }
    }
    // run external call
    println(Thread.getAllStackTraces().keys)
    val exitCode = cmd?.let { runWithoutSTD(it) }
    // get performance number after running external call
    val threadsAfter = Thread.getAllStackTraces().keys.size - threadsBefore

    val timeAfter = System.nanoTime()
    val memoryAfter = (runtime.totalMemory() - runtime.freeMemory()) / MEGABYTE

    println("Execution time is ${(timeAfter-timeBefore)/1000000} ms")
    println("exit code is $exitCode")
    println("Memory cost by runWithoutSTD is ${memoryAfter-memoryBefore} MB")
}

fun runWithoutSTD(
    cmd: String
): Int {
    val commandline = CommandLine.parse(cmd) // parse command
    val executor = DefaultExecutor() // default executor object
    return try {
        val code = executor.execute(commandline)
        exit = true
        code
    } catch (e: ExecuteException) {
        e.exitValue
    } catch (e: IOException) {
        throw e
    }
}

