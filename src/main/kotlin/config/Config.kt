package top.e404.wularecipe.config

import kotlinx.serialization.Serializable
import org.bukkit.inventory.ItemStack
import top.e404.eplugin.config.ESerializationConfig
import top.e404.eplugin.config.JarConfig
import top.e404.wularecipe.PL
import top.e404.wularecipe.serialization.ItemStackSerializer

object Config : ESerializationConfig<ConfigData>(
    plugin = PL,
    path = "config.yml",
    default = JarConfig(PL, "config.yml"),
    serializer = ConfigData.serializer(),
    format = defaultYaml
) {
    var debug: Boolean
        get() = config.debug
        set(value) {
            config.debug = value
        }

    val prev: ItemStack
        get() = config.menu.prev.clone()

    val next: ItemStack
        get() = config.menu.next.clone()
}

@Serializable
data class ConfigData(
    var debug: Boolean,
    val prefix: String,
    var menu: Menu,
    val exclude: List<String> = emptyList()
)

@Serializable
data class Menu(
    val title: String,
    @Serializable(ItemStackSerializer::class)
    val prev: ItemStack,
    @Serializable(ItemStackSerializer::class)
    val next: ItemStack,
)
