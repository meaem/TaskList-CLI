package tasklist
data class Task (val description:String)
fun main() {
   println("Input the tasks (enter a blank line to end):")

   val tasks = mutableListOf<Task>()
   var line = readln().trimIndent()

   while (line.isNotBlank()){
      tasks.add(Task(line))
      line = readln().trim()
   }

   if(tasks.isEmpty()){
      println("No tasks have been input")
   }
   else{
      tasks.forEachIndexed { index, task -> println("${index+1}".padEnd(3) + task.description) }
   }
   println()
}


