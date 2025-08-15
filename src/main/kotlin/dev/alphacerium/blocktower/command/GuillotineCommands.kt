package dev.alphacerium.blocktower.command

import com.mojang.brigadier.context.CommandContext
import de.maxhenkel.admiral.annotations.Command
import de.maxhenkel.admiral.annotations.Name
import dev.alphacerium.blocktower.Blocktower
import net.minecraft.commands.CommandSourceStack
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component

@Command(GuillotineCommands.GUILLOTINE_COMMAND)
class GuillotineCommands {
    companion object {
        const val GUILLOTINE_COMMAND = "guillotine"
    }

    @Command("set")
    fun set(
        context: CommandContext<CommandSourceStack>,
        @Name("position") position: BlockPos
    ): Int {
        Blocktower.GUILLOTINE_STORE.setGuillotinePosition(position)
        return 1
    }

    @Command("list")
    fun list(
        context: CommandContext<CommandSourceStack>
    ): Int {
        val position = Blocktower.GUILLOTINE_STORE.getGuillotinePosition()
        if (position == null) {
            context.source.sendSuccess({ Component.literal(("No guillotine position set")) }, false)
            return 1
        }

        context.source.sendSuccess(
            { Component.literal("Guillotine postion: ${position.x}, ${position.y}, ${position.z}") },
            false
        )
        return 1
    }

    @Command("remove")
    fun remove(
        context: CommandContext<CommandSourceStack>
    ): Int {
        Blocktower.GUILLOTINE_STORE.removeGuillotinePosition()
        context.source.sendSuccess({ Component.literal(("Removed guillotine position")) }, false)
        return 1
    }
}