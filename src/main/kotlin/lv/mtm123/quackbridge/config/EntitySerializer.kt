package lv.mtm123.quackbridge.config

import com.google.common.reflect.TypeToken
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer

class EntitySerializer : TypeSerializer<Entity> {

    private val pattern: Regex = Regex("[&@]\\d+")
    private val default: Entity = Entity(Entity.EntityType.USER, 0)

    override fun deserialize(type: TypeToken<*>, value: ConfigurationNode): Entity? {
        val entityString = value.string ?: return default
        if (entityString.matches(pattern)) {
            return when (entityString[0]) {
                '@' -> Entity(Entity.EntityType.USER,
                        entityString.subSequence(1, entityString.length).toString().toLong())
                '&' -> Entity(Entity.EntityType.ROLE,
                        entityString.subSequence(1, entityString.length).toString().toLong())
                else -> throw IllegalArgumentException() //This shouldn't be possible
            }
        }

        return default
    }

    override fun serialize(type: TypeToken<*>, obj: Entity?, value: ConfigurationNode) {
        value.value = obj?.type?.prefix?.toString().plus(obj?.id)
    }

}
