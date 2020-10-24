package lv.mtm123.quackbridge.discord

import lv.mtm123.quackbridge.QuackBridge
import lv.mtm123.quackbridge.commands.CommandManager
import lv.mtm123.quackbridge.config.Config
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.spongepowered.api.Server
import org.spongepowered.api.scheduler.Task
import org.spongepowered.api.text.serializer.TextSerializers

class GuildListener(private val plugin: QuackBridge, private val server: Server?, private val config: Config, private val commandManager: CommandManager) : ListenerAdapter() {

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.channel.idLong != this.config.chatChannel
                || event.isWebhookMessage
                || event.jda.selfUser.idLong == event.author.idLong) {
            return
        }

        val msg = event.message.contentStripped
        if (commandManager.isPossibleCommand(msg)) {
            event.member?.let { commandManager.handleCommand(it, event.channel, event.message) }
        } else {
            Task.builder().execute(Runnable {
                val bridgedMsg = this.config.discordChatMessageFormat
                        .replace("%user%", event.member!!.effectiveName) //Won't be null because we are ignoring webhook messages
                        .replace("%text%", msg)
                server?.broadcastChannel?.send(TextSerializers.FORMATTING_CODE.deserialize(bridgedMsg))
            }).submit(plugin)
        }
    }

}
