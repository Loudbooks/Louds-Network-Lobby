package com.loudbook.dev

import com.loudbook.dev.api.ServerInfo

class InfoManager {
    private val serverInfo = mutableListOf<ServerInfo>()

    fun addServerInfo(serverInfo: ServerInfo) {
        for (info in this.serverInfo) {
            if (info.id == serverInfo.id) return
            this.serverInfo.add(serverInfo)
        }
    }

    fun removeServerInfo(serverInfo: ServerInfo) {
        for (info in this.serverInfo) {
            if (info.id == serverInfo.id) {
                this.serverInfo.remove(info)
                return
            }
        }
    }
}