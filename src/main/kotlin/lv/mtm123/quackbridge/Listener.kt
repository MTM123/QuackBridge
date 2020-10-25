package lv.mtm123.quackbridge

import lv.mtm123.quackbridge.config.Config
import lv.mtm123.quackbridge.discord.DiscordWebhookHandler
import lv.mtm123.quackbridge.discord.MessageRequest
import org.javacord.api.DiscordApi
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.Order
import org.spongepowered.api.event.advancement.AdvancementEvent
import org.spongepowered.api.event.entity.DestructEntityEvent
import org.spongepowered.api.event.filter.cause.First
import org.spongepowered.api.event.message.MessageChannelEvent
import org.spongepowered.api.event.network.ClientConnectionEvent
import org.spongepowered.api.util.SpongeApiTranslationHelper

class Listener(private val api: DiscordApi, private val webhookHandler: DiscordWebhookHandler, private val config: Config) {

    @Listener(order = Order.POST)
    fun onChat(event: MessageChannelEvent.Chat, @First player: Player) {
        val msg = MessageRequest("${config.avatarApiUrl}/${player.uniqueId}",
                event.rawMessage.toPlain(), player.name)
        webhookHandler.postMessage(msg)
    }

    @Listener(order = Order.POST)
    fun onAdvancement(event: AdvancementEvent.Grant, @First player: Player) {
        if (event.advancement.name.startsWith("recipes")) return
        val embed = EmbedBuilder()
        embed.setAuthor("${player.name} got a new advancement:")
        embed.setTitle(event.advancement.name)
        event.advancement.displayInfo.get().let { di ->
            embed.setDescription(SpongeApiTranslationHelper.t(di.description.toPlain()).toPlain())
        }

        sendEmbedToChatChannel(embed)
    }

    @Listener(order = Order.POST)
    fun onDeath(event: DestructEntityEvent.Death) {
        if (event.targetEntity !is Player) return

        val p = event.targetEntity as Player
        val msg = event.message.toPlain()
        if (msg.isEmpty()) return

        sendMessageToChatChannel("**:skull_crossbones: $msg**")
    }

    @Listener(order = Order.POST)
    fun onJoin(event: ClientConnectionEvent.Join, @First player: Player) {
        sendMessageToChatChannel("** ${player.name} joined the server **")
    }

    @Listener
    fun onQuit(event: ClientConnectionEvent.Disconnect, @First player: Player) {
        sendMessageToChatChannel("** ${player.name} left the server **")
    }

    private fun sendMessageToChatChannel(message: String) {
        this.api.getChannelById(this.config.chatChannel).ifPresent { ch ->
            ch.asTextChannel().ifPresent { chtxt ->
                chtxt.sendMessage(message)
            }
        }
    }

    private fun sendEmbedToChatChannel(embedBuilder: EmbedBuilder) {
        this.api.getChannelById(this.config.chatChannel).ifPresent { ch ->
            ch.asTextChannel().ifPresent { chtxt ->
                chtxt.sendMessage(embedBuilder)
            }
        }
    }
}
