@file:Suppress("UNCHECKED_CAST")

fun main(args: Array<String>) {

    val stack = mutableListOf<Any>()
    val heap = mutableMapOf<Any, Any?>()

    fun <E> MutableList<E>.pop(): E {
        return removeAt(size - 1);
    }

    fun <E> MutableList<E>.peek(): E {
        return get(size - 1)
    }

    fun <E> MutableList<E>.push(e: E) {
        add(e)
    }

    fun <E> MutableList<E>.extend(other: Iterable<E>) {
        addAll(other)
    }

    fun readFile() {
        stack.pop()
                .let { it as String }
                .let { open(it).readText() }
                .also { stack.push(it) }
    }

    fun filterChars() {
        stack.pop()
                .let { it as String }
                .let { "[\\W_]+".toRegex().replace(it, " ").trim().toLowerCase() }
                .also { stack.push(it) }
    }

    fun scan() {
        stack.pop()
                .let { it as String }
                .split(" ")
                .also { stack.extend(it) }
    }

    fun removeStopWords() {
        open("stop_words.txt")
                .readText()
                .split(",")
                .apply { stack.push(this) }

        heap["stop_words"] = stack.pop()

        heap["words"] = mutableListOf<Any>()
        while (stack.size > 0) {
            if (stack.peek() in (heap["stop_words"] as List<*>)) {
                stack.pop()
            } else {
                (heap["words"] as MutableList<Any>).add(stack.pop())
            }
        }
        stack.extend(heap["words"] as List<Any>)

        heap["stop_words"] = null
        heap["words"] = null
    }

    fun frequencies() {
        heap["word_freqs"] = mutableMapOf<String, Int>()

        while (stack.size > 0) {
            with (heap["word_freqs"] as MutableMap<String, Int>) {
                if (stack.peek() in keys) {
                    stack.push((this[stack.peek() as String]) ?: 0)
                    stack.push(1)
                    stack.push(stack.pop() as Int + stack.pop() as Int)
                } else {
                    stack.push(1)
                }

                stack.pop().let {
                    this[stack.pop() as String] = it as Int
                }
            }
        }
        stack.push(heap["word_freqs"]!!)
        heap["word_freqs"] = null
    }

    fun sort() {
        (stack.pop() as Map<String, Int>)
                .toList()
                .sortedBy { (_, value) -> value }
                .toMap()
                .run {
                    stack.extend(this.entries)
                }
    }

    stack.push(inputFilename(args))
    readFile()
    filterChars()
    scan()
    removeStopWords()
    frequencies()
    sort()

    stack.push(0)
    while ((stack.peek() as Int) < 25 && stack.size > 1) {
        heap["i"] = stack.pop()

        val (w, f) = stack.pop() as Map.Entry<String, Int>
        println("$w - $f")

        stack.push(heap["i"]!!)
        stack.push(1)
        stack.push(stack.pop() as Int + stack.pop() as Int)
    }
}