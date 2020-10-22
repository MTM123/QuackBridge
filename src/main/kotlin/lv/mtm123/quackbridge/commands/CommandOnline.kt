package lv.mtm123.quackbridge.commands

import lv.mtm123.quackbridge.QuackBridge
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import org.spongepowered.api.Game
import org.spongepowered.api.scheduler.Task

class CommandOnline(private val plugin: QuackBridge, private val game: Game?) : Command {

    override fun onExecute(channel: TextChannel, issuer: Member, message: Message, args: List<String>) {
        Task.builder().execute(Runnable {
            val msg = game?.server?.onlinePlayers?.joinToString(", ")
            channel.sendMessage(msg.toString()).queue()
        }).submit(plugin)
    }

}
