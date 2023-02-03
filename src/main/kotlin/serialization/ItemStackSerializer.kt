@file:Suppress("UNUSED")

package top.e404.wularecipe.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import top.e404.eplugin.EPlugin.Companion.formatAsConst
import top.e404.eplugin.util.buildItemStack

@Suppress("DEPRECATION")
object ItemStackSerializer : KSerializer<ItemStack> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("Color") {
            element<String>("type")
            element<String>("name", isOptional = true)
            element<List<String>>("lore", isOptional = true)
            element<Int>("amount", isOptional = true)
            element<String>("custom_model_data", isOptional = true)
        }

    override fun serialize(encoder: Encoder, value: ItemStack) =
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.type.name)
            val im = value.itemMeta ?: return@encodeStructure
            encodeStringElement(descriptor, 1, im.displayName)
            val lore = im.lore
            if (!lore.isNullOrEmpty())
                encodeSerializableElement(descriptor, 2, ListSerializer(String.serializer()), lore)
            encodeIntElement(descriptor, 3, value.amount)
            encodeIntElement(descriptor, 4, im.customModelData)
        }

    @ExperimentalSerializationApi
    override fun deserialize(decoder: Decoder): ItemStack =
        decoder.decodeStructure(descriptor) {
            var type = Material.AIR
            var name: String? = null
            var lore: List<String>? = null
            var amount = 1
            var cmd: Int? = null
            while (true) when (val index = decodeElementIndex(descriptor)) {
                0 -> type = Material.valueOf(decodeStringElement(descriptor, 0).formatAsConst())
                1 -> name = decodeStringElement(descriptor, 1)
                2 -> lore = decodeNullableSerializableElement(descriptor, 2, ListSerializer(String.serializer()).nullable, null)
                3 -> amount = decodeIntElement(descriptor, 3)
                4 -> decodeNullableSerializableElement(descriptor, 4, Int.serializer().nullable, null)?.let { cmd = it }
                CompositeDecoder.DECODE_DONE -> break
                else -> error("Unexpected index: $index")
            }
            buildItemStack(type, amount, name, lore) {
                setCustomModelData(cmd)
            }
        }
}
