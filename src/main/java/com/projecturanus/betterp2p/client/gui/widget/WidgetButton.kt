package com.projecturanus.betterp2p.client.gui.widget

import net.minecraft.client.gui.GuiButton

/**
 * Widget button for stuff.
 * @param x
 * @param y
 * @param width
 * @param height
 * @param hoverText - unlocalized text to display when mouse hovering
 */
abstract class WidgetButton(x: Int, y: Int, private var width: Int, private var height: Int,
                            val hoverText: () -> String): GuiButton(0, x, y, width, height, "") {

    abstract fun mousePressed(mouseX: Int, mouseY: Int)

    fun draw(mouseX: Int, mouseY: Int, partial: Float) {
        drawRect(xPosition, yPosition, xPosition + width, yPosition + height, 0xFF shl 24)
    }

    fun setPosition(x: Int, y: Int) {
        this.xPosition = x
        this.yPosition = y
    }

    fun setSize(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    fun isHovering(mouseX: Int, mouseY: Int): Boolean {
        return mouseX > xPosition && mouseX < xPosition + width && mouseY > yPosition && mouseY < yPosition + height
    }
}
