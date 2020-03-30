package posidon.uranium.engine.graphics

import posidon.library.types.Vec2f

object BlockTextures {

    const val WIDTH = 8
    const val HEIGHT = 8

    fun getUvForId(id: String) = when (id) {
        "grass" -> Vec2f(0f, 0f)
        "stone" -> Vec2f(1f, 0f)
        "moonstone" -> Vec2f(2f, 0f)
        "wood" -> Vec2f(0f, 1f)
        "moonstone_bricks" -> Vec2f(2f, 1f)
        "slime" -> Vec2f(3f, 0f)
        else -> Vec2f(7f, 7f)
    }

    lateinit var sheet: Texture private set

    fun init(path: String?) {
        val actualPath =
            if (path == null) "res/textures/"
            else "$path/textures/"
        sheet = Texture(actualPath + "block/texture.png")
    }

    fun clear() {
        sheet.delete()
    }
}