package com.loudbook.dev

import dev.hypera.scaffolding.Scaffolding
import dev.hypera.scaffolding.schematic.Schematic
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.PlayerSkin
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.extensions.Extension
import net.minestom.server.extras.bungee.BungeeCordProxy
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.instance.block.Block
import net.minestom.server.timer.TaskSchedule
import net.minestom.server.utils.NamespaceID
import net.minestom.server.world.DimensionType
import java.io.File


class LoudsLobby : Extension() {
    private var fullbright: DimensionType = DimensionType.builder(NamespaceID.from("minestom:full_bright"))
        .ambientLight(2.0f)
        .build()
    private var redis: Redis? = null
    override fun initialize() {
        MinecraftServer.getDimensionTypeManager().addDimension(fullbright)

        val instanceManager = MinecraftServer.getInstanceManager()
        val instanceContainer = instanceManager.createInstanceContainer(fullbright)

        instanceContainer.setGenerator { unit ->
            unit.modifier().fillHeight(0, 1, Block.AIR)
        }

        val globalEventHandler = MinecraftServer.getGlobalEventHandler()
        globalEventHandler.addListener(
            PlayerLoginEvent::class.java
        ) { event: PlayerLoginEvent ->
            event.setSpawningInstance(
                instanceContainer
            )
            event.player.respawnPoint = Pos(0.5, 60.0, 0.5, -90f, 0f)
            event.player.gameMode = GameMode.ADVENTURE
            val skin = PlayerSkin.fromUsername(event.player.username)
            event.player.skin = skin
        }

        initializeLobby(instanceContainer)

        MinecraftServer.setBrandName("Loud's Network")

        val infoManager = InfoManager()

        this.redis = Redis(infoManager)

        MinecraftServer.getCommandManager().register(QueueCommand(infoManager, redis!!))

        BungeeCordProxy.enable()

        this.redis!!.subscribeServerInfo()
    }

    override fun terminate() {
        this.redis!!.client.shutdown()
    }

    private fun initializeLobby(instance: InstanceContainer) {
        val schematic: Schematic = Scaffolding.fromFile(File("./extensions/schematics/lobby.schematic"))!!
        schematic.build(instance, Pos(0.0, 60.0, 0.0))
    }
}