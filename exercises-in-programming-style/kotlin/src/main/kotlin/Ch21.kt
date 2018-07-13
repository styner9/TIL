@file:Suppress("UNCHECKED_CAST")

import java.io.IOException

class Ch21 {
    fun extractWords(filename: Any): Any {
        assert(filename is String) { "I need a string!" }
        assert((filename as String).isNotEmpty()) { "I need a non-empty string!" }

        val data = try {
            open(filename).readText().trim()
        } catch (e: IOException) {
            println("I/O error when opening $filename: ${e.message}")
            throw e
        }

        return data.replace("[\\W_]+".toRegex(), " ").toLowerCase().split(" +".toRegex())
    }

    fun removeStopWords(wordList: Any): Any {
        assert(wordList is List<*>) { "I need a list!" }

        val stopWords = try {
            open("stop_words.txt").readText().trim().split(",")
        } catch (e: IOException) {
            println("I/O error when opening stop_words.txt: ${e.message}")
            throw e
        }.plus(('a'..'z').map { it.toString() })

        return (wordList as List<String>).filter { it !in stopWords }
    }

    fun frequencies(wordList: Any): Any {
        assert(wordList is List<*>) { "I need a list!" }
        assert((wordList as List<*>).isNotEmpty()) { "I need a non-empty list!" }

        val wordFreqs = mutableMapOf<String, Int>()
        wordList.map { it as String }.forEach {
            wordFreqs[it] = (wordFreqs[it] ?: 0) + 1
        }
        return wordFreqs
    }

    fun sort(wordFreqs: Any): Any {
        assert(wordFreqs is Map<*, *>) { "I need a map!" }
        assert((wordFreqs as Map<*, *>).isNotEmpty()) { "I need a non-empty map!" }

        return try {
            wordFreqs.toList().map { it as Pair<String, Int> }.sortedByDescending { (_, v) -> v }
        } catch (e: Exception) {
            println("Sorted threw ${e.javaClass.name}: ${e.message}")
            throw e
        }
    }
}

fun main(args: Array<String>) {
    try {
        assert(args.isNotEmpty()) { "You idiot! I need an input file!" }

        with(Ch21()) {
            val wordFreqs = sort(frequencies(removeStopWords(extractWords(args[0]))))

            assert(wordFreqs is List<*>) { "OMG! This is not a list!" }
            assert((wordFreqs as List<*>).size > 25) { "SRSLY? Less than 25 words!" }

            (wordFreqs as List<Pair<String, Int>>).take(25).prettyPrint()
        }
    } catch (e: Exception) {
        println("Something wrong: $e")
        e.printStackTrace()
    }
}