class Ch11 {

    class DataStorageManager() {
        private var data = ""

        fun dispatch(vararg message: String): Any {
            return when(message[0]) {
                "init" -> init(message[1])
                "words" -> words()
                else -> throw Exception("Message not understood ${message[0]}")
            }
        }

        private fun init(filename: String) {
            data = open(filename).readText()
                    .trim()
                    .replace("[\\W_]+".toRegex(), " ")
                    .toLowerCase()
        }

        private fun words() = data.split(" +".toRegex())
    }

    class StopWordManager {
        private var stopWords = emptyList<String>()

        fun dispatch(vararg message: String): Any {
            return when(message[0]) {
                "init" -> init()
                "is_stop_word" -> isStopWord(message[1])
                else -> throw Exception("Message not understood ${message[0]}")
            }
        }

        private fun init() {
            stopWords = open("stop_words.txt").readText()
                    .split(",")
                    .plus(('a'..'z').map { it.toString() })
        }

        private fun isStopWord(word: String) = word in stopWords
    }

    class WordFrequencyManager {
        private var wordFreqs = mutableMapOf<String, Int>()

        fun dispatch(vararg message: String): Any {
            return when(message[0]) {
                "increment_count" -> incrementCount(message[1])
                "sorted" -> sorted()
                else -> throw Exception("Message not understood ${message[0]}")
            }
        }

        private fun incrementCount(word: String) {
            wordFreqs[word] = (wordFreqs[word] ?: 0) + 1
        }

        private fun sorted() = wordFreqs.toList().sortedByDescending { (_, v) -> v }.toMap()
    }

    class WordFrequencyController() {
        private var storageManager: DataStorageManager? = null
        private var stopWordManager: StopWordManager? = null
        private var wordFrequencyManager: WordFrequencyManager? = null

        fun dispatch(vararg message: String): Any {
            return when(message[0]) {
                "init" -> init(message[1])
                "run" -> run()
                else -> throw Exception("Message not understood ${message[0]}")
            }
        }

        private fun init(filename: String) {
            storageManager = DataStorageManager()
            stopWordManager = StopWordManager()
            wordFrequencyManager = WordFrequencyManager()

            storageManager!!.dispatch("init", filename)
            stopWordManager!!.dispatch("init")
        }

        @Suppress("UNCHECKED_CAST")
        private fun run() {
            (storageManager!!.dispatch("words") as List<String>).forEach { word ->
                if (!(stopWordManager!!.dispatch("is_stop_word", word) as Boolean)) {
                    wordFrequencyManager!!.dispatch("increment_count", word)
                }
            }

            (wordFrequencyManager!!.dispatch("sorted") as Map<String, Int>)
                    .toList()
                    .take(25)
                    .prettyPrint()
        }
    }
}

fun main(args: Array<String>) {
    with(Ch11.WordFrequencyController()) {
        dispatch("init", inputFilename(args, Input.MEDIUM))
        dispatch("run")
    }
}
