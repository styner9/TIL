fun main(args: Array<String>) {
    val stops = open("stop_words.txt").readText().trim().split(",")

    open(inputFilename(args, Input.MEDIUM)).readText().toLowerCase().replace("[^a-z]".toRegex(), " ").split(" ")
            .filter { it.matches("[a-z]{2,}".toRegex()) && it !in stops }
            .groupingBy { it }.eachCount().toList()
            .sortedByDescending { (_, v) -> v }
            .take(25)
            .forEach { println(it) }
}