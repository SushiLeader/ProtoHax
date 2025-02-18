package dev.sora.relay.cheat.module.impl

import dev.sora.relay.cheat.module.CheatModule
import dev.sora.relay.game.event.EventTick
import org.cloudburstmc.math.vector.Vector3f
import org.cloudburstmc.protocol.bedrock.data.PlayerAuthInputData
import org.cloudburstmc.protocol.bedrock.packet.SetEntityMotionPacket

class ModuleAirJump : CheatModule("AirJump") {

	private var speedMultiplierValue by floatValue("SpeedMultiplier", 1f, 0.5f..3f)

	private var jumpPressed = false

	override fun onDisable() {
		jumpPressed = false
	}

	private val onTick = handle<EventTick> {
		if (it.session.thePlayer.inputData.contains(PlayerAuthInputData.JUMP_DOWN)) {
			if (!jumpPressed) {
				val player = it.session.thePlayer
				if (!player.onGround && !player.prevOnGround) {
					it.session.netSession.inboundPacket(SetEntityMotionPacket().apply {
						runtimeEntityId = player.runtimeEntityId
						motion = Vector3f.from((player.posX - player.prevPosX) * speedMultiplierValue, 0.42f, (player.posZ - player.prevPosZ) * speedMultiplierValue)
					})
				}
			}
			jumpPressed = true
		} else {
			jumpPressed = false
		}
	}
}
