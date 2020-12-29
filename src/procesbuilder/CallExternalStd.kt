package procesbuilder


import java.io.*
import java.io.BufferedReader
import kotlin.concurrent.thread


const val MEGABYTE = 1024L * 1024L
var exit = false
fun main(){
    print("Command: ")
    val cmd = readLine()!!
    val commandList: List<String> = cmd.split(" ").map { it.trim() }
    // setup runtime
    val runtime = Runtime.getRuntime()
    runtime.gc()
    thread {
        while(!exit){
            println(Thread.getAllStackTraces().keys)
        }
    }
    // get performance number before running external call
    val memoryBefore = (runtime.totalMemory() - runtime.freeMemory()) / MEGABYTE
    val timeBefore = System.nanoTime()
    val threadsBefore = Thread.getAllStackTraces().keys.size

    // run external call
    val exitCode = runProcessStd(commandList)

    // get performance number after running external call
    val threadsAfter = Thread.getAllStackTraces().keys.size - threadsBefore
    val timeAfter = System.nanoTime()
    val memoryAfterExternalCall = (runtime.totalMemory() - runtime.freeMemory()) / MEGABYTE

    println("Number of threads used by ProcessBuilder is $threadsAfter")
    println("Execution time is ${(timeAfter-timeBefore)/1000000} ms")
    println("exit code is $exitCode")
    println("Memory cost by runWithoutSTD is ${memoryAfterExternalCall-memoryBefore} MB")

}

fun runProcessStd(
    commandList: List<String>
):Int {
    val processBuilder = ProcessBuilder()
    val errorFile = File("./", "error.txt")
    val outputFile = File("./", "output.txt")
    processBuilder.command(commandList)
        .redirectError(errorFile)
        .redirectOutput(outputFile)
    val process = processBuilder.start()
    val exitCode = process.waitFor()
    exit = true
    return try {
        exitCode
    } catch (e: IOException) {
        exitCode
    }
}