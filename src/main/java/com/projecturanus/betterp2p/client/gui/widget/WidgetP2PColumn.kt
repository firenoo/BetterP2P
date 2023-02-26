package com.projecturanus.betterp2p.client.gui.widget

import com.projecturanus.betterp2p.BetterP2P
import com.projecturanus.betterp2p.capability.P2PTunnelInfo
import com.projecturanus.betterp2p.client.gui.GuiAdvancedMemoryCard
import com.projecturanus.betterp2p.client.gui.InfoList
import com.projecturanus.betterp2p.client.gui.InfoWrapper
import com.projecturanus.betterp2p.item.BetterMemoryCardModes
import com.projecturanus.betterp2p.network.*
import net.minecraftforge.common.util.ForgeDirection
import org.lwjgl.input.Keyboard
import kotlin.reflect.KProperty0

/**
 * WidgetP2PColumn
 *
 * A widget that contains a list of P2P entries.
 */
class WidgetP2PColumn(private val gui: GuiAdvancedMemoryCard,
                      private val infos: InfoList,
                      private var x: Int, private var y: Int,
                      private val selectedInfo: KProperty0<InfoWrapper?>,
                      val mode: () -> BetterMemoryCardModes,
                      private var scrollBar: WidgetScrollBar) {

    private val entries: MutableList<WidgetP2PDevice> = mutableListOf()
    private val renameBar: IGuiTextField = IGuiTextField(0,0)

    init {
        renameBar.setMaxStringLength(50)
    }

    /**
     * Resize the column
     */
    fun resize(scale: GuiScale, availableHeight: Int) {
        entries.clear()
        for (i in 0 until scale.size(availableHeight)) {
            val widget = WidgetP2PDevice( selectedInfo, mode,
                { infos.filtered.getOrNull(i + scrollBar.currentScroll) },
                x, y + i * (P2P_ENTRY_HEIGHT + 1))
            entries.add(widget)
        }
        scrollBar.setRange(0, entries.size, 23)
    }

    fun setPosition(x: Int, y: Int) {
        this.x = x
        this.y = y
        for ((i, entry) in entries.withIndex()) {
            entry.x = x
            entry.y = y + i * (P2P_ENTRY_HEIGHT + 1)
        }
    }

    fun finishRename(){
        for (widget in entries){
            widget.renderNameTextField = true
        }
        if(renameBar.info != null && renameBar.info.name != renameBar.text){
            val info:InfoWrapper = renameBar.info
            renameBar.text = renameBar.text.trim()
            ModNetwork.channel.sendToServer(C2SP2PTunnelInfo(P2PTunnelInfo(info.posX,info.posY,info.posZ,info.dim, ForgeDirection.valueOf(info.facing.name).ordinal,renameBar.text)))
        }
        renameBar.isVisible = false
        renameBar.text = ""
        renameBar.setFocus(false)
        renameBar.info = null
    }

//private fun transportPlayer(info: InfoWrapper){
//    Minecraft.getMinecraft().thePlayer.closeScreen()
//    ModNetwork.channel.sendToServer(C2STransportPlayer(P2PTunnelInfo(info.posX,info.posY,info.posZ,info.dim,0)))
//}

    private fun onRenameButtonClicked(info:InfoWrapper, index: Int) {
//        if (GuiScreen.isShiftKeyDown()) {
//            transportPlayer(info)
//        }
        renameBar.isVisible = true
        renameBar.y = (this.y) + index * (P2P_ENTRY_HEIGHT + 1) + 19
        renameBar.x = this.x + 50
        renameBar.w = 120
        renameBar.h = 12
        renameBar.text = info.name
        renameBar.setFocus(true,0)
        renameBar.info = info
    }

    private fun onSelectButtonClicked(info: InfoWrapper) {
        gui.selectInfo(info.code)
    }

    private fun onBindButtonClicked(info: InfoWrapper) {
        if (infos.selectedEntry == NONE) return
        when (mode()) {
            BetterMemoryCardModes.INPUT -> {
                BetterP2P.logger.debug("Bind ${info.code} as input")
                ModNetwork.channel.sendToServer(C2SLinkP2P(info.code, infos.selectedEntry))
            }
            BetterMemoryCardModes.OUTPUT -> {
                BetterP2P.logger.debug("Bind ${info.code} as output")
                ModNetwork.channel.sendToServer(C2SLinkP2P(infos.selectedEntry, info.code))
            }
            BetterMemoryCardModes.COPY -> {
                val input = findInput(infos.selectedInfo?.frequency)
                if (input != null)
                    ModNetwork.channel.sendToServer(C2SLinkP2P(input.code, info.code))
            }
        }
    }

    private fun findInput(frequency: Long?) =
        infos.filtered.find { it.frequency == frequency && !it.output }

    fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        var clickRenameButton = false
        for ((index,widget) in entries.withIndex()) {
            val info = widget.infoSupplier()
            if (info?.selectButton?.mousePressed(gui.mc, mouseX, mouseY) == true) {
                onSelectButtonClicked(widget.infoSupplier()!!)
            } else if (info?.bindButton?.mousePressed(gui.mc, mouseX, mouseY) == true) {
                onBindButtonClicked(widget.infoSupplier()!!)
            } else if (info?.renameButton?.mousePressed(gui.mc, mouseX,mouseY) == true) {
                if(renameBar.info != widget.infoSupplier()!!) {
                    finishRename()
                }
                widget.renderNameTextField = false
                onRenameButtonClicked(widget.infoSupplier()!!, index)
                clickRenameButton = true
            }
        }
        renameBar.mouseClicked(mouseX,mouseY,mouseButton)
        if(!clickRenameButton && renameBar.isVisible) {
            finishRename()
        }
    }

    fun keyTyped(char: Char, key: Int): Boolean {
        if(renameBar.isFocused){
            if(key == Keyboard.KEY_RETURN){
                finishRename()
            }else{
                renameBar.textboxKeyTyped(char, key)
            }
            return true
        }
        return false
    }

    fun render(gui: GuiAdvancedMemoryCard, mouseX: Int, mouseY: Int, partialTicks: Float) {
        for (widget in entries) {
            widget.render(gui, mouseX, mouseY, partialTicks)
        }
        renameBar.drawTextBox()
    }

    fun onGuiClosed() {
        finishRename()
    }
}

