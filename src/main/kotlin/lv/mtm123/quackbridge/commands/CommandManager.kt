package lv.mtm123.quackbridge.commands

import org.javacord.api.entity.channel.TextChannel
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.user.User

class CommandManager(private val prefix: String) {

    private val commands: MutableMap<String, Command> = HashMap()

    fun register(name: String, command: Command) {
        commands[name] = command
    }

    fun handleCommand(issuer: User, channel: TextChannel, message: Message) {
        val commandComponents = message.readableContent.split(" ")
        val cmdLabel = commandComponents[0].substring(1)
        if (!commands.containsKey(cmdLabel)) {
            return
        }

        val cmd = commands[cmdLabel]
        val args = commandComponents.subList(1, commandComponents.size)

        cmd?.onExecute(channel, issuer, message, args)
    }

    fun isPossibleCommand(commandText: String): Boolean {
        return commandText.startsWith(prefix)
    }
}
