package tasklist

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

class Task(description: String) {
    var description = description
        private set

    val isEmpty: Boolean
        get() = description.isBlank()


    fun addDescLine(line: String) {
        if (line.isNotBlank())
            description += "\n    $line"
    }

    override fun toString(): String {
        return description
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
                println("Input a new task (enter a blank line to end):")
                var line = readln().trimIndent()
//                var desc = ""
                if (line.isNotBlank()) {
                    val t = Task(line)

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




