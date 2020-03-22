package posidon.uranium.net.packets

class MovPacket : Packet("mov") {

    var dir = 0f
    var run = false
    var keys = booleanArrayOf(false, false, false, false)
    var fly = 0

    override fun packToString() = "$dir&${if (run) '1' else '0'}&" +
            "${if (keys[0]) '1' else '0'}&" +
            "${if (keys[1]) '1' else '0'}&" +
            "${if (keys[2]) '1' else '0'}&" +
            "${if (keys[3]) '1' else '0'}&$fly"
}