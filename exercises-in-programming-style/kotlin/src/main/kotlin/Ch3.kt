fun main(args: Array<String>) {
    val wordFreqs = mutableListOf<MutablePair>()

    val stopWords = open("stop_words.txt").useLines { lines ->
        lines.map { it.split(",") }.flatten().toList().map { it.toLowerCase() }
    }

    fun <E> MutableList<E>.swap(i1: Int, i2: Int) {
        val tmp = this[i1]
        this[i1] = this[i2]
        this[i2] = tmp
    }

    open(inputFilename(args)).useLines { lines ->
        lines.forEach { line ->
            var startChar: Int? = null
            var i = 0
            for (c in line) {
                if (startChar == null) {
                    if (c.isLetterOrDigit()) {
                        startChar = i
                    }
                } else {
                    if (!c.isLetterOrDigit()) {
                        var found = false
                        val word = line.substring(startChar, i).toLowerCase()
                        if (word !in stopWords) {
                            var pairIndex = 0

                            for (pair in wordFreqs) {
                                if (word == pair.first) {
                                    pair.second += 1
                                    found = true
                                    break
                                }
                                pairIndex += 1
                            }

                            if (!found) {
                                wordFreqs.add(MutablePair(word, 1))
                            } else if (wordFreqs.size > 1) {
                                for (n in pairIndex downTo 0) {
                                    if (wordFreqs[pairIndex].second > wordFreqs[n].second) {
                                        wordFreqs.swap(n, pairIndex)
                                        pairIndex = n
                                    }
                                }
                            }
                        }
                        startChar = null
                    }
                }
                i += 1
            }
        }
    }

    wordFreqs.take(25).forEachIndexed { i, pair ->
        println("#${i+1}: ${pair.first} - ${pair.second}")
    }
}