fun main(args: Array<String>) {

    val data = mutableListOf<Char>()
    val words = mutableListOf<String>()
    val wordFreqs = mutableListOf<Pair>()

    fun readFile(filepath: String) {
        open(filepath).readText().toCharArray().forEach {
            data.add(it)
        }
    }

    fun filterCharsAndNormalize() {
        data.forEachIndexed { i, c ->
            data[i] = if (c.isLetterOrDigit()) c.toLowerCase() else ' '
        }
    }

    fun scan() {
        data.joinToString("").trim().split(" +".toRegex()).apply {
            words.addAll(this)
        }
    }

    fun removeStopWords() {
        val stopWords = open("stop_words.txt")
                .readText()
                .trim()
                .split(",")
                .plus('a'..'z')

        val indexes = mutableListOf<Int>()

        words.forEachIndexed { i, word ->
            if (word in stopWords) {
                indexes.add(i)
            }
        }

        indexes.reversed().forEach {
            words.removeAt(it)
        }
    }

    fun frequencies() {
        for (w in words) {
            val keys = wordFreqs.map { it.first }
            if (w in keys) {
                wordFreqs[keys.indexOf(w)].apply {
                    second += 1
                }
            } else {
                wordFreqs.add(Pair(w, 1))
            }
        }
    }

    fun sort() {
        wordFreqs.sortByDescending { it.second }
    }

    readFile(inputFilename(args, Input.MEDIUM))
    filterCharsAndNormalize()
    scan()
    removeStopWords()
    frequencies()
    sort()

    wordFreqs.take(25).forEachIndexed { i, pair ->
        println("#${i+1}: ${pair.first} - ${pair.second}")
    }
}