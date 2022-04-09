package tasklist

import kotlinx.datetime.*
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

typealias MOption = Menu.MenuOption

class TaskList() {
    val list = mutableListOf<Pair<Int, Task>>()
    private var lastIndex = 0;

    fun add(t: Task) {
        lastIndex++
        list.add(lastIndex to t)
    }

    fun print() {
        println(list.joinToString("\n") {
            "${it.first}".padEnd(3) + "${it.second}\n"
        })
    }

    fun isEmpty() = list.isEmpty()

}

class Task(description: String, val date: Long, val priority: Char) {
    var description = "   $description"
        private set

    val isEmpty: Boolean
        get() = description.isBlank()


    fun addDescLine(line: String) {
        if (line.isNotBlank())
            description += "\n   $line"
    }

    override fun toString(): String {
        val dt = LocalDateTime.ofEpochSecond(date, 0, ZoneOffset.UTC)
        val date = dt.toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = dt.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))

        return "$date $time $priority\n$description"
    }
}

fun main() {
//   val optionsList = listOf(
//      MOption("1. Add matrices", "add") { performAdd() },
//   )
    var running = true

//   val menu = Menu(optionsList,"Input an action (add, print, end):")

    val tasks = TaskList()
    while (running) {
        println("Input an action (add, print, end):")

        val choice = readln().lowercase()
        when (choice) {
            "add" -> {
                val p = readTaskPriority()
                val date = readDate()

                val time = readTime()

                val dt = LocalDateTime(date.year, date.month, date.dayOfMonth, time.hour, time.minute, time.second)
                println("Input a new task (enter a blank line to end):")
                var line = readln().trimIndent()
//                var desc = ""
                if (line.isNotBlank()) {
                    val t = Task(line, dt.toInstant(TimeZone.UTC).epochSeconds, p)

                    do {
                        line = readln().trimIndent()
//                        desc += line
                        t.addDescLine(line)
                    } while (line.isNotBlank())
                    tasks.add(t)
                } else {
                    println("The task is blank")
                }

            }
            "print" -> {
                if (tasks.isEmpty()) {
                    println("No tasks have been input")
                } else {
                    tasks.print()
                }
                println()
            }
            "end" -> {
                println("Tasklist exiting!")
                running = false
            }
            else -> {
                println("The input action is invalid")
            }
        }
    }
}

fun readTime(): LocalTime {
    var correct = false
    println("Input the time (hh:mm):")
    var tmp = readln()
    while (!correct) {
        while (!"\\d\\d?:\\d\\d?(:\\d\\d?)?".toRegex().matches(tmp)) {
            println("The input time is invalid")
            println("Input the time (hh:mm):")
            tmp = readln()
        }
        tmp = tmp.split(":")
            .map { it.padStart(2, '0') }
            .joinToString(":")
        try {
            LocalTime.parse(tmp)
            correct=true
        } catch (ex: Exception) {
            correct=false
            println("The input time is invalid")
            println("Input the time (hh:mm):")
            tmp = readln()
        }
    }
    return LocalTime.parse(tmp)
}

fun readDate(): LocalDate {
    var correct = false
    println("Input the date (yyyy-mm-dd):")
    var tmp = readln()
    while (!correct) {

        while (!"\\d{4}-\\d\\d?-\\d\\d?".toRegex().matches(tmp)) {
            println("The input date is invalid")
            println("Input the date (yyyy-mm-dd):")
            tmp = readln()
        }
        tmp = tmp.split("-")
            .mapIndexed { index, s -> if (index > 0) s.padStart(2, '0') else s }
            .joinToString("-")
        try {
             LocalDate.parse(tmp)
            correct=true
        } catch (ex: Exception) {
            correct=false
            println("The input date is invalid")
            println("Input the date (yyyy-mm-dd):")
            tmp = readln()
        }
    }
    return LocalDate.parse(tmp)
}

fun readTaskPriority(): Char {
    val priorities = listOf("C", "H", "N", "L")
    println("Input the task priority (${priorities.joinToString(", ")}):")
    var tmp = readln()
    while (tmp.uppercase() !in priorities) {
        println("Input the task priority (${priorities.joinToString(", ")}):")
        tmp = readln()
    }

    return tmp.first()
}




