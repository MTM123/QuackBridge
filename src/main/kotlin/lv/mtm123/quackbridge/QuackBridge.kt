package lv.mtm123.quackbridge

import com.google.common.reflect.TypeToken
import com.google.inject.Inject
import lv.mtm123.quackbridge.commands.CommandCmd
import lv.mtm123.quackbridge.commands.CommandManager
import lv.mtm123.quackbridge.commands.CommandOnline
import lv.mtm123.quackbridge.config.Config
import lv.mtm123.quackbridge.config.Entity
import lv.mtm123.quackbridge.config.EntitySerializer
import lv.mtm123.quackbridge.discord.DiscordWebhookHandler
import lv.mtm123.quackbridge.discord.GuildListener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import ninja.leaping.configurate.ConfigurationOptions
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection
import org.slf4j.Logger
import org.spongepowered.api.Game
import org.spongepowered.api.Sponge
import org.spongepowered.api.config.DefaultConfig
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.state.GameStartedServerEvent
import org.spongepowered.api.event.game.state.GameStoppedServerEvent
import org.spongepowered.api.plugin.Plugin
import java.io.File
import java.io.IOException

@Plugin(id = "quackbridge")
class QuackBridge {

    @Inject
    private lateinit var logger: Logger

    @Inject
    @DefaultConfig(sharedRoot = false)
    private lateinit var configManager: ConfigurationLoader<CommentedConfigurationNode>

    @Inject
    @DefaultConfig(sharedRoot = false)
    private lateinit var defaultConfigFile: File

    @Inject
    private lateinit var game: Game

    private lateinit var config: Config
    private lateinit var jda: JDA

    @Listener
    fun onServerStart(event: GameStartedServerEvent?) {

        val typeToken = TypeToken.of(Config::class.java)
        val serializers = TypeSerializerCollection.defaults().newChild()
        serializers.register(TypeToken.of(Entity::class.java), EntitySerializer())
        val options = ConfigurationOptions.defaults().withSerializers(serializers)

        val default = Config()
        if (!defaultConfigFile.exists()) {
            try {
                this.configManager.save(this.configManager.createEmptyNode(options).setValue(typeToken, default))
            } catch (e: IOException) {
                this.logger.error("Failed to save the config", e)
                return
            }
        }

        try {
            this.config = this.configManager.load(options).getValue(typeToken, default)
        } catch (e: Exception) {
            this.logger.error("Failed to load the config - using default", e)
            return
        }

        val commandManager = CommandManager(config.commandPrefix)
        commandManager.register("online", CommandOnline(this, game))
        commandManager.register("cmd", CommandCmd(this, config.entitiesAllowedToExecuteCmds))

        this.jda = JDABuilder.createDefault(config.botToken)
            .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS)
            .addEventListeners(GuildListener(this, game.server, this.config, commandManager))
            .build()

        val discordWebhookHandler = DiscordWebhookHandler(logger, config.discordWebhookUrl)

        Sponge.getEventManager().registerListeners(this, Listener(jda, discordWebhookHandler, config))
    }

    @Listener
    fun onServerStop(event: GameStoppedServerEvent) {
        this.jda.getTextChannelById(config.chatChannel)?.sendMessage("** Server stopped **")?.queue()
    }

}


