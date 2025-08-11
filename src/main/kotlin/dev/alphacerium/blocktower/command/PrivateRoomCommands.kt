package dev.alphacerium.blocktower.command

import com.mojang.brigadier.context.CommandContext
import de.maxhenkel.admiral.annotations.Command
import de.maxhenkel.admiral.annotations.Name
import de.maxhenkel.enhancedgroups.EnhancedGroups
import de.maxhenkel.enhancedgroups.command.PersistentGroupCommands
import de.maxhenkel.enhancedgroups.config.PersistentGroup
import de.maxhenkel.voicechat.api.Group
import dev.alphacerium.advancedgroups.AdvancedGroupCommands
import dev.alphacerium.advancedgroups.AdvancedGroupCommandsVoicechatPlugin
import dev.alphacerium.blocktower.Blocktower
import dev.alphacerium.blocktower.BlocktowerVoicechatPlugin
import dev.alphacerium.blocktower.config.PrivateRoom
import net.minecraft.commands.CommandSourceStack
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component

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
        @Name("exit") exit: BlockPos,
        @Name("group") group: String
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
}