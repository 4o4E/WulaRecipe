package top.e404.wularecipe.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import top.e404.eplugin.EPlugin.Companion.color
import top.e404.eplugin.command.ECommand
import top.e404.eplugin.util.asMutableList
import top.e404.wularecipe.PL
import top.e404.wularecipe.config.Lang
import top.e404.wularecipe.config.Recipe
import top.e404.wularecipe.config.RecipeManager
import top.e404.wularecipe.menu.MenuManager
import top.e404.wularecipe.menu.ViewMenu

object Open : ECommand(
    PL,
    "open",
    "(?i)open|o",
    false,
    "wularecipe.admin"
) {
    override val usage: String
        get() = Lang["command.usage.open"].color()

    override fun onCommand(sender: CommandSender, args: Array<out String>) {
        if (args.size != 3) {
            plugin.sendMsgWithPrefix(sender, usage)
            return
        }
        val (_, type, player) = args
        val p = Bukkit.getPlayer(player)
        if (p == null) {
            plugin.sendMsgWithPrefix(sender, Lang["message.invalid_player"])
            return
        }

        val machine = RecipeManager[type]
        if (machine == null) {
            plugin.sendMsgWithPrefix(sender, Lang["message.invalid_machine", "machine" to type])
            return
        }
        val list = machine.recipes.filter { (id, _) ->
            p.hasPermission("wularecipe.use.$id")
        }
        if (list.isEmpty()) {
            plugin.sendMsgWithPrefix(sender, Lang["command.open.empty", "type" to type, "player" to player])
            return
        }
        MenuManager.openMenu(ViewMenu(machine.info.name, list.map(Map.Entry<String, Recipe>::value).asMutableList()), p)
    }

    override fun onTabComplete(
        sender: CommandSender,
        args: Array<out String>,
        complete: MutableList<String>,
    ) {
        when (args.size) {
            2 -> complete.addAll(RecipeManager.data.keys)
            3 -> complete.addOnlinePlayers()
        }
    }
}
