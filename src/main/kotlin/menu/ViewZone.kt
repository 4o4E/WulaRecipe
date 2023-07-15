package top.e404.wularecipe.menu

import org.bukkit.event.inventory.InventoryClickEvent
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
    override fun onClick(menuIndex: Int, zoneIndex: Int, itemIndex: Int, event: InventoryClickEvent) = true
}
