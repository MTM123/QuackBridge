package lv.mtm123.quackbridge.commands

import org.javacord.api.entity.channel.TextChannel
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.user.User

interface Command {

    fun onExecute(channel: TextChannel, issuer: User, message: Message, args: List<String>)

}
