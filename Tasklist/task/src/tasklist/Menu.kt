package tasklist

class Menu(val options: List<MenuOption>,val menuHeaderr:String="") {
    val menu = options.map { it.optionString }.joinToString("\n")
    fun display() {
        if (menuHeaderr.isNotBlank())
            println(menuHeaderr)
        println(menu)
    }

    fun matchedOption(choice: String) = options.firstOrNull { it.choiceMatch(choice) }

    class MenuOption(val optionString: String, private val choice: String, private val action: () -> Unit) {

        fun choiceMatch(otherChoice: String) = choice == otherChoice
        fun doAction() {
            action()
        }
    }


}
