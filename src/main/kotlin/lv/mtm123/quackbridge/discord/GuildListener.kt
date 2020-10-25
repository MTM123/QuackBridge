package lv.mtm123.quackbridge.discord

import lv.mtm123.quackbridge.QuackBridge
import lv.mtm123.quackbridge.commands.CommandManager
import lv.mtm123.quackbridge.config.Config
import org.javacord.api.event.message.MessageCreateEvent
import org.javacord.api.listener.message.MessageCreateListener
import org.spongepowered.api.Server
import org.spongepowered.api.scheduler.Task
import org.spongepowered.api.text.serializer.TextSerializers

class GuildListener(private val plugin: QuackBridge, private val server: Server?, private val config: Config, private val commandManager: CommandManager) : MessageCreateListener {

    override fun onMessageCreate(event: MessageCreateEvent) {
        if (event.channel.id != this.config.chatChannel
                || !event.messageAuthor.isRegularUser
                || !event.messageAuthor.isUser
                || !event.isServerMessage) {
            return
        }

        if (!event.messageAuthor.asUser().isPresent) {
            return
        }

        val user = event.messageAuthor.asUser().get()
        val msg = event.message.readableContent
        if (commandManager.isPossibleCommand(msg)) {
            commandManager.handleCommand(user, event.channel, event.message)
        } else {
            Task.builder().execute(Runnable {
                val bridgedMsg = this.config.discordChatMessageFormat
                        .replace("%user%", user.getDisplayName(event.server.get())) //Won't be null because we are ignoring webhook messages
                        .replace("%text%", msg)
                server?.broadcastChannel?.send(TextSerializers.FORMATTING_CODE.deserialize(bridgedMsg))
            }).submit(plugin)
        }
    }

}
