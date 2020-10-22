package lv.mtm123.quackbridge.discord

import lv.mtm123.quackbridge.QuackBridge
import lv.mtm123.quackbridge.commands.CommandManager
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.spongepowered.api.Server
import org.spongepowered.api.scheduler.Task
import org.spongepowered.api.text.Text

class GuildListener(private val plugin: QuackBridge, private val server: Server?, private val commandManager: CommandManager) : ListenerAdapter() {

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        val msg = event.message.contentRaw
        if (commandManager.isPossibleCommand(msg)) {
            event.member?.let { commandManager.handleCommand(it, event.channel, event.message) }
        } else {
            Task.builder().execute(Runnable {
                server?.broadcastChannel?.send(Text.of(msg))
            }).submit(plugin)
        }
    }

}
