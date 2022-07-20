package lv.mtm123.quackbridge.discord

import lv.mtm123.quackbridge.QuackBridge
import lv.mtm123.quackbridge.commands.CommandManager
import lv.mtm123.quackbridge.config.Config
import net.dv8tion.jda.api.entities.EmbedType
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.MessageType
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
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

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (!event.isFromGuild) return

        if (event.channel.idLong != this.config.chatChannel
            || event.isWebhookMessage
            || event.jda.selfUser.idLong == event.author.idLong
        ) {
            return
        }

        val msg = createMessage(event.message)

        if (commandManager.isPossibleCommand(msg)) {
            event.member?.let { commandManager.handleCommand(it, event.channel.asTextChannel(), event.message) }
            return
        }

        //Won't be null because we are ignoring webhook messages
        val user = event.member!!.effectiveName

        if (event.message.type == MessageType.INLINE_REPLY) {
            val ref = event.message.messageReference ?: return
            ref.resolve().mapToResult().queue { res ->
                if (res.isFailure) {
                    sendSyncMessage(
                        config.discordChatMessageFormat
                            .replace("%user%", user)
                            .replace("%target%", "unknown")
                            .replace("%text%", msg)
                            .replace("%prefix%", config.messagePrefix)
                    )
                } else {
                    event.guild.retrieveMemberById(res.get().author.idLong).mapToResult().queue {
                        val name = if (it.isFailure) {
                            res.get().author.name
                        } else {
                            it.get().effectiveName
                        }

                        sendSyncMessage(
                            config.replyFormat
                                .replace("%user%", user)
                                .replace("%target%", name)
                                .replace("%text%", msg)
                                .replace("%prefix%", config.messagePrefix)
                        )
                    }

                }
            }

        } else {
            sendSyncMessage(
                config.discordChatMessageFormat.replace("%text%", msg)
                    .replace("%user%", user)
                    .replace("%prefix%", config.messagePrefix)
            )
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

    private fun sendSyncMessage(msg: String) {
        Task.builder().execute(Runnable {
            val channel = server?.broadcastChannel ?: return@Runnable
            channel.send(TextSerializers.FORMATTING_CODE.deserialize(msg))
        }).submit(plugin)
    }
}