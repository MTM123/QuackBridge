package lv.mtm123.quackbridge.discord

import lv.mtm123.quackbridge.QuackBridge
import lv.mtm123.quackbridge.commands.CommandManager
import lv.mtm123.quackbridge.config.Config
import net.dv8tion.jda.api.entities.EmbedType
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.spongepowered.api.Server
import org.spongepowered.api.scheduler.Task
import org.spongepowered.api.text.serializer.TextSerializers

class GuildListener(
    private val plugin: QuackBridge,
    private val server: Server?,
    private val config: Config,
    private val commandManager: CommandManager
) : ListenerAdapter() {

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.channel.idLong != this.config.chatChannel
            || event.isWebhookMessage
            || event.jda.selfUser.idLong == event.author.idLong
        ) {
            return
        }

        val msg = createMessage(event.message)

        if (commandManager.isPossibleCommand(msg)) {
            event.member?.let { commandManager.handleCommand(it, event.channel, event.message) }
        } else {
            Task.builder().execute(Runnable {
                val bridgedMsg = this.config.discordChatMessageFormat.replace(
                    "%user%", event.member!!.effectiveName
                ) //Won't be null because we are ignoring webhook messages
                    .replace("%text%", msg)
                server?.broadcastChannel?.send(TextSerializers.FORMATTING_CODE.deserialize(bridgedMsg))
            }).submit(plugin)
        }
    }

    override fun onReady(event: ReadyEvent) {
        event.jda.getTextChannelById(config.chatChannel)?.sendMessage("** Server started **")?.queue()
    }

    private fun asUrl(embed: MessageEmbed): String {
        return when (embed.type) {
            EmbedType.VIDEO -> {
                val videoInfo = embed.videoInfo ?: return ""
                videoInfo.url ?: ""
            }
            EmbedType.IMAGE -> {
                val imageInfo = embed.image ?: return ""
                imageInfo.url ?: imageInfo.proxyUrl ?: ""
            }
            else -> ""
        }
    }

    private fun createMessage(message: Message): String {
        var msg = message.contentStripped
        if (message.embeds.isNotEmpty()) {
            message.embeds.forEach {
                msg = concat(msg, asUrl(it))
            }
        }

        if (message.attachments.isNotEmpty()) {
            message.attachments.forEach {
                msg = concat(msg, it.url)
            }
        }

        return msg
    }

    private fun concat(msg: String, text: String): String {
        var newMessage = msg
        if (msg.isEmpty()) {
            newMessage += "\n"
        }

        newMessage += text
        return newMessage
    }

}
