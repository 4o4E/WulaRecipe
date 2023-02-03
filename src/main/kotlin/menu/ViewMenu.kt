package top.e404.wularecipe.menu

import org.bukkit.event.inventory.InventoryClickEvent
import top.e404.eplugin.EPlugin.Companion.placeholder
import top.e404.eplugin.menu.Displayable
import top.e404.eplugin.menu.menu.Menu
import top.e404.wularecipe.PL
import top.e404.wularecipe.config.Config

class ViewMenu(
    type: String,
    list: MutableList<Displayable>,
) : Menu(
    plugin = PL,
    row = 6,
    title = Config.config.menu.title.placeholder("type" to type),
    self = false
) {
    val zone: ViewZone
    val prev: PrevButton
    val next: NextButton

    init {
        zone = ViewZone(this, list)
        zones.add(zone)
        prev = PrevButton(this)
        next = NextButton(this)
        slots[48] = prev
        slots[50] = next
    }

    override fun onClick(slot: Int, event: InventoryClickEvent) = true
    override fun onClickBlank(event: InventoryClickEvent) {
        event.isCancelled = true
    }

    override fun onClickSelfInv(event: InventoryClickEvent) {
        event.isCancelled = true
    }
}