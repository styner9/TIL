import org.ini4j.Wini

@Suppress("UNCHECKED_CAST")
class Ch19 {
    lateinit var wordPlugin: WordPlugin
    lateinit var frequencyPlugin: FrequencyPlugin

    fun loadPlugins() {

        val ini = Wini(open("Ch19-config.ini"))
        fun <T> loadPlugin(name: String): T {
            return ini.get("Plugins", name).let {
                javaClass.classLoader.loadClass(it)
            }.let {
                it.newInstance() as T
            }
        }

        wordPlugin = loadPlugin("words")
        frequencyPlugin = loadPlugin("frequencies")
    }

    interface WordPlugin {
        fun extractWords(filename: String): List<String>
    }

    class WordPlugin1: WordPlugin {
        override fun extractWords(filename: String): List<String> {
            val stopWords = open("stop_words.txt").readText()
                    .trim()
                    .split(",")
                    .plus(('a'..'z').map { it.toString() })

            return open(filename).readText()
                    .trim()
                    .replace("[\\W_]+".toRegex(), " ")
                    .toLowerCase()
                    .split(" +".toRegex())
                    .filter { it !in stopWords }
        }
    }

    class WordPlugin2: WordPlugin {
        override fun extractWords(filename: String): List<String> {
            val stopWords = open("stop_words.txt").readText()
                    .trim()
                    .split(",")
                    .plus(('a'..'z').map { it.toString() })

            return "[a-z]{2,}".toRegex()
                    .findAll(open(filename).readText().trim().toLowerCase())
                    .map { it.value }
                    .filter { it !in stopWords }
                    .toList()
        }
    }

    interface FrequencyPlugin {
        fun top25(wordList: List<String>): Map<String, Int>
    }

    class FrequencyPlugin1: FrequencyPlugin {
        override fun top25(wordList: List<String>): Map<String, Int> {
            val wordFreqs = mutableMapOf<String, Int>()
            wordList.forEach {
                wordFreqs[it] = (wordFreqs[it] ?: 0) + 1
            }
            return wordFreqs.toList().sortedByDescending { (_, v) -> v }.toMap()
        }
    }

    class FrequencyPlugin2: FrequencyPlugin {
        override fun top25(wordList: List<String>): Map<String, Int> {
            return wordList
                    .groupingBy { it }
                    .eachCount()
                    .toList()
                    .sortedByDescending { (_, v) -> v }
                    .toMap()
        }
    }
}

fun main(args: Array<String>) {
    with(Ch19()) {
        loadPlugins()
        frequencyPlugin.top25(wordPlugin.extractWords(inputFilename(args)))
                .toList()
                .prettyPrint()
    }
}