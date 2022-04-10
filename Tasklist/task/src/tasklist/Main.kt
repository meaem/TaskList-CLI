package tasklist

import kotlinx.datetime.*
import java.time.format.DateTimeFormatter

//import java.time.LocalDateTime
//import java.time.LocalTime
//import java.time.ZoneOffset
//import java.time.format.DateTimeFormatter


class TaskList() {
    val list = mutableListOf<Task>()
//    private var lastIndex = 0;

    fun add(t: Task) {
//        lastIndex++
        list.add( t)
    }

    fun print() {
        if (isEmpty()) {
            println("No tasks have been input")
        } else {
            println(list.withIndex().joinToString("\n") {
                "${it.index+1}".padEnd(3) + "${it.value}\n"
            })
        }


    }

    fun lastIndex() = list.lastIndex

    fun isEmpty() = list.isEmpty()
    fun delete(taskId: Int) {

        list.removeAt(taskId)
    }

    fun get(i: Int) = list[i]

}

class Task(description: String, var date: Long, var priority: Char) {


    var description = description.lines().map { "   $it" }.joinToString("\n")  //"   ${lines.first()}"
        private set

    val isEmpty: Boolean
        get() = description.isBlank()


    private fun addDescLine(line: String) {
        if (line.isNotBlank())
            description += "\n   $line"
    }

    override fun toString(): String {
        val dt = Instant.fromEpochSeconds(date).toLocalDateTime(TimeZone.UTC) //LocalDateTime .ofEpochSecond(date, 0, ZoneOffset.UTC)
        val date =  dt.toJavaLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = dt.toJavaLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm"))

        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+0")).date
        val numberOfDays = currentDate.daysUntil(dt.date)
        val due = if (numberOfDays ==0 ) "T" else if (numberOfDays > 0) "I" else "O"

        return "$date $time $priority $due\n$description"
    }

    fun updateDate(year: Int, month: Month, dayOfMonth: Int): Boolean {
        try {
            val dtt = Instant.fromEpochSeconds(date, 0).toLocalDateTime(TimeZone.UTC)
            val dt = LocalDateTime(year, month, dayOfMonth, dtt.hour, dtt.minute, dtt.second)
            date = dt.toInstant(TimeZone.UTC).epochSeconds
            return true
        } catch (ex: Exception) {
            return false
        }


    }

    fun updateTime(hour: Int, minute: Int, second: Int): Boolean {
        try {
            val dtt = Instant.fromEpochSeconds(date, 0).toLocalDateTime(TimeZone.UTC)

            val dt = LocalDateTime(dtt.year, dtt.month, dtt.dayOfMonth, hour, minute, second)

            date = dt.toInstant(TimeZone.UTC).epochSeconds
            return true
        } catch (ex: Exception) {
            return false
        }
    }

    fun updateDescription(newP: String): Boolean {
        try {
            description = newP.lines().map { "   $it" }.joinToString("\n")
            return true
        } catch (ex: Exception) {
            return false
        }
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
        println("Input an action (add, print, edit, delete, end):")

        val choice = readln().lowercase()
        when (choice) {
            "add" -> {
                val p = readTaskPriority()
                val date = readDate()

                val time = readTime()

                val dt = LocalDateTime(date.year, date.month, date.dayOfMonth, time.hour, time.minute, time.second)
                val line = readDescription()

                val t = Task(line, dt.toInstant(TimeZone.UTC).epochSeconds, p)
                tasks.add(t)

            }
            "print" -> {
                tasks.print()

                println()
            }
            "end" -> {
                println("Tasklist exiting!")
                running = false
            }

            "edit" -> {
                tasks.print()
                if (!tasks.isEmpty()) {
                    var taskEdited = false
                    while (!taskEdited) {
                        try {
                            println("Input the task number (1-${tasks.lastIndex() + 1}):")
                            val taskId = readln().toInt()
                            if (taskId - 1 > tasks.lastIndex() || taskId -1 < 0) {
                                throw IllegalArgumentException()
                            }

                            println("Input a field to edit (priority, date, time, task):")
                            var field = readln().lowercase()
                            while (field !in listOf("priority", "date", "time", "task")) {
                                println("Invalid field")
                                println("Input a field to edit (priority, date, time, task):")

                                field = readln().lowercase()
                            }
                            when (field) {
                                "priority" -> {
                                    val newP = readTaskPriority()
                                    tasks.get(taskId - 1).priority = newP
                                    taskEdited = true
                                }
                                "date" -> {
                                    val t = tasks.get(taskId - 1)
                                    val newP = readDate()
                                    taskEdited = t.updateDate(newP.year, newP.month, newP.dayOfMonth)

                                }
                                "time" -> {
                                    val t = tasks.get(taskId - 1)
                                    val newP = readTime()
                                    taskEdited = t.updateTime(newP.hour, newP.minute, newP.second)
                                }
                                "task" -> {
                                    val t = tasks.get(taskId - 1)
                                    val newP = readDescription()
                                    taskEdited = t.updateDescription(newP)
                                }
                            }


                            if (taskEdited) {
                                println("The task is changed")
                            }


                        } catch (ex: Exception) {
                            println("Invalid task number")
                            taskEdited = false
                        }

                    }
                }
            }
            "delete" -> {
                tasks.print()
                if (!tasks.isEmpty()) {
                    var taskDeleted = false
                    while (!taskDeleted) {
                        try {
                            println("Input the task number (1-${tasks.lastIndex() + 1}):")
                            val taskId = readln().toInt()
                            if (taskId - 1 > tasks.lastIndex()) {
                                throw IllegalArgumentException()
                            }
                            tasks.delete(taskId - 1)
                            println("The task is deleted")
                            taskDeleted =true

                        } catch (ex: Exception) {
                            println("Invalid task number")
                            taskDeleted = false
                        }

                    }
                }
            }

            else -> {
                println("The input action is invalid")
            }
        }
    }
}

fun readDescription(): String {
    var desc = ""
    println("Input a new task (enter a blank line to end):")
    var line = readln().trimIndent()
//                var desc = ""
    if (line.isNotBlank()) {

        do {
            desc += "$line\n"
            line = readln().trimIndent()
//                        desc += line
//            t.addDescLine(line)
        } while (line.isNotBlank())

    } else {
        println("The task is blank")
    }
    return desc.removeSuffix("\n")
}

fun readTime(): LocalDateTime {
    var correct = false
    val currdate = Clock.System.todayAt(TimeZone.UTC).toJavaLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

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
            LocalDateTime.parse("${currdate}T$tmp")
            correct = true
        } catch (ex: Exception) {
            correct = false
            println("The input time is invalid")
            println("Input the time (hh:mm):")
            tmp = readln()
        }
    }
    return LocalDateTime.parse("${currdate}T$tmp")
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
            correct = true
        } catch (ex: Exception) {
            correct = false
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




