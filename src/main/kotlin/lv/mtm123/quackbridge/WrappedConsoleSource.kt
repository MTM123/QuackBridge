package lv.mtm123.quackbridge

import org.spongepowered.api.command.source.ConsoleSource
import org.spongepowered.api.text.Text

class WrappedConsoleSource(val source: ConsoleSource) : ConsoleSource by source {

    private val receivedMessages = mutableListOf<String>()

    override fun sendMessage(message: Text) {
        receivedMessages.add(message.toPlain())
    }

    fun collectMessages(): String {
        val msg = receivedMessages.joinToString("\n")
        receivedMessages.clear()
        return msg
    }

    override fun hasPermission(permission: String): Boolean {
        return true
    }

}