package tasklist

import kotlinx.datetime.*
import java.time.format.DateTimeFormatter

//import java.time.LocalDateTime
//import java.time.LocalTime
//import java.time.ZoneOffset
//import java.time.format.DateTimeFormatter


class TaskList() {
    companion object {
        val horizontalLine = "+----+------------+-------+---+---+--------------------------------------------+"
        val columns = "|%s| %s | %s | %s | %s |%s|"
        val headerColumne =
            columns.format(
                "N".padEnd(3).padStart(4),
                "   Date   ",
                "Time ",
                "P",
                "D",
                "                   Task                     "
            )
        val header = "$horizontalLine\n$headerColumne\n$horizontalLine"

        fun printHeader() {
            println(header)
        }

        fun printTask(taskId: Int, t: Task) {
            val dt = Instant.fromEpochSeconds(t.date)
                .toLocalDateTime(TimeZone.UTC) //LocalDateTime .ofEpochSecond(date, 0, ZoneOffset.UTC)

            val currentDate = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+0")).date
            val numberOfDays = currentDate.daysUntil(dt.date)
            val due = if (numberOfDays == 0) "T" else if (numberOfDays > 0) "I" else "O"

            val priorityColorString = when (t.priority) {
                'C' -> "\u001B[101m \u001B[0m"
                'H' -> "\u001B[103m \u001B[0m"
                'N' -> "\u001B[102m \u001B[0m"
                'L' -> "\u001B[104m \u001B[0m"
                else -> " "
            }
            val dueColorString = when (due) {
                "I" -> "\u001B[102m \u001B[0m"
                "T" -> "\u001B[103m \u001B[0m"
                "O" -> "\u001B[101m \u001B[0m"
                else -> " "
            }
            val dd = t.description.lines().map {
                if (it.length > 44) it.substring(0, 44) + "\n" + it.substring(44) else it
            }.joinToString("\n").lines()

            for (line in dd.withIndex()) {
                val taskColumn = if (line.index == 0) {
                    columns.format(
                        "${taskId + 1}".padEnd(3).padStart(4),
                        t.dateString(),
                        t.timeString(),
                        priorityColorString,
                        dueColorString,
                        line.value.padEnd(44)
                    )

                } else {
                    columns.format(
                        " ".padEnd(3).padStart(4),
                        " ".repeat(10),
                        " ".repeat(5),
                        " ",
                        " ",
                        line.value.padEnd(44)
                    )

                }


                val header = taskColumn
                println(header)
            }
            println(horizontalLine)
        }
    }

    val list = mutableListOf<Task>()
//    private var lastIndex = 0;

    fun add(t: Task) {
//        lastIndex++
        list.add(t)
    }

    fun print() {

        if (isEmpty()) {
            println("No tasks have been input")
        } else {
            printHeader()
//            println(list.withIndex().joinToString("\n") {
//                "${it.index + 1}".padEnd(3) + "${it.value}\n"
//            })
            for (t in list.withIndex()) {
                printTask(t.index, t.value)
            }

//            println(list.joinToString("\n"))
        }


    }

    fun lastIndex() = list.lastIndex

    fun isEmpty() = list.isEmpty()
    fun delete(taskId: Int) {

        list.removeAt(taskId)
    }

    fun get(i: Int) = list[i]

}

class Task(description: String, var date: Long, priority: Char) {

    var priority = priority.uppercase().first()
    var description = ""
        // description.lines().joinToString("\n")  //"   ${lines.first()}"
        private set

    val isEmpty: Boolean
        get() = description.isBlank()

    init {
        updateDescription(description)
    }

//    private fun addDescLine(line: String) {
//        if (line.isNotBlank())
//            description += "\n   $line"
//    }

    fun dateString(): String? {
        val dt = Instant.fromEpochSeconds(date)
            .toLocalDateTime(TimeZone.UTC) //LocalDateTime .ofEpochSecond(date, 0, ZoneOffset.UTC)
        return dt.toJavaLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    fun timeString(): String? {
        val dt = Instant.fromEpochSeconds(date)
            .toLocalDateTime(TimeZone.UTC) //LocalDateTime .ofEpochSecond(date, 0, ZoneOffset.UTC)
        return dt.toJavaLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm"))

    }

    override fun toString(): String {
        val dt = Instant.fromEpochSeconds(date)
            .toLocalDateTime(TimeZone.UTC) //LocalDateTime .ofEpochSecond(date, 0, ZoneOffset.UTC)
//        val date = dt.toJavaLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
//        val time = dt.toJavaLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm"))

        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+0")).date
        val numberOfDays = currentDate.daysUntil(dt.date)
        val due = if (numberOfDays == 0) "T" else if (numberOfDays > 0) "I" else "O"

        return "${dateString()} ${timeString()} $priority $due\n$description"
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
            description = newP.lines().joinToString("\n")
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
//println("\u001B[101m \u001B[0m")
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
                            if (taskId - 1 > tasks.lastIndex() || taskId - 1 < 0) {
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
                            taskDeleted = true

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
    val currdate =
        Clock.System.todayAt(TimeZone.UTC).toJavaLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

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




