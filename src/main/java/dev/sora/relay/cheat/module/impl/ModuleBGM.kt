package dev.sora.relay.cheat.module.impl

import dev.sora.relay.cheat.module.CheatModule
import dev.sora.relay.game.event.EventTick
import dev.sora.relay.game.utils.NoteBlockUtils
import org.cloudburstmc.protocol.bedrock.data.SoundEvent
import org.cloudburstmc.protocol.bedrock.packet.LevelSoundEventPacket

class ModuleBGM : CheatModule("BGM") {

    private val song by lazy {
		NoteBlockUtils.Song().apply {
			readNbs(ModuleBGM::class.java.getResourceAsStream("/assets/music.nbs"))
			computeMaxTicks()
		}
	}
    private var ticks = 0

    override fun onEnable() {
        ticks = -1
        session.chat("${song.maxTicks / 20} seconds")
    }

	private val handleTick = handle<EventTick> { event ->
		if (ticks + 20 > song.maxTicks) {
			ticks = 0
		}
		val notes = song.ticks[ticks++] ?: return@handle
		notes.forEach { note ->
			val pk = LevelSoundEventPacket().apply {
				sound = SoundEvent.NOTE
				position = event.session.thePlayer.vec3Position
				extraData = note.inst.ordinal shl 8 or note.key.toInt()
				identifier = ":"
				isBabySound = false
				isRelativeVolumeDisabled = false
			}
			event.session.sendPacket(pk)
		}
	}

//    @Listen
//    fun onPacketInbound(event: EventPacketInbound) {
//        if (event.packet is LevelSoundEvent2Packet) {
//            event.cancel()
//        }
//    }
}
