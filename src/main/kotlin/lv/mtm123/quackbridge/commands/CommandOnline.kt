package lv.mtm123.quackbridge.commands

import lv.mtm123.quackbridge.QuackBridge
import org.javacord.api.entity.channel.TextChannel
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.user.User
import org.spongepowered.api.Game
import org.spongepowered.api.scheduler.Task
import java.util.stream.Collectors

class CommandOnline(private val plugin: QuackBridge, private val game: Game?) : Command {

    override fun onExecute(channel: TextChannel, issuer: User, message: Message, args: List<String>) {
        Task.builder().execute(Runnable {
            val msg = game?.server?.onlinePlayers?.stream()?.map { p -> p.name }
                    ?.collect(Collectors.toSet())?.joinToString(", ")
            channel.sendMessage("** Players online: **\n $msg")
        }).submit(plugin)
    }

}
