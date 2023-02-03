package top.e404.wularecipe

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.inventory.ItemStack

fun ItemStack.display() = (itemMeta.displayName() ?: Component.translatable(translationKey()))
    .style(Style.style(TextColor.color(0xFFAA00)).decoration(TextDecoration.ITALIC, false))