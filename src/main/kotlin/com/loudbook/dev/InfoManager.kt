package com.loudbook.dev

import com.loudbook.dev.api.ServerInfo

class InfoManager {
    private val serverInfo = mutableListOf<ServerInfo>()

    fun addServerInfo(serverInfo: ServerInfo) {
        for (info in this.serverInfo) {
            if (info.id == serverInfo.id) {
                this.serverInfo.remove(info)
                break
            }
        }
        this.serverInfo.add(serverInfo)
    }

    fun removeServerInfo(serverInfo: ServerInfo) {
        for (info in this.serverInfo) {
            if (info.id == serverInfo.id) {
                println("Removed server info: $info")
                this.serverInfo.remove(info)
                return
            }
        }
    }

    fun getServer(gameType: String): ServerInfo? {
        for (info in this.serverInfo) {
            if (info.gameType.equals(gameType, true)) return info
        }
        return null
    }
}