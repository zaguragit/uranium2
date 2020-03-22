package posidon.uranium.engine.utils

import java.util.*

object HashMapUtils {
    fun newID(map: Map<Long, *>): Long {
        var id: Long
        do id = Random().nextLong() while (map.containsKey(id))
        return id
    }
}