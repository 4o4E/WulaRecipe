package top.e404.wularecipe.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import top.e404.eplugin.EPlugin.Companion.color
import top.e404.eplugin.command.ECommand
import top.e404.wularecipe.PL
import top.e404.wularecipe.SummonManager
import top.e404.wularecipe.config.Lang
import top.e404.wularecipe.config.RecipeManager

object Summon : ECommand(
    PL,
    "summon",
    "(?i)summon",
    false,
    "wularecipe.admin"
) {
    override val usage: String
        get() = Lang["command.usage.summon"].color()

    override fun onCommand(sender: CommandSender, args: Array<out String>) {
        if (args.size != 3) {
            plugin.sendMsgWithPrefix(sender, Open.usage)
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
        SummonManager.summon(p, machine)
        plugin.sendMsgWithPrefix(sender, Lang["command.summon_done"])
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