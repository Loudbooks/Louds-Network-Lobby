package com.loudbook.dev.api

import java.util.*

data class ServerInfo(val id: UUID, val gameType: String, val maxPlayers: Int, val currentPlayers: Int, val spotsOpen: Int, val shuttingDown: Boolean)
