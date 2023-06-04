package top.e404.wularecipe.config

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.decodeFromString
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import top.e404.eplugin.EPlugin.Companion.color
import top.e404.eplugin.config.serialization.MaterialSerializer
import top.e404.eplugin.hook.mmoitems.equals
import top.e404.eplugin.hook.mmoitems.id
import top.e404.eplugin.menu.Displayable
import top.e404.eplugin.util.editItemMeta
import top.e404.wularecipe.PL
import top.e404.wularecipe.display
import top.e404.wularecipe.hook.IaHook
import top.e404.wularecipe.hook.MiHook
import top.e404.eplugin.util.matches as trMatches

object RecipeManager {
    private val dir = PL.dataFolder.resolve("data")
    var data = mutableMapOf<String, Machine>()
        private set

    private val yaml = Yaml(configuration = YamlConfiguration(strictMode = false, polymorphismStyle = PolymorphismStyle.Property))

    fun load(sender: CommandSender? = null) {
        if (dir.isFile) dir.delete()
        if (!dir.exists()) {
            dir.mkdirs()
            PL.getResource("example.yml")!!.use {
                dir.resolve("example.yml").writeBytes(it.readBytes())
            }
        }
        val d = mutableMapOf<String, Machine>()
        dir.listFiles()?.forEach {
            if (it.isDirectory) return@forEach
            try {
                val recipeData = yaml.decodeFromString<Machine>(it.readText())
                d[it.name.substringBeforeLast(".")] = recipeData
            } catch (e: Exception) {
                PL.sendAndWarn(sender, "加载文件${it.absolutePath}时出现异常, 此文件内容将被跳过, 请检查文件格式", e)
            }
        }
        data = d
    }

    operator fun get(machineType: String) = data[machineType]
}

@Serializable
data class Machine(
    @SerialName("machine")
    val info: MachineInfo,
    val recipes: Map<String, Recipe>,
) {
    val configuration by lazy {
        JoinConfiguration.builder()
            .prefix(Component.text("&6${info.name}&f中已有以下物品: &7[".color()))
            .separator(Component.text("&7, ".color()))
            .suffix(Component.text("&7], &a等待5秒则开始合成".color()))
            .build()
    }
}

@Serializable
data class MachineInfo(
    val name: String,
    val model: String,
    val animation: String,
    @SerialName("animation_duration")
    val animationDuration: Long,
)

@Serializable
data class Recipe(
    val command: List<String>,
    @SerialName("in")
    val input: List<Item>,
    @SerialName("out")
    val output: Item,
) : Displayable {
    private val formattedInput by lazy {
        input.flatMap { i ->
            if (i.amount <= 1) arrayListOf(i)
            else ArrayList<Item>(i.amount).apply { repeat(i.amount) { add(i) } }
        }
    }

    fun matches(items: List<ItemStack>) = trMatches(formattedInput, items) { item, itemStack ->
        item.match(itemStack)
    }

    override val item: ItemStack
        get() = output.toItemStack().editItemMeta {
            val prefix = Config.config.prefix
            val lore = mutableListOf<Component>()
            lore.add(Component.text("${prefix}&f合成方式: ".color()))
            input.forEach {
                val component = it.toItemStack().display().append(Component.text("&fx&b${it.amount}".color()))
                lore.add(Component.text(prefix).append(component))
            }
            lore.add(Component.text(""))
            lore()?.let { lore.addAll(it) }
            lore(lore)
        }

    @Transient
    override var needUpdate = false
    override fun update() {}
}

@Serializable
sealed interface Item {
    val amount: Int
    fun toItemStack(): ItemStack
    fun match(item: ItemStack): Boolean
}

@Serializable
@SerialName("mc")
data class McItem(
    @Serializable(MaterialSerializer::class)
    val id: Material,
    override val amount: Int = 1,
) : Item {
    override fun toItemStack() = ItemStack(id).also { it.amount = amount }
    override fun match(item: ItemStack) = if (IaHook.getIaItemInfo(item) != null
        || MiHook.getNbtItem(item).id.isNotBlank()
    ) false else item.type == id
}

@Serializable
@SerialName("mi")
data class MiItem(
    val category: String,
    val id: String,
    override val amount: Int = 1,
) : Item {
    override fun toItemStack() = MiHook.getItem(category, id)?.also { it.amount = amount } ?: throw UnknownMmoItemsItem(category, id)
    override fun match(item: ItemStack) = MiHook.getNbtItem(item).equals(category, id)
}

class UnknownMmoItemsItem(type: String, id: String) : RuntimeException("无法找到类别为${type}, id为${id}的MmoItems物品")

@Serializable
@SerialName("ia")
data class IaItem(
    val namespaceId: String,
    override val amount: Int = 1,
) : Item {
    override fun toItemStack() = IaHook.getIaItem(namespaceId)?.itemStack?.also { it.amount = amount } ?: throw UnknownItemsAdderItem(namespaceId)
    override fun match(item: ItemStack) = IaHook.getIaItemInfo(item)?.namespacedID == namespaceId
}

class UnknownItemsAdderItem(namespaceId: String) : RuntimeException("无法找到id为${namespaceId}的ItemsAdder物品")
