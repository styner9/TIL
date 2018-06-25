class Ch14 {

    class WordFrequencyFramework {
        private val loadEventHandlers = mutableListOf<Function1<String, Unit>>()
        private val doworkEventHandlers = mutableListOf<Function0<Unit>>()
        private val endEventHandlers = mutableListOf<Function0<Unit>>()

        fun registerForLoadEvent(handler: Function1<String, Unit>) {
            loadEventHandlers.add(handler)
        }

        fun registerForDoworkEvent(handler: Function0<Unit>) {
            doworkEventHandlers.add(handler)
        }

        fun registerForEndEvent(handler: Function0<Unit>) {
            endEventHandlers.add(handler)
        }

        fun run(filename: String) {
            loadEventHandlers.forEach { it(filename) }
            doworkEventHandlers.forEach { it() }
            endEventHandlers.forEach { it() }
        }
    }

    class DataStorage(wfapp: WordFrequencyFramework, private val stopWordFilter: StopWordFilter) {
        private var data: String = ""
        private var wordEventHandlers = mutableListOf<Function1<String, Unit>>()

        init {
            wfapp.registerForLoadEvent(::load)
            wfapp.registerForDoworkEvent(::produceWords)
        }

        private fun load(filename: String) {
            data = open(filename)
                    .readText()
                    .trim()
                    .replace("[\\W_]+".toRegex(), " ")
                    .toLowerCase()
        }

        private fun produceWords() {
            data.split(" +".toRegex()).forEach { word ->
                if (!stopWordFilter.isStopWord(word)) {
                    wordEventHandlers.forEach { it(word) }
                }
            }
        }

        fun registerForWordEvent(handler: Function1<String, Unit>) {
            wordEventHandlers.add(handler)
        }
    }

    class StopWordFilter(wfapp: WordFrequencyFramework) {
        private var stopWords = emptyList<String>()

        init {
            wfapp.registerForLoadEvent(::load)
        }

        private fun load(ignore: String) {
            stopWords = open("stop_words.txt")
                    .readText()
                    .trim()
                    .split(",")
                    .plus(('a'..'z').map { it.toString() })
        }

        fun isStopWord(word: String): Boolean = word in stopWords
    }

    class WordFrequencyCounter(wfapp: WordFrequencyFramework, dataStorage: DataStorage) {
        private val wordFreqs = mutableMapOf<String, Int>()

        init {
            dataStorage.registerForWordEvent(::incrementCount)
            wfapp.registerForEndEvent(::printFreqs)
        }

        private fun incrementCount(word: String) {
            wordFreqs[word] = (wordFreqs[word] ?: 0) + 1
        }

        private fun printFreqs() {
            wordFreqs.toList().sortedByDescending { (_, v) -> v }.take(25).prettyPrint()
        }
    }
}

fun main(args: Array<String>) {
    val wfapp = Ch14.WordFrequencyFramework()
    val stopWordFilter = Ch14.StopWordFilter(wfapp)
    val dataStorage = Ch14.DataStorage(wfapp, stopWordFilter)
    val wordFreqCounter = Ch14.WordFrequencyCounter(wfapp, dataStorage)
    wfapp.run(inputFilename(args, Input.MEDIUM))
}