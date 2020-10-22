package lv.mtm123.quackbridge.discord

import com.google.gson.annotations.SerializedName

class MessageRequest {

    private val content: String
    private val username: String

    @SerializedName("avatar_url")
    private val avatarUrl: String

    constructor(avatarUrl: String, content: String, username: String) {
        this.avatarUrl = avatarUrl
        this.content = content
        this.username = username
    }
}
