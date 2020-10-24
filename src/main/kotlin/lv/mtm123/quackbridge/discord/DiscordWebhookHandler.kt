package lv.mtm123.quackbridge.discord

import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.slf4j.Logger
import java.io.IOException

class DiscordWebhookHandler(private val logger: Logger?, private val webhookUrl: String) {

    private val mediaType: MediaType = "application/json".toMediaType()
    private val gson: Gson = Gson()
    private val client: OkHttpClient = OkHttpClient()

    fun postMessage(messageRequest: MessageRequest) {
        val json = gson.toJson(messageRequest)
        val requestBody = RequestBody.create(mediaType, json)

        val req = Request.Builder()
                .url(webhookUrl)
                .addHeader("User-Agent", "duckbot")
                .post(requestBody)
                .build()

        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                logger?.error("Failed to send message!", e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.close()
            }
        })
    }

}
