package top.e404.wularecipe.menu

import org.bukkit.event.inventory.InventoryClickEvent
import top.e404.eplugin.menu.slot.MenuButton
import top.e404.eplugin.util.emptyItem
import top.e404.wularecipe.config.Config

class PrevButton(viewMenu: ViewMenu) : MenuButton(viewMenu) {
    val zone = viewMenu.zone

    override var item = Config.prev.clone()

    override fun onClick(
        slot: Int,
        event: InventoryClickEvent,
    ): Boolean {
        if (zone.hasPrev) {
            zone.prevPage()
            menu.updateIcon()
        }
        return true
    }

    override fun updateItem() {
        if (!zone.hasPrev) item = emptyItem
        else {
            if (item.type.isAir) item = Config.prev.clone()
            item.amount = (zone.page - 1).coerceIn(1, item.type.maxStackSize)
        }
    }


}

class NextButton(viewMenu: ViewMenu) : MenuButton(viewMenu) {
    val zone = viewMenu.zone

    override var item = Config.next.clone()

    override fun onClick(
        slot: Int,
        event: InventoryClickEvent,
    ): Boolean {
        if (zone.hasNext) {
            zone.nextPage()
            menu.updateIcon()
        }
        return true
    }

    override fun updateItem() {
        if (!zone.hasNext) item = emptyItem
        else {
            if (item.type.isAir) item = Config.next.clone()
            item.amount = (zone.page + 1).coerceIn(1, item.type.maxStackSize)
        }
    }
}