package lv.mtm123.quackbridge

import lv.mtm123.quackbridge.config.Config
import lv.mtm123.quackbridge.discord.DiscordWebhookHandler
import lv.mtm123.quackbridge.discord.MessageRequest
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.Order
import org.spongepowered.api.event.advancement.AdvancementEvent
import org.spongepowered.api.event.entity.DestructEntityEvent
import org.spongepowered.api.event.filter.cause.First
import org.spongepowered.api.event.message.MessageChannelEvent
import org.spongepowered.api.event.network.ClientConnectionEvent

class Listener(private val jda: JDA, private val webhookHandler: DiscordWebhookHandler, private val config: Config) {

    @Listener(order = Order.POST)
    fun onChat(event: MessageChannelEvent.Chat, @First player: Player) {
        val msg = MessageRequest("${config.avatarApiUrl}/${player.uniqueId}",
                event.rawMessage.toPlain(), player.name)
        webhookHandler.postMessage(msg)
    }

    @Listener(order = Order.POST)
    fun onAdvancement(event: AdvancementEvent.Grant, @First player: Player) {
        if (event.advancement.name.startsWith("recipes")) return
        val embed = EmbedBuilder().setAuthor("${player.name} got a new advancement:")
        embed.setTitle(event.advancement.name)
        event.advancement.displayInfo.get().let { di ->
            embed.setDescription(di.description.toPlain())
        }

        jda.getTextChannelById(config.chatChannel)?.sendMessage(embed.build())?.queue()
    }

    @Listener(order = Order.POST)
    fun onDeath(event: DestructEntityEvent.Death) {
        if (event.targetEntity !is Player) return

        val p = event.targetEntity as Player
        val msg = event.message.toPlain()
        if (msg.isEmpty()) return

        jda.getTextChannelById(config.chatChannel)?.sendMessage("**:skull_crossbones: $msg**")?.queue()
    }

    @Listener(order = Order.POST)
    fun onJoin(event: ClientConnectionEvent.Join, @First player: Player) {
        jda.getTextChannelById(config.chatChannel)?.sendMessage("** ${player.name} joined the server **")?.queue()
    }

    @Listener
    fun onQuit(event: ClientConnectionEvent.Disconnect, @First player: Player) {
        jda.getTextChannelById(config.chatChannel)?.sendMessage("** ${player.name} left the server **")?.queue()
    }
}
