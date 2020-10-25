package lv.mtm123.quackbridge.commands

import lv.mtm123.quackbridge.QuackBridge
import lv.mtm123.quackbridge.config.Entity
import org.javacord.api.entity.channel.TextChannel
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.server.Server
import org.javacord.api.entity.user.User
import org.spongepowered.api.Sponge
import org.spongepowered.api.scheduler.Task

class CommandCmd(private val plugin: QuackBridge, private val entities: List<Entity>) : Command {

    override fun onExecute(channel: TextChannel, issuer: User, message: Message, args: List<String>) {

        if (!canExecuteCommand(message.server.get(), issuer)) {
            channel.sendMessage("** You don't have permission to use this command! **")
            return
        }

        if (args.isEmpty()) {
            channel.sendMessage("** You need to specify a command to execute! **")
            return
        }

        Task.builder().execute(Runnable {
            Sponge.getCommandManager().process(Sponge.getServer().console, args.joinToString(" "))
            channel.sendMessage("** Command executed **")
        }).submit(plugin)

    }

    private fun canExecuteCommand(server: Server, issuer: User): Boolean {
        return entities.stream().anyMatch { e ->
            return@anyMatch when (e.type) {
                Entity.EntityType.USER -> issuer.id == e.id
                Entity.EntityType.ROLE -> issuer.getRoles(server).stream().anyMatch { r -> r.id == e.id }
            }
        }
    }

}
