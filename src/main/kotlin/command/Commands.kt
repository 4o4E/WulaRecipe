package top.e404.wularecipe.command

import top.e404.eplugin.command.ECommandManager
import top.e404.wularecipe.PL

object Commands : ECommandManager(
    PL,
    "wularecipe",
    Debug,
    Reload,
    Open,
    Summon
)