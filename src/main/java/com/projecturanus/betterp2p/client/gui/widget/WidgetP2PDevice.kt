package com.projecturanus.betterp2p.client.gui.widget

import appeng.me.GridNode
import appeng.parts.p2p.PartP2PTunnelME
import appeng.tile.networking.TileCableBus
import com.projecturanus.betterp2p.client.gui.GuiAdvancedMemoryCard
import com.projecturanus.betterp2p.client.gui.InfoWrapper
import com.projecturanus.betterp2p.item.BetterMemoryCardModes
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.resources.I18n
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.common.util.ForgeDirection
import java.awt.Color
import kotlin.reflect.KProperty0

const val P2P_ENTRY_HEIGHT: Int = 41
const val P2P_ENTRY_WIDTH: Int = 254

class WidgetP2PDevice(private val selectedInfoProperty: KProperty0<InfoWrapper?>, val modeSupplier: () -> BetterMemoryCardModes, val infoSupplier: () -> InfoWrapper?, var x: Int, var y: Int): Widget() {
    private val outputColor = 0x4566ccff
    private val selectedColor = 0x4545DA75
    private val errorColor = 0x45DA4527
    private val inactiveColor = 0x45FFEA05

    var renderNameTextField = true

    private val selectedInfo: InfoWrapper?
        get() = selectedInfoProperty.get()

    fun render(gui: GuiAdvancedMemoryCard, mouseX: Int, mouseY: Int, partialTicks: Float) {
        val info = infoSupplier()
        if (info != null) {
            val fontRenderer = gui.mc.fontRenderer
            if (selectedInfo?.code == info.code)
                GuiScreen.drawRect(x, y, x + P2P_ENTRY_WIDTH, y + P2P_ENTRY_HEIGHT, selectedColor)
            else if (info.error) {
                // P2P output without an input
                GuiScreen.drawRect(x, y, x + P2P_ENTRY_WIDTH, y + P2P_ENTRY_HEIGHT, errorColor)
            }
            else if (!info.hasChannel && info.frequency != 0.toLong()) {
                // No channel
                GuiScreen.drawRect(x, y, x + P2P_ENTRY_WIDTH, y + P2P_ENTRY_HEIGHT, inactiveColor)
            }
            else if (selectedInfo?.frequency == info.frequency && info.frequency != 0.toLong()) {
                // Show same frequency
                GuiScreen.drawRect(x, y, x + P2P_ENTRY_WIDTH, y + P2P_ENTRY_HEIGHT, outputColor)
            }
            fontRenderer.drawString(info.description, x + 24, y + 3, 0)
            fontRenderer.drawString(I18n.format("gui.advanced_memory_card.pos", info.posX, info.posY, info.posZ, info.facing.name), x + 24, y + 12, 0)
            if(renderNameTextField) {
                fontRenderer.drawString(I18n.format("gui.advanced_memory_card.name", info.name), x + 24, y + 21, 0)
            } else {
                fontRenderer.drawString(I18n.format("gui.advanced_memory_card.name", ""), x + 24, y + 21, 0)
            }
            fontRenderer.drawString(getExtraInfo(info.dim, info.posX, info.posY, info.posZ, info.facing), x + 24, y + 30, 0)

            if (selectedInfo == null) {
                info.bindButton.enabled = false
                info.selectButton.enabled = true
            } else if (info.code != selectedInfo?.code) {
                info.bindButton.enabled = true
                info.selectButton.enabled = true
            } else {
                // TODO Unbind
                info.bindButton.enabled = false
                info.selectButton.enabled = false
            }

            val mode = modeSupplier()
            if (mode == BetterMemoryCardModes.COPY && !info.output && info.frequency != 0.toLong()) {
                info.bindButton.enabled = false
            }
            drawButtons(gui, info, mouseX, mouseY, partialTicks)
            drawP2PColors(gui, info.frequency, x, y)
        }
    }

    private fun drawP2PColors(gui: GuiScreen, frequency: Long, x: Int, y: Int) {
        drawRectBorder(x + 9, y + 9, 3, 3, Color.BLACK.rgb)
        for (row in 0..1) {
            for (col in 0..1) {
                for (colorIndex in 0..3) {
                    val offsetX: Int = colorIndex % 2
                    val offsetY: Int = colorIndex / 2
                    drawHorizontalLine(x + 7 + col * 6 + offsetX, x + 8 + col * 6 + offsetX, y + 7 + row * 6 + offsetY, Color.BLACK.rgb)
                }
                drawRectBorder(x + 6 + col * 6, y + 6 + row * 6, 3, 3, Color.BLACK.rgb)
            }
        }
    }

    private fun drawButtons(gui: GuiScreen, info: InfoWrapper, mouseX: Int, mouseY: Int, partialTicks: Float) {
        info.renameButton.xPosition = x + 50
        info.renameButton.width = 120
        info.renameButton.yPosition = y + 19
        info.renameButton.height = 12
        if (!info.bindButton.enabled && info.selectButton.enabled) {
            info.bindButton.enabled = false
            info.selectButton.enabled = true

            info.selectButton.xPosition = x + 215
            info.selectButton.width = 32
            info.selectButton.yPosition = y + 6
            info.selectButton.drawButton(gui.mc, mouseX, mouseY)
        } else if (info.bindButton.enabled && info.selectButton.enabled) {
            info.bindButton.enabled = true
            info.selectButton.enabled = true

            // Select button on the left
            info.selectButton.xPosition = x + 178
            info.selectButton.width = 32
            info.selectButton.yPosition = y + 6
            info.selectButton.drawButton(gui.mc, mouseX, mouseY)

            // Bind button on the right
            info.bindButton.xPosition = x + 215
            info.bindButton.width = 32
            info.bindButton.yPosition = y + 6
            info.bindButton.drawButton(gui.mc, mouseX, mouseY)
        } else if (!info.selectButton.enabled && !info.bindButton.enabled) {
            // TODO Unbind
            info.bindButton.enabled = false
            info.selectButton.enabled = false
        }
    }

    // For a future feature ;P

//    private fun drawIcon(gui: GuiAdvancedMemoryCard, posX: Int, posY: Int, posZ: Int, face: ForgeDirection, world: Int) {
//        val tile = DimensionManager.getWorld(world)?.getTileEntity(posX, posY, posZ)
//        if (tile is TileCableBus) {
//            val p2p = (tile.getPart(face) as? PartP2PTunnel<*>) ?: return
//            val icon: IIcon = p2p.getItemStack().getIconIndex() ?: return
//            val tessellator = Tessellator.instance
//            gui.bindTexture(MODID, "textures/gui/advanced_memory_card.png")
//            gui.mc.renderEngine.bindTexture(gui.mc.renderEngine.getResourceLocation(0))
//            GL11.glPushAttrib(GL11.GL_BLEND or GL11.GL_TEXTURE_2D or GL11.GL_COLOR)
//            GL11.glEnable(GL11.GL_BLEND)
//            GL11.glEnable(GL11.GL_TEXTURE_2D)
//            GL11.glColor3f(255f, 255f, 255f)
//            OpenGlHelper.glBlendFunc(770, 771, 1, 0)
//            tessellator.startDrawingQuads()
//            tessellator.addVertexWithUV(x.toDouble(), y + rowHeight.toDouble(), 0.0, icon.minU.toDouble(), icon.maxV.toDouble())
//            tessellator.addVertexWithUV(x + rowWidth.toDouble(), y + rowHeight.toDouble(), 0.0, icon.maxU.toDouble(), icon.maxV.toDouble())
//            tessellator.addVertexWithUV(x + rowWidth.toDouble(), y.toDouble(), 0.0, icon.maxU.toDouble(), icon.minV.toDouble())
//            tessellator.addVertexWithUV(x.toDouble(), y.toDouble(), 0.0, icon.minU.toDouble(), icon.minV.toDouble())
//            tessellator.draw()
//            GL11.glPopAttrib()
//        }
//    }
}

fun getExtraInfo(world: Int, posX: Int, poxY: Int, posZ: Int, face: ForgeDirection): String {
    val tile = DimensionManager.getWorld(world)?.getTileEntity(posX, poxY, posZ)
    if (tile is TileCableBus) {
        val used = ((tile.getPart(face) as? PartP2PTunnelME)?.externalFacingNode as? GridNode)?.usedChannels()
        if (used != null) {
            return I18n.format("gui.advanced_memory_card.extra.channel", used)
        }
    }
    return I18n.format("gui.advanced_memory_card.extra.none")
}
