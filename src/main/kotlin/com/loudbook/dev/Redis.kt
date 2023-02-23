package com.loudbook.dev

import com.loudbook.dev.api.ServerInfo
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import java.io.FileInputStream
import java.io.IOException
import java.util.*

class Redis(private val infoManager: InfoManager) {
    val client: RedissonClient
    private var uri: String? = null

    init {
        try {
            FileInputStream("./extensions/config.properties").use { input ->
                val prop = Properties()
                prop.load(input)
                this.uri = prop.getProperty("uri")
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

        val config = Config()
        config.useSingleServer()
            .address = uri
        this.client = Redisson.create(config)
    }

    fun subscribeServerInfo() {
        this.client.getTopic("server-info").addListener(ServerInfo::class.java) { _, msg ->
            if (msg.shuttingDown) {
                infoManager.removeServerInfo(msg)
            } else {
                infoManager.addServerInfo(msg)
            }
        }
    }
}