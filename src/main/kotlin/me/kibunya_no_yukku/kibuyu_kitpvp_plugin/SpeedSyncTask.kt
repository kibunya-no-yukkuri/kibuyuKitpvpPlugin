package me.kibunya_no_yukku.kibuyu_kitpvp_plugin


import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

fun setSpeed(player: Player, speedScore: Double, speedDebuffScore: Double) {
    // Minecraft 内部速度: 0.2 がデフォルト
    val baseSpeed = 0.2f

    val newSpeedDebuff = if (speedDebuffScore > 0) {
        speedDebuffScore / (speedDebuffScore + 100.0)
    } else 0.0

    val speed = 1.0 + (speedScore / 100.0)  // 10%増なら 1.1
    val multiplier = (speed * (1 - newSpeedDebuff))

    val newSpeed = (baseSpeed * multiplier).toFloat().coerceIn(0f, 1f)
    player.walkSpeed = newSpeed
}

// タスクでスコアボードに応じて自動更新
class SpeedSyncTask(private val plugin: JavaPlugin) : BukkitRunnable() {
    override fun run() {
        val scoreboard = plugin.server.scoreboardManager.mainScoreboard
        val speedObj = scoreboard.getObjective("speed") ?: return
        val speedDebuffObj = scoreboard.getObjective("speed_debuff") ?: return

        for (player in plugin.server.onlinePlayers) {
            val score = speedObj.getScore(player.name).score.toDouble()
            val debuffScore = speedDebuffObj.getScore(player.name).score.toDouble()
            setSpeed(player, score, debuffScore)
        }
    }
}

