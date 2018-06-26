class Ch15 {

    enum class EventType {
        LOAD, START, WORD, EOF, VALID_WORD, PRINT, RUN
    }

    data class Event(val type: EventType, val data: Any)

    class EventManager {
        private val subscriptions = mutableMapOf<EventType, MutableList<Function1<Event, Unit>>>()

        fun subscribe(eventType: EventType, handler: Function1<Event, Unit>) {
            when(eventType in subscriptions) {
                true -> subscriptions[eventType]!!.add(handler)
                false -> subscriptions[eventType] = mutableListOf(handler)
            }
        }

        fun publish(event: Event) {
            subscriptions[event.type]?.forEach { it(event) }
        }
    }

    class DataStorage(private val eventManager: EventManager) {
        private var data: String = ""

        init {
            eventManager.subscribe(EventType.LOAD, ::load)
            eventManager.subscribe(EventType.START, ::produceWords)
        }

        private fun load(event: Event) {
            data = open(event.data as String)
                    .readText()
                    .trim()
                    .replace("[\\W_]+".toRegex(), " ")
                    .toLowerCase()
        }

        private fun produceWords(event: Event) {
            data.split(" +".toRegex()).forEach { word ->
                eventManager.publish(Event(EventType.WORD, word))
            }
            eventManager.publish(Event(EventType.EOF, Unit))
        }
    }

    class StopWordFilter(private val eventManager: EventManager) {
        private var stopWords = emptyList<String>()

        init {
            eventManager.subscribe(EventType.LOAD, ::load)
            eventManager.subscribe(EventType.WORD, ::isStopWord)
        }

        private fun load(event: Event) {
            stopWords = open("stop_words.txt")
                    .readText()
                    .trim()
                    .split(",")
                    .plus(('a'..'z').map { it.toString() })
        }

        private fun isStopWord(event: Event) {
            with(event.data as String) {
                if (this !in stopWords) {
                    eventManager.publish(Event(EventType.VALID_WORD, this))
                }
            }
        }
    }

    class WordFrequencyCounter(private val eventManager: EventManager) {
        private val wordFreqs = mutableMapOf<String, Int>()

        init {
            eventManager.subscribe(EventType.VALID_WORD, ::incrementCount)
            eventManager.subscribe(EventType.PRINT, ::printFreqs)
        }

        private fun incrementCount(event: Event) {
            with(event.data as String) {
                wordFreqs[this] = (wordFreqs[this] ?: 0) + 1
            }
        }

        private fun printFreqs(event: Event) {
            wordFreqs.toList().sortedByDescending { (_, v) -> v }.take(25).prettyPrint()
        }
    }

    class WordFrequencyApplication(private val eventManager: EventManager) {
        init {
            eventManager.subscribe(EventType.RUN, ::run)
            eventManager.subscribe(EventType.EOF, ::stop)
        }

        private fun run(event: Event) {
            eventManager.publish(Event(EventType.LOAD, event.data))
            eventManager.publish(Event(EventType.START, Unit))
        }

        private fun stop(event: Event) {
            eventManager.publish(Event(EventType.PRINT, Unit))
        }
    }
}

fun main(args: Array<String>) {
    Ch15.EventManager().apply {
        Ch15.DataStorage(this)
        Ch15.StopWordFilter(this)
        Ch15.WordFrequencyCounter(this)
        Ch15.WordFrequencyApplication(this)
    }.publish(Ch15.Event(Ch15.EventType.RUN, inputFilename(args, Input.MEDIUM)))
}