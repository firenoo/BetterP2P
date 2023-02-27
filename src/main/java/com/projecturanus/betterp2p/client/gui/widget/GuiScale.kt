package com.projecturanus.betterp2p.client.gui.widget

enum class GuiScale(inline val size: (Int) -> Int, val minHeight: Int) {
    SMALL({ 4 }, 0),
    NORMAL({ 6 }, 326),
    LARGE({ 8 }, 409),
    DYNAMIC({ availableHeight -> availableHeight / P2P_ENTRY_HEIGHT }, 0)
}
