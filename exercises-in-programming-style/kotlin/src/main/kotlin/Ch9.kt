class Ch9 {
    class TheOne(private var value: Any) {
        fun bind(func: Any) = this.also {
            @Suppress("UNCHECKED_CAST")
            it.value = (func as (Any) -> Any) (it.value)
        }

        fun printme() = println(value)
    }

    fun readFile(filepath: String) = open(filepath).readText().trim()

    fun filterChars(str: String) = str.replace("[\\W_]+".toRegex(), " ")

    fun normalize(str: String) = str.toLowerCase()

    fun scan(str: String) = str.split(" +".toRegex())

    fun removeStopWords(words: List<String>) = open("stop_words.txt").readText()
            .trim()
            .split(",")
            .plus(('a'..'z').map { it.toString() })
            .let { stopWords ->
                words.filterNot { it in stopWords }
            }

    fun frequencies(words: List<String>) = words.groupingBy { it }.eachCount()

    fun sort(wordFreqs: Map<String, Int>) = wordFreqs
            .toList()
            .sortedByDescending { (_, v) -> v }
            .toMap()

    fun top25Freqs(wordFreqs: Map<String, Int>) = wordFreqs
            .toList()
            .take(25)
            .map { "${it.first} - ${it.second}" }
            .joinToString("\n")
}

fun main(args: Array<String>) {
    with(Ch9()) {
        Ch9.TheOne(inputFilename(args, Input.MEDIUM))
                .bind(this::readFile)
                .bind(this::filterChars)
                .bind(this::normalize)
                .bind(this::scan)
                .bind(this::removeStopWords)
                .bind(this::frequencies)
                .bind(this::sort)
                .bind(this::top25Freqs)
                .printme()
    }
}