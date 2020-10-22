package lv.mtm123.quackbridge.commands

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel

class CommandManager(private val prefix: String) {

    private val commands: MutableMap<String, Command> = HashMap()

    fun register(name: String, command: Command) {
        commands[name] = command
    }

    fun handleCommand(issuer: Member, channel: TextChannel, message: Message) {
        val commandComponents = message.contentRaw.split(" ")
        if (!commands.containsKey(commandComponents[0])) {
            return
        }

        val cmd = commands[commandComponents[0]]
        val args = commandComponents.subList(1, commandComponents.size)

        cmd?.onExecute(channel, issuer, message, args)
    }

    fun isPossibleCommand(commandText: String): Boolean {
        return commandText.startsWith(prefix)
    }
}
