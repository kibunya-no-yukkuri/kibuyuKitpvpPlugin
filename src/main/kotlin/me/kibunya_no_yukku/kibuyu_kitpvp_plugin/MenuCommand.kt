package me.kibunya_no_yukku.kibuyu_kitpvp_plugin

import me.kibunya_no_yukku.kibuyu_kitpvp_gui.*
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

object MenuCommand: CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            return false
        }
        // 引数なし → ヘルプを表示
        if (args.isEmpty()) {
            // MainMenu を開くだけ
            val inventory = MainMenu.create()
            sender.openInventory(inventory)
            Kibuyu_kitpvp_plugin.guiMap[sender.uniqueId] = "main"
            return true
        }
        when (args[0].lowercase()) {
            "tp" -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tp ${sender.name} 0 -60 0")
                sender.closeInventory()
            }
            "team" -> {
                val inventory = TeamMenu.create()
                sender.openInventory(inventory)
                Kibuyu_kitpvp_plugin.guiMap[sender.uniqueId] = "team"
            }
            "kit" -> {
                val inventory = KitMenu.create()
                sender.openInventory(inventory)
                Kibuyu_kitpvp_plugin.guiMap[sender.uniqueId] = "kit"
            }
            "kit2" -> {
                val inventory = Kit2Menu.create()
                sender.openInventory(inventory)
                Kibuyu_kitpvp_plugin.guiMap[sender.uniqueId] = "kit"
            }
            "map" -> {
                val inventory = MapMenu.create()
                sender.openInventory(inventory)
                Kibuyu_kitpvp_plugin.guiMap[sender.uniqueId] = "map"
            }
            "mode" -> {
                val inventory = ModeMenu.create()
                sender.openInventory(inventory)
                Kibuyu_kitpvp_plugin.guiMap[sender.uniqueId] = "mode"
            }
        }

        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String>? {
        return when (args.size) {
            1 -> {
                // 1個目の引数で候補を出す
                listOf("tp", "team","kit","map","mode","kit2")
                    .filter { it.startsWith(args[0], ignoreCase = true) }
                    .toMutableList()
            }

            else -> mutableListOf()
        }
    }
}