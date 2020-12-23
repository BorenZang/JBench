package procesbuilder

import java.io.IOException





fun main(){
    print("Command: ")
    val cmd = readLine()!!
    val commandList: List<String> = cmd.split(" ").map { it.trim() }
    // setup runtime
    val runtime = Runtime.getRuntime()
    runtime.gc()

    // get performance number before running external call
    val memoryBefore = (runtime.totalMemory() - runtime.freeMemory()) / MEGABYTE
    val timeBefore = System.nanoTime()
    val threadsBefore = Thread.getAllStackTraces().keys.size

    // run external call
    val exitCode = runProcess(commandList)

    // get performance number after running external call
    val threadsAfter = Thread.getAllStackTraces().keys.size - threadsBefore
    val timeAfter = System.nanoTime()
    val memoryAfter = (runtime.totalMemory() - runtime.freeMemory()) / MEGABYTE

    println("Number of threads used by ProcessBuilder is $threadsAfter")
    println("Execution time is ${(timeAfter - timeBefore)/1000000} ms")
    println("exit code is $exitCode")
    println("Memory cost by runWithoutSTD is ${memoryAfter - memoryBefore} MB")
}

fun runProcess(
    commandList: List<String>
):Int {
    val processBuilder = ProcessBuilder()
    processBuilder.command(commandList).inheritIO()
    val process = processBuilder.start()

    val exitCode = process.waitFor()
    return try {
        exitCode
    } catch (e: IOException) {
        exitCode
    }
}