package lv.mtm123.quackbridge.commands

import lv.mtm123.quackbridge.QuackBridge
import lv.mtm123.quackbridge.config.Entity
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import org.spongepowered.api.Sponge
import org.spongepowered.api.scheduler.Task

class CommandCmd(private val plugin: QuackBridge, private val entities: List<Entity>) : Command {

    override fun onExecute(channel: TextChannel, issuer: Member, message: Message, args: List<String>) {

        if (!canExecuteCommand(issuer)) {
            channel.sendMessage("** You don't have permission to use this command! **").queue()
            return
        }

        if (args.isEmpty()) {
            channel.sendMessage("** You need to specify a command to execute! **").queue()
            return
        }

        Task.builder().execute(Runnable {
            Sponge.getCommandManager().process(Sponge.getServer().console, args.joinToString(" "))
            channel.sendMessage("** Command executed **").queue()
        }).submit(plugin)

    }

    private fun canExecuteCommand(issuer: Member): Boolean {
        return entities.stream().anyMatch { e ->
            return@anyMatch when (e.type) {
                Entity.EntityType.USER -> issuer.idLong == e.id
                Entity.EntityType.ROLE -> issuer.roles.stream().anyMatch { r -> r.idLong == e.id }
            }
        }
    }

}
