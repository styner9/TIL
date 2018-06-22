@file:Suppress("UNCHECKED_CAST")

class Ch12 {

    private fun extractWords(obj: MutableMap<String, Any>, filename: String) {
        obj["data"] = open(filename).readText()
                .trim()
                .replace("[\\W_]+".toRegex(), " ")
                .toLowerCase()
                .split(" +".toRegex())
    }

    private fun loadStopWords(obj: MutableMap<String, Any>) {
        obj["stop_words"] = open("stop_words.txt").readText()
                .trim()
                .split(",")
                .plus(('a'..'z').map { it.toString() })
    }

    private fun incrementCount(obj: MutableMap<String, Any>, w: String) {
        (obj["freqs"] as MutableMap<String, Int>).apply {
            this[w] = (this[w] ?: 0) + 1
        }
    }

    val dataStorageObj: MutableMap<String, Any> by lazy {
        mutableMapOf(
                "data" to emptyList<String>(),
                "init" to { filename: String -> extractWords(dataStorageObj, filename) },
                "words" to { dataStorageObj["data"] }
        )
    }

    val stopWordsObj: MutableMap<String, Any> by lazy {
        mutableMapOf(
                "stop_words" to emptyList<String>(),
                "init" to { loadStopWords(stopWordsObj) },
                "is_stop_word" to { w: String -> w in stopWordsObj["stop_words"] as List<String> }
        )
    }

    val wordFreqsObj: MutableMap<String, Any> by lazy {
        mutableMapOf(
                "freqs" to mutableMapOf<String, Int>(),
                "increment_count" to { w: String -> incrementCount(wordFreqsObj, w) },
                "sorted" to {
                    (wordFreqsObj["freqs"] as Map<String, Int>)
                            .toList()
                            .sortedByDescending { (_, v) -> v }
                            .toMap()
                }
        )
    }

}

fun main(args: Array<String>) {
    with(Ch12()) {
        (dataStorageObj["init"] as (String) -> Any)(inputFilename(args, Input.MEDIUM))
        (stopWordsObj["init"] as () -> Any)()

        for (w in (dataStorageObj["words"] as () -> List<String>)()) {
            if (!(stopWordsObj["is_stop_word"] as (String) -> Boolean)(w)) {
                (wordFreqsObj["increment_count"] as (String) -> Any)(w)
            }
        }

        (wordFreqsObj["sorted"] as () -> Map<String, Int>)()
                .toList()
                .take(25)
                .prettyPrint()
    }
}