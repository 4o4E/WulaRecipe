package top.e404.wularecipe.hook

import top.e404.eplugin.hook.EHookManager
import top.e404.eplugin.hook.bentobox.ItemsAdderHook
import top.e404.eplugin.hook.mmoitems.MmoitemsHook
import top.e404.eplugin.hook.modelengine.ModelEngineHook
import top.e404.eplugin.hook.placeholderapi.PlaceholderAPIHook
import top.e404.wularecipe.PL

object HookManager : EHookManager(PL, MiHook, IaHook, MegHook, PapiHook) {
    private fun readResolve(): Any = HookManager
}

object MiHook : MmoitemsHook(PL)
object IaHook : ItemsAdderHook(PL)
object MegHook : ModelEngineHook(PL)
object PapiHook : PlaceholderAPIHook(PL)