class Ch10 {
    abstract class TFExercise {
        open fun info() = javaClass.name
    }

    class DataStorageManager(filename: String): TFExercise() {
        private val data = open(filename).readText()
                .trim()
                .replace("[\\W_]+".toRegex(), " ")
                .toLowerCase()

        fun words() = data.split(" +".toRegex())

        override fun info() = "${super.info()}: My major data structure is a ${data.javaClass.name}"
    }

    class StopWordManager: TFExercise() {
        private val stopWords = open("stop_words.txt").readText()
                .split(",")
                .plus(('a'..'z').map { it.toString() })

        fun isStopWord(word: String) = word in stopWords

        override fun info() = "${super.info()}: My major data structure is a ${stopWords.javaClass.name}"
    }

    class WordFrequencyManager: TFExercise() {
        private val wordFreqs = mutableMapOf<String, Int>()

        fun incrementCount(word: String) {
            wordFreqs[word] = (wordFreqs[word] ?: 0) + 1
        }

        fun sorted() = wordFreqs.toList().sortedByDescending { (_, v) -> v }.toMap()

        override fun info() = "${super.info()}: My major data structure is a ${wordFreqs.javaClass.name}"
    }

    class WordFrequencyController(filename: String): TFExercise() {
        private val storageManager = DataStorageManager(filename)
        private val stopWordManager = StopWordManager()
        private val wordFrequencyManager = WordFrequencyManager()

        fun run() {
            storageManager.words().forEach { word ->
                if (!stopWordManager.isStopWord(word)) {
                    wordFrequencyManager.incrementCount(word)
                }
            }

            wordFrequencyManager.sorted().toList().take(25).prettyPrint()
        }
    }
}

fun main(args: Array<String>) {
    Ch10.WordFrequencyController(inputFilename(args, Input.MEDIUM)).run()
}