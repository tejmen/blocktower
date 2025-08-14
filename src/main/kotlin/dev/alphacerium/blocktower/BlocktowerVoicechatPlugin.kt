package dev.alphacerium.blocktower

import de.maxhenkel.voicechat.api.VoicechatPlugin
import de.maxhenkel.voicechat.api.VoicechatServerApi
import de.maxhenkel.voicechat.api.events.EventRegistration
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent

class BlocktowerVoicechatPlugin : VoicechatPlugin {
    companion object {
        final lateinit var SERVER_API: VoicechatServerApi
    }
    override fun getPluginId(): String {
        return Blocktower.MOD_ID
    }

    override fun registerEvents(registration: EventRegistration) {
        registration.registerEvent<VoicechatServerStartedEvent>(VoicechatServerStartedEvent::class.java) { event ->
            SERVER_API = event.voicechat
        }
    }
}