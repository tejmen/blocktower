@file:Suppress("unused")

package dev.alphacerium.blocktower.command

import com.mojang.brigadier.context.CommandContext
import de.maxhenkel.admiral.annotations.Command
import de.maxhenkel.admiral.annotations.Name
import de.maxhenkel.enhancedgroups.EnhancedGroups
import de.maxhenkel.enhancedgroups.config.PersistentGroup
import de.maxhenkel.voicechat.api.Group
import dev.alphacerium.advancedgroups.AdvancedGroupCommands
import dev.alphacerium.blocktower.Blocktower
import dev.alphacerium.blocktower.BlocktowerVoicechatPlugin
import dev.alphacerium.blocktower.config.PrivateRoom
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentUtils
import net.minecraft.network.chat.HoverEvent
import java.util.UUID

@Command(PrivateRoomCommands.PRIVATE_ROOM_COMMAND)
class PrivateRoomCommands {
    companion object {
        const val PRIVATE_ROOM_COMMAND = "privateroom"
    }

    @Command("add")
    fun add(
        context: CommandContext<CommandSourceStack>,
        @Name("name") name: String,
        @Name("password") password: String,
        @Name("entrance") entrance: BlockPos,
        @Name("exit") exit: BlockPos
    ): Int {
        if (name.isBlank()) {
            context.source.sendFailure(Component.literal("Name cannot be blank"))
            return 0
        }
        if (AdvancedGroupCommands.PERSISTENT_GROUP_STORE.getGroup(name) == null) {
            // create the group
            val type = Group.Type.NORMAL
            val vcGroup = BlocktowerVoicechatPlugin.SERVER_API.groupBuilder().setPersistent(true).setName(name).setType(type).build()

            val persistentGroup = PersistentGroup(name, password, PersistentGroup.Type.fromGroupType(type), false)
            EnhancedGroups.PERSISTENT_GROUP_STORE.addGroup(persistentGroup)
            EnhancedGroups.PERSISTENT_GROUP_STORE.addCached(vcGroup.id, persistentGroup)
        }

        val persistentGroup = EnhancedGroups.PERSISTENT_GROUP_STORE.getGroup(name)!!
        val privateRoom = PrivateRoom(name, entrance, exit, persistentGroup, null)
        Blocktower.PRIVATE_ROOM_STORE.addPrivateRoom(privateRoom)

        context.source.sendSuccess({ Component.literal("Successfully created persistent group $name") }, false)
        return 1
    }

    @Command("remove")
    fun remove(
        context: CommandContext<CommandSourceStack>,
        @Name("name") name: String
    ): Int {
        val privateRoom = Blocktower.PRIVATE_ROOM_STORE.getPrivateRoom(name)
        if (privateRoom != null) {
            Blocktower.PRIVATE_ROOM_STORE.removePrivateRoom(privateRoom)
            context.source.sendSuccess({ Component.literal("Successfully removed private room $name") }, false)
            return 1
        } else {
            context.source.sendFailure(Component.literal("Private room $name not found"))
            return 0
        }
    }

    @Command("remove")
    fun remove(
        context: CommandContext<CommandSourceStack>,
        @Name("id") id: UUID
    ): Int {
        val privateRoom = Blocktower.PRIVATE_ROOM_STORE.getPrivateRoom(id)
        if (privateRoom != null) {
            Blocktower.PRIVATE_ROOM_STORE.removePrivateRoom(privateRoom)
            context.source.sendSuccess({ Component.literal("Successfully removed private room $id") }, false)
            return 1
        } else {
            context.source.sendFailure(Component.literal("Private room $id not found"))
            return 0
        }
    }

    @Command("list")
    fun list(context: CommandContext<CommandSourceStack>): Int {
        val privateRooms = Blocktower.PRIVATE_ROOM_STORE.getPrivateRooms()
        if (privateRooms.isEmpty()) {
            context.source.sendSuccess({ Component.literal("No private rooms found") }, false)
        }
        for (privateRoom in privateRooms) {
            val output = Component.literal(privateRoom.name)
                .append(" ")
                .append(ComponentUtils.wrapInSquareBrackets(Component.literal("Remove"))
                    .withStyle{
                        it.withClickEvent(ClickEvent.RunCommand("/" + PRIVATE_ROOM_COMMAND + " remove " + privateRoom.id))
                            .withHoverEvent(HoverEvent.ShowText(Component.literal("click to delete private room")))
                            .applyFormat(ChatFormatting.GREEN)
                    })
                .append(" ")
                .append(ComponentUtils.wrapInSquareBrackets(Component.literal("Copy ID"))
                    .withStyle {
                        it.withClickEvent(ClickEvent.CopyToClipboard(privateRoom.id.toString()))
                            .withHoverEvent(HoverEvent.ShowText(Component.literal("click to copy private room id")))
                            .applyFormat(ChatFormatting.GREEN)
                    }
                )
            context.source.sendSuccess({ output }, false)
        }
        return 1
    }
}