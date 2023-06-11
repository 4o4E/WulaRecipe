package top.e404.wularecipe.menu

import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import top.e404.eplugin.menu.Displayable
import top.e404.eplugin.menu.menu.ChestMenu
import top.e404.eplugin.menu.zone.MenuButtonZone

class ViewZone(
    menu: ChestMenu,
    list: MutableList<Displayable>,
) : MenuButtonZone<Displayable>(
    menu = menu,
    x = 1,
    y = 1,
    w = 7,
    h = 4,
    data = list
) {
    override val inv = menu.inv
    override fun onHotbarAction(target: ItemStack?, hotbarItem: ItemStack?, slot: Int, hotbar: Int, event: InventoryClickEvent) = true
    override fun onClick(slot: Int, event: InventoryClickEvent) = true
    override fun onPickup(clicked: ItemStack, slot: Int, event: InventoryClickEvent) = true
    override fun onPutin(cursor: ItemStack, slot: Int, event: InventoryClickEvent) = true
    override fun onSwitch(clicked: ItemStack, cursor: ItemStack, slot: Int, event: InventoryClickEvent) = true
}
