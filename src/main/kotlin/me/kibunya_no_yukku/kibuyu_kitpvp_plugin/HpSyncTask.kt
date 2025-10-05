package me.kibunya_no_yukku.kibuyu_kitpvp_plugin

import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

class HpSyncTask(
    private val plugin: JavaPlugin,
    private val listener: EventListener
) : BukkitRunnable() {

    override fun run() {
        val scoreboard = Bukkit.getScoreboardManager()?.mainScoreboard ?: return
        val hpObj = scoreboard.getObjective("hp") ?: return
        val maxHpObj = scoreboard.getObjective("max_hp") ?: return

        val toRemove = mutableListOf<Player>()

        for (player in listener.syncNeeded) {
            if (!player.isOnline) {
                toRemove.add(player)
                continue
            }

            val hpScore = hpObj.getScore(player.name).score
            val maxHpScore = maxHpObj.getScore(player.name).score.coerceAtLeast(1)

            val attr = player.getAttribute(Attribute.MAX_HEALTH) ?: continue

            // 最大HPを反映
            if (attr.baseValue.toInt() != maxHpScore) {
                attr.baseValue = maxHpScore.toDouble()
            }

            // HPを反映
            val clampedHp = hpScore.coerceIn(0, maxHpScore)
            if (player.health.toInt() != clampedHp) {
                player.health = clampedHp.toDouble()
            }

            // 同期完了したのでフラグ解除
            toRemove.add(player)
        }

        toRemove.forEach { listener.unmarkSync(it) }
    }
}
