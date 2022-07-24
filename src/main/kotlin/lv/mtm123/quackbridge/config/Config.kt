package lv.mtm123.quackbridge.config

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

@ConfigSerializable
class Config {

    @Setting("bot-token")
    val botToken: String = "token"

    @Setting("command-prefix")
    val commandPrefix: String = "!"

    @Setting("chat-channel-id")
    val chatChannel: Long = 752136288143933513

    @Setting("avatar-api-url")
    val avatarApiUrl: String = "https://mc-heads.net/avatar"

    @Setting("discord-webhook-url")
    val discordWebhookUrl: String =  "https://example.com"

    @Setting("discord-message-prefix")
    val messagePrefix = "&6[Discord]"

    @Setting("discord-chat-message-format")
    val discordChatMessageFormat: String = "%prefix% &a%user%: &7%text%"

    @Setting("discord-reply-format")
    val replyFormat = "%prefix% &a%user% &7replied to %target%: %text%"

    @Setting("discord-reply-hover-format")
    val replyHoverFormat = "&a%target% &7said: &7%text%"

    @Setting("entities-allowed-to-execute-cmds")
    val entitiesAllowedToExecuteCmds: List<Entity> = listOf(Entity(Entity.EntityType.ROLE, 751497992149663856),
            Entity(Entity.EntityType.USER, 179983857267769344))

}
