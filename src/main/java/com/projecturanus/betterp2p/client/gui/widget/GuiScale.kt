package com.projecturanus.betterp2p.client.gui.widget

enum class GuiScale(inline val size: (Int) -> Int) {
    SMALL({ 4 }),
    NORMAL({ 6 }),
    LARGE({ 10 }),
    DYNAMIC({ availableHeight -> availableHeight / P2P_ENTRY_HEIGHT})
}
