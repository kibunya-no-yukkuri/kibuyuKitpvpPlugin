package me.kibunya_no_yukku.kibuyu_kitpvp_plugin

import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class HpSyncTask(
    private val listener: EventListener
) : BukkitRunnable() {

    override fun run() {
        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val hpObj = scoreboard.getObjective("hp") ?: return
        val maxHpObj = scoreboard.getObjective("max_hp") ?: return

        val toRemove = mutableListOf<Player>()

        for (player in listener.syncNeeded) {
            if (!player.isOnline) {
                toRemove.add(player)
                continue
            }

            val hpScore = hpObj.getScore(player.name)
            val maxHpScore = maxHpObj.getScore(player.name).score.coerceAtLeast(1)

            val attr = player.getAttribute(Attribute.MAX_HEALTH) ?: continue

            // 最大HPを反映
            if (attr.baseValue.toInt() != maxHpScore) {
                attr.baseValue = maxHpScore.toDouble()
            }

            //HPスコアが実際のHPと違う場合実際のHPにスコアのHPを代入する.
            val clampedHp = hpScore.score.coerceIn(0, maxHpScore)
            if (player.health.toInt() != clampedHp) {
                player.health = clampedHp.toDouble()
            }

            // HPスコアがMAX HPスコアを超えた場合HPスコアをMAX HPスコアと同じ値にする.
            if (hpScore.score > maxHpScore){
                hpScore.score = maxHpScore
            }

            // 同期完了したのでフラグ解除
            toRemove.add(player)
        }

        toRemove.forEach { listener.unmarkSync(it) }
    }
}
