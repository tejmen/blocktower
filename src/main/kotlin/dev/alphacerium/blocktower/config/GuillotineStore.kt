package dev.alphacerium.blocktower.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dev.alphacerium.blocktower.Blocktower
import net.minecraft.core.BlockPos
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class GuillotineStore(private val file: File) {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private var position: BlockPos? = null

    init {
        load()
    }

    fun load() {
        if (!file.exists()) {
            Blocktower.LOGGER.error("Guillotine position file not found")
            return // don't make a file if we don't have to
        }
        try {
            FileReader(file).use { reader ->
                val blockPosType = object : TypeToken<BlockPos>() {}.type
                position = gson.fromJson(reader, blockPosType)
            }
        } catch (e: Exception) {
            Blocktower.LOGGER.error("Failed to parse Guillotine position file", e)
        }
        save()
    }

    fun save() {
        file.parentFile.mkdirs()
        try {
            FileWriter(file).use { writer ->
                gson.toJson(position, writer)
            }
        } catch (e: Exception) {
            Blocktower.LOGGER.error("Failed to save guillotine position", e)
        }
    }

    fun setGuillotinePosition(pos: BlockPos) {
        position = pos
        save()
    }

    fun getGuillotinePosition(): BlockPos? {
        return position
    }

    fun removeGuillotinePosition() {
        position = null
        save()
    }
}