package lv.mtm123.quackbridge.commands

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel

interface Command {

    fun onExecute(channel: TextChannel, issuer: Member, message: Message, args: List<String>)

}
