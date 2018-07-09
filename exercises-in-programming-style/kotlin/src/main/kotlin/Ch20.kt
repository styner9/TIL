@file:Suppress("UNCHECKED_CAST")

import java.io.IOException

class Ch20 {
    fun extractWords(filename: Any): Any {
        if (filename !is String || filename.isBlank()) {
            return emptyList<String>()
        }

        val data = try {
            open(filename).readText().trim()
        } catch (e: IOException) {
            println("I/O error when opening $filename: ${e.message}")
            return emptyList<String>()
        }

        return data.replace("[\\W_]+".toRegex(), " ").toLowerCase().split(" +".toRegex())
    }

    fun removeStopWords(wordList: Any): Any {
        if (wordList !is List<*> || wordList.isEmpty()) {
            return emptyList<String>()
        }

        val stopWords = try {
            open("stop_words.txt").readText().trim().split(",")
        } catch (e: IOException) {
            println("I/O error when opening stop_words.txt: ${e.message}")
            return wordList
        }.plus(('a'..'z').map { it.toString() })

        return wordList.filter { it !in stopWords }
    }

    fun frequencies(wordList: Any): Any {
        if (wordList !is List<*> || wordList.isEmpty()) {
            return emptyMap<String, Int>()
        }

        val wordFreqs = mutableMapOf<String, Int>()
        wordList.map { it as String }.forEach {
            wordFreqs[it] = (wordFreqs[it] ?: 0) + 1
        }
        return wordFreqs
    }

    fun sort(wordFreqs: Any): Any {
        if (wordFreqs !is Map<*, *> || wordFreqs.isEmpty()) {
            return emptyList<Any>()
        }
        return wordFreqs.toList().map { it as Pair<String, Int> }.sortedByDescending { (_, v) -> v }
    }
}

fun main(args: Array<String>) {
    val filename = if (args.size > 1) args[0] else "input.txt"
    with(Ch20()) {
        (sort(frequencies(removeStopWords(extractWords(filename)))) as List<Pair<String, Int>>).take(25).prettyPrint()
    }
}