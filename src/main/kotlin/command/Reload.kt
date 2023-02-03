package top.e404.wularecipe.command

import org.bukkit.command.CommandSender
import top.e404.eplugin.EPlugin.Companion.color
import top.e404.eplugin.command.ECommand
import top.e404.wularecipe.PL
import top.e404.wularecipe.SummonManager
import top.e404.wularecipe.config.Config
import top.e404.wularecipe.config.Lang
import top.e404.wularecipe.config.RecipeManager
import top.e404.wularecipe.menu.MenuManager

object Reload : ECommand(
    PL,
    "reload",
    "(?i)reload|r",
    false,
    "wularecipe.admin"
) {
    override val usage: String
        get() = Lang["command.usage.reload"].color()

    override fun onCommand(sender: CommandSender, args: Array<out String>) {
        MenuManager.close()
        SummonManager.close()
        plugin.runTaskAsync {
            Config.load(null)
            Lang.load(null)
            RecipeManager.load(null)
            plugin.sendMsgWithPrefix(sender, Lang["command.reload_done"])
        }
    }
}