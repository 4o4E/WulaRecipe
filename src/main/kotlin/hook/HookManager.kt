package top.e404.wularecipe.hook

import top.e404.eplugin.hook.EHookManager
import top.e404.eplugin.hook.ady.AdyeshachHook
import top.e404.eplugin.hook.bentobox.ItemsAdderHook
import top.e404.eplugin.hook.mmoitems.MmoitemsHook
import top.e404.eplugin.hook.oe.OrangeEngineHook
import top.e404.eplugin.hook.placeholderapi.PlaceholderAPIHook
import top.e404.wularecipe.PL

object HookManager : EHookManager(PL, MiHook, IaHook, AdyHook, OeHook, PapiHook)
object MiHook : MmoitemsHook(PL)
object IaHook : ItemsAdderHook(PL)
object AdyHook : AdyeshachHook(PL)
object OeHook : OrangeEngineHook(PL)
object PapiHook : PlaceholderAPIHook(PL)