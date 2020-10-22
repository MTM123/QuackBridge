package lv.mtm123.quackbridge.config

data class Entity(val type: EntityType, val id: Long) {

    enum class EntityType(val prefix: Char) {
        USER('@'),
        ROLE('&');
    }

}


