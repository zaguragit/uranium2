package posidon.uranium.engine.graphics

object Textures {

    enum class BlockPaths(val id: String, val path: String) {
        GRASS("grass", "block/grass.png"),
        LIGHT_BRICKS("light_bricks", "block/lightbricks.png"),
        MOONSTONE("moonstone", "block/moonstone.png"),
        MOONSTONE_BRICKS("moonstone_bricks", "block/moonstone_bricks.png"),
        STONE("stone", "block/stone.png"),
        WOOD("wood", "block/wood.png")
    }

    val blockTextures = HashMap<String, Texture>()

    fun init(path: String?) {
        val actualPath =
            if (path == null) "res/textures/"
            else "$path/textures/"
        for (value in BlockPaths.values())
            blockTextures[value.id] = Texture(actualPath + value.path)
    }

    fun clear() {
        for (texture in blockTextures.values) texture.delete()
        blockTextures.clear()
    }
}