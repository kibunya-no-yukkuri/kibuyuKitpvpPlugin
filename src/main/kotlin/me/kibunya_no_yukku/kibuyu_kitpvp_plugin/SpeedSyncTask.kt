package me.kibunya_no_yukku.kibuyu_kitpvp_plugin


import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

fun setSpeed(player: Player, speedScore: Double) {
    // Minecraft 内部速度: 0.2 がデフォルト
    val baseSpeed = 0.2f
    val multiplier = 1.0 + speedScore / 100.0   // 10%増なら 1.1
    val newSpeed = (baseSpeed * multiplier).toFloat().coerceIn(0f, 1f)
    player.walkSpeed = newSpeed
}

// タスクでスコアボードに応じて自動更新
class SpeedSyncTask(private val plugin: JavaPlugin) : BukkitRunnable() {
    override fun run() {
        val scoreboard = plugin.server.scoreboardManager?.mainScoreboard ?: return
        val speedObj = scoreboard.getObjective("speed") ?: return

        for (player in plugin.server.onlinePlayers) {
            val score = speedObj.getScore(player.name).score.toDouble()
            setSpeed(player, score)
        }
    }
}

