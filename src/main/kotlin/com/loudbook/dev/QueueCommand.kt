package com.loudbook.dev

import com.loudbook.dev.api.PlayerSendInfo
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.Player
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit

class QueueCommand(private val infoManager: InfoManager, private val redis: Redis) : Command("queue") {
    init {
        setDefaultExecutor { sender, _ ->
            sender.sendMessage(Component.text("Please provide a game type!", NamedTextColor.RED))
        }

        val gameTypeArg = ArgumentType.String("gameType")

        addSyntax({ sender, context ->
            val gameType = context.get(gameTypeArg)
            val server = infoManager.getServer(gameType)

            if (server == null) {
                sender.sendMessage(Component.text("Could not find a server for that game type!", NamedTextColor.RED))
                return@addSyntax
            }

            val username = (sender as Player).username

            redis.client.getTopic("server-send").publish("$username:$gameType")

            val map = redis.client.getMap<UUID, PlayerSendInfo>("player-send-info")
            map[sender.uuid] = PlayerSendInfo(server.id)
            map.expireAsync(Duration.ofSeconds(7))

            sender.sendMessage(Component.text("Sending you to ${server.id}...", NamedTextColor.GREEN))
        }, gameTypeArg)
    }
}
