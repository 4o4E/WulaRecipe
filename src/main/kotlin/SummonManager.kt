package top.e404.wularecipe

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.event.AdyeshachEntityDamageEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntityInteractEvent
import ltd.icecold.orangeengine.api.data.model.ModelType
import ltd.icecold.orangeengine.api.model.ModelEntity
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitTask
import top.e404.eplugin.listener.EListener
import top.e404.eplugin.util.execAsCommand
import top.e404.eplugin.util.giveStickItem
import top.e404.wularecipe.config.Lang
import top.e404.wularecipe.config.Machine
import top.e404.wularecipe.hook.AdyHook
import top.e404.wularecipe.hook.OeHook
import top.e404.wularecipe.hook.PapiHook

object SummonManager : EListener(PL) {
    val map = mutableMapOf<Player, SummonObject>()

    fun summon(p: Player, machine: Machine) {
        val exists = map.remove(p)
        if (exists != null) {
            exists.fail()
            exists.entityInstance.remove()
        }
        val entityInstance = AdyHook.getPrivateEntityManager(p).create(EntityTypes.SHULKER, p.location)
        val modelEntity = OeHook.setModel(entityInstance.normalizeUniqueId, machine.info.model, ModelType.BLOCKBENCH)
        map[p] = SummonObject(p, entityInstance, modelEntity, machine)
    }

    private fun onTick() {
        map.values.toMutableList().forEach { summon ->
            if (summon.tooFar()) map.remove(summon.player)?.remove()
        }
    }

    override fun register() {
        Bukkit.getPluginManager().registerEvents(this, plugin)
        PL.runTaskTimer(20, 20, ::onTick)
    }

    fun close() {
        map.values.forEach(SummonObject::remove)
    }

    // event handler

    @EventHandler
    fun AdyeshachEntityInteractEvent.onEvent() {
        map[player]?.onRightClick(this)
    }

    @EventHandler
    fun AdyeshachEntityDamageEvent.onEvent() {
        map[player]?.onLeftClick(this)
    }

    @EventHandler
    fun PlayerQuitEvent.onEvent() {
        map.remove(player)?.remove()
    }
}

class SummonObject(
    val player: Player,
    val entityInstance: EntityInstance,
    val modelEntity: ModelEntity,
    val machine: Machine,
) {
    val machineName = machine.info.name
    val items = mutableListOf<ItemStack>()
    var clickTask: BukkitTask? = null
    var animationTask: BukkitTask? = null

    fun onRightClick(event: AdyeshachEntityInteractEvent) {
        if (!event.isMainHand) return
        val item = event.player.inventory.itemInMainHand
        if (item.type.isAir) {
            PL.sendMsgWithPrefix(player, Lang["message.empty_handed"])
            event.isCancelled = true
            return
        }
        PL.debug { "玩家${player.name}向机器${machineName}中放入${item.type}" }
        if (item.amount == 1) {
            items.add(item)
            event.player.inventory.setItemInMainHand(null)
        } else {
            event.player.inventory.setItemInMainHand(item.clone().apply { amount-- })
            items.add(item.apply { amount = 1 })
        }
        scheduleCraft()
    }

    fun onLeftClick(event: AdyeshachEntityDamageEvent) {
        if (items.isEmpty()) {
            PL.sendMsgWithPrefix(event.player, Lang["message.empty_machine", "machine" to machineName])
            return
        }
        val item = items.removeLast()
        PL.debug { "玩家${player.name}从机器${machineName}中取出${item.type}" }
        event.player.giveStickItem(item)
        scheduleCraft()
    }

    fun tooFar() = player.location.distance(entityInstance.getLocation()) > 15

    /**
     * 5秒不点击则尝试合成
     */
    fun scheduleCraft() {
        notice()
        clickTask?.cancel()
        clickTask = PL.runTaskLater(100, ::onCraft)
    }

    fun onCraft() {
        val entry = machine.recipes.entries.firstOrNull { it.value.matches(items) }
        if (entry == null) {
            player.sendMessage(Lang["message.no_such_recipe"])
            fail()
            return
        }
        val (name, recipe) = entry
        val output = try {
            recipe.output.toItemStack()
        } catch (e: Exception) {
            PL.sendMsgWithPrefix(player, Lang["message.craft_fail", "recipe" to name])
            PL.warn("玩家使用${machineName}合成物品, 匹配合成表${name}, 但是合成表${name}获取对应物品失败, 返还材料", e)
            fail()
            return
        }
        PL.debug { "玩家使用${machineName}合成物品, 匹配合成表${name}" }
        modelEntity.playAnimation(machine.info.animation)
        animationTask = PL.runTaskLater(machine.info.animationDuration) {
            modelEntity.playAnimation("idle")
            items.clear()
            player.giveStickItem(output)
            recipe.command.forEach {
                try {
                    PapiHook.placeholder(it, player).execAsCommand()
                } catch (e: Exception) {
                    PL.warn("执行指令${it}时出现异常, 继续执行后续指令", e)
                }
            }
        }
    }

    fun fail() {
        items.forEach(player::giveStickItem)
        items.clear()
    }

    fun notice() = items
        .map { it.display().hoverEvent(it.asHoverEvent()) }
        .let { player.sendMessage(Component.join(machine.configuration, it)) }

    fun remove() {
        animationTask?.let {
            it.cancel()
            animationTask = null
        }
        fail()
        entityInstance.remove()
    }
}