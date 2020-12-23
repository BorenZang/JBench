package apacheexec

import org.apache.commons.exec.CommandLine
import org.apache.commons.exec.DefaultExecutor
import org.apache.commons.exec.ExecuteException
import org.apache.commons.exec.PumpStreamHandler

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.Runtime

fun main() {
    print("Command: ")

    // setup runtime
    val runtime = Runtime.getRuntime()
    runtime.gc()
    // parameters of running external process
    val cmd = readLine()
    val stderr: OutputStream = FileOutputStream("src/ApacheExec/error.txt")
    val stdout: OutputStream = FileOutputStream("src/ApacheExec/output.txt")
    val workDir = File(System.getProperty("user.dir"))

    // get memory usage before calling runWithoutSTD function
    val memoryBefore = (runtime.totalMemory() - runtime.freeMemory()) / MEGABYTE
    val timeBefore = System.nanoTime()
    val threadsBefore = Thread.getAllStackTraces().keys.size

    // run external call
    val exitCode = cmd?.let { runWithSTD(it,workDir,stdout,stderr) }

    // get performance number after running external call
    val threadsAfter = Thread.getAllStackTraces().keys.size - threadsBefore
    val timeAfter = System.nanoTime()
    val memoryAfter = (runtime.totalMemory() - runtime.freeMemory()) / MEGABYTE

    println("Number of threads used by apache exec is $threadsAfter")
    println("Execution time is ${(timeAfter-timeBefore)/1000000} ms")
    println("exit code is $exitCode")
    println("Memory cost by runWithoutSTD is ${memoryAfter-memoryBefore} MB")
}

fun runWithSTD(
    cmd: String,
    workingDirectory: File,
    stdout: OutputStream,
    stderr: OutputStream,
):Int {
    val commandline = CommandLine.parse(cmd) // parse command
    val exec = DefaultExecutor() // default executor object
    exec.workingDirectory = workingDirectory // set working directory
    val streamHandler = PumpStreamHandler(stdout, stderr) // stream handler
    exec.streamHandler = streamHandler

    return try {
        val code = exec.execute(commandline)
        code
    } catch (e: ExecuteException) {
        e.exitValue
    } catch (e: IOException) {
        throw e
    }
}