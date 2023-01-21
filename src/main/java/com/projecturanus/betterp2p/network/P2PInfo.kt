package com.projecturanus.betterp2p.network

import net.minecraftforge.common.util.ForgeDirection

class P2PInfo(val index: Int,
              val frequency: Long,
              val posX: Int,
              val posY: Int,
              val posZ: Int,
              val world: Int,
              val facing: ForgeDirection,
              val name: String,
              val output: Boolean,
              val hasChannel: Boolean)
