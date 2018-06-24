class Ch13 {

    interface IDataStorage {
        fun words(): List<String>
    }

    interface  IStopWordFilter {
        fun isStopWord(word: String): Boolean
    }

    interface IWordFrequencyCounter {
        fun incrementCount(word: String)
        fun sorted(): Map<String, Int>
    }

    class DataStorageManager(private val filename: String): IDataStorage {
        private val data = open(filename)
                .readText()
                .trim()
                .replace("[\\W_]+".toRegex(), " ")
                .toLowerCase()
                .split(" +".toRegex());

        override fun words(): List<String> = data
    }

    class StopWordManager: IStopWordFilter {
        private val stopWords = open("stop_words.txt")
                .readText()
                .trim()
                .split(",")
                .plus(('a'..'z').map { it.toString() })

        override fun isStopWord(word: String): Boolean = word in stopWords
    }

    class WordFrequencyManager: IWordFrequencyCounter {
        private val wordFreqs = mutableMapOf<String, Int>()

        override fun incrementCount(word: String) {
            wordFreqs[word] = (wordFreqs[word] ?: 0) + 1
        }

        override fun sorted(): Map<String, Int> {
            return wordFreqs.toList().sortedByDescending { (_, v) -> v }.toMap()
        }
    }

    class WordFrequencyController(private val filename: String) {
        private val storage = DataStorageManager(filename)
        private val stopWordManager = StopWordManager()
        private val wordFreqCounter = WordFrequencyManager()

        fun run() {
            storage.words().forEach { word ->
                if (!stopWordManager.isStopWord(word)) {
                    wordFreqCounter.incrementCount(word)
                }
            }

            wordFreqCounter.sorted().toList()
                    .take(25)
                    .prettyPrint()
        }
    }
}

fun main(args: Array<String>) {
    Ch13.WordFrequencyController(inputFilename(args, Input.MEDIUM)).run()
}