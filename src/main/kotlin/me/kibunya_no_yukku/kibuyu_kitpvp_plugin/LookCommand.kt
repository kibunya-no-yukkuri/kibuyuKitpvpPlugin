package me.kibunya_no_yukku.kibuyu_kitpvp_plugin


import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object LookCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("このコマンドはプレイヤー専用です")
            return true
        }

        val player = sender

        if (args.size != 2) {
            player.sendMessage("使い方: /look <yaw> <pitch>")
            return false
        }

        val yaw = args[0].toFloatOrNull()
        val pitch = args[1].toFloatOrNull()

        if (yaw == null || pitch == null) {
            player.sendMessage("yaw と pitch は数字で指定してください")
            return false
        }

        // 視点だけを変更（位置は変わらない）
        player.setRotation(yaw, pitch)

        player.sendMessage("視点を変更しました: yaw=$yaw, pitch=$pitch")
        return true
    }
}