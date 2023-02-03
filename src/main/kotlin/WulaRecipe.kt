package top.e404.wularecipe

import top.e404.eplugin.EPlugin
import top.e404.wularecipe.command.Commands
import top.e404.wularecipe.config.Config
import top.e404.wularecipe.config.Lang
import top.e404.wularecipe.config.RecipeManager
import top.e404.wularecipe.hook.HookManager
import top.e404.wularecipe.menu.MenuManager

class WulaRecipe : EPlugin() {
    override val debugPrefix: String
        get() = langManager.getOrElse("debug_prefix") { "&7[&bWulaRecipeDebug&7]" }
    override val prefix: String
        get() = langManager.getOrElse("prefix") { "&7[&6WulaRecipe&7]" }

    override var debug: Boolean
        get() = Config.debug
        set(value) {
            Config.debug = value
        }
    override val langManager by lazy { Lang }

    override fun onEnable() {
        PL = this
        Lang.load(null)
        Config.load(null)
        RecipeManager.load(null)
        MenuManager.register()
        SummonManager.register()
        Commands.register()
        HookManager.register()
        info("&a加载完成")
    }

    override fun onDisable() {
        SummonManager.close()
        MenuManager.close()
        cancelAllTask()
        info("&a卸载完成")
    }
}

lateinit var PL: WulaRecipe
    private set