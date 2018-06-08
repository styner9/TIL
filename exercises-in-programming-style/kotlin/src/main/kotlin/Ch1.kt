import java.io.File
import java.io.RandomAccessFile
import java.nio.file.Files
import java.nio.file.StandardOpenOption

fun main(args: Array<String>) {

    fun open(filename: String): File {
        return File(object {}.javaClass.getResource(filename).toURI())
    }

    // PART 1
    var data = arrayOf(
            // Load the list of stop words
            open("stop_words.txt").reader().use { reader ->
                CharArray(1024)
                        .also { reader.read(it) }
                        .let { String(it).split(",") }
            },          // data[0] holds the stop words
            null,       // data[1] is line (max 80 characters)
            null,       // data[2] is index of the start_char of word
            0,          // data[3] is index on characters, i = 0
            false,      // data[4] is flag indicating if word was found
            "",         // data[5] is the word
            "",         // data[6] is word,NNNN
            0           // data[7] is frequency
    )

    // Open the secondary memory
    val f = File("word_freqs").also {
        Files.write(
                it.toPath(),
                ByteArray(0),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )
    }.let { RandomAccessFile(it, "rwd") }

    // Open the input file
    open(args.let {
        if (it.size == 1) it[0]
        else "input.txt"
    }).useLines { lines ->
        // Loop over input file's lines
        lines.forEach {
            data[1] = it

            // end of input file
            if (data[1]!! == "") {
                return@forEach
            }

            // If it does not end with \n, add \n
            if (!(data[1] as String).endsWith("\n")) {
                data[1] = data[1] as String + "\n"
            }

            data[2] = null
            data[3] = 0

            // elimination of symbol c is exercise
            for (c in data[1] as String) {
                if (data[2] == null) {
                    if (c.isLetterOrDigit()) {
                        // We found the start of a word
                        data[2] = data[3]
                    }
                } else {
                    if (!c.isLetterOrDigit()) {
                        // We found the end of a word. Process it
                        data[4] = false
                        data[5] = (data[1] as String)
                                .substring(data[2] as Int, data[3] as Int)
                                .toLowerCase()

                        // Ignore words with len < 2, and stop words
                        if ((data[5] as String).length >= 2 && !(data[0] as List<*>).contains(data[5])) {
                            // Let's see if it already exists
                            while (true) {
                                data[6] = (f.readLine() ?: "").trim()
                                if (data[6] == "") {
                                    break
                                }

                                (data[6] as String).split(",").apply {
                                    data[6] = this[0]
                                    data[7] = this[1].toInt()
                                }

                                if (data[5] == data[6]) {
                                    data[7] = data[7] as Int + 1
                                    data[4] = true
                                    break
                                }
                            }

                            if (data[4] == false) {
                                f.writeBytes("%20s,%04d\n".format(data[5], 1))
                            } else {
                                f.seek(f.filePointer - 26)
                                f.writeBytes("%20s,%04d\n".format(data[5], data[7]))
                            }
                            f.seek(0)
                        }
                        // Let's reset
                        data[2] = null
                    }
                }
                data[3] = data[3] as Int + 1
            }
        }
        // We're done with the input file
    }

    // PART 2

    // Let's use the first 25 entries for the top 25 words
    data = Array(27, { _ -> null })
    data[25] = ""   // data[25] is word,freq from file
    data[26] = 0    // data[26] is freq

    while (true) {
        data[25] = (f.readLine() ?: "").trim()
        // EOF
        if (data[25] == "") {
            break
        }

        (data[25] as String).split(",").apply {
            data[25] = this[0]
            data[26] = this[1].toInt()
        }

        // Check if this word has more counts than the ones in memory
        // elimination of symbol i is exercise
        for (i in 0 until 25) {
            if (data[i] == null || ((data[i] as Array<*>)[1] as Int) < (data[26] as Int)) {
                data[i] = arrayOf(data[25], data[26])
                data[26] = null // delete the last element
                break
            }
        }
    }

    // elimination of symbol tf is exercise
    for (i in 0 until 25) {
        with(data[i] as Array<*>?) {
            if (this != null && size == 2) {
                println("[#%02d] %s - %d".format(i + 1, this[0], this[1]))
            }
        }
    }

    // We're done
    f.close()
}
