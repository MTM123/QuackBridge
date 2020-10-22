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

    @Setting("entities-allowed-to-execute-cmds")
    val entitiesAllowedToExecuteCmds: List<Entity> = listOf(Entity(Entity.EntityType.ROLE, 751497992149663856),
            Entity(Entity.EntityType.USER, 179983857267769344))

}
