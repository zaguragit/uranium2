package posidon.uranium.engine.ui

import posidon.uranium.engine.graphics.Texture
import posidon.library.types.Vec2f

class HotBar : View(
    Vec2f(0f, -1 + 0.5f / WIDTH_TO_HEIGHT_RATIO),
    Vec2f(1f, 1 / WIDTH_TO_HEIGHT_RATIO), Texture("res/textures/ui/hotbar.png")) {
    companion object { private const val WIDTH_TO_HEIGHT_RATIO = 23 / 3f }
}