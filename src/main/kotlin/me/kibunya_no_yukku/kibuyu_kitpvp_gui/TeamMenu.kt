package me.kibunya_no_yukku.kibuyu_kitpvp_gui

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object TeamMenu {
    fun create(): Inventory {
        val inventory = Bukkit.createInventory(null, 9*3, "§cチームを選択")

        // スロット10番目 赤羊毛
        val redwool = ItemStack(Material.RED_WOOL, 1)
        val redwoolmeta = redwool.itemMeta
        redwoolmeta?.setDisplayName("§c赤チームに参加")
        redwool.itemMeta = redwoolmeta
        inventory.setItem(10, redwool)

        // スロット12番目 青羊毛
        val bluewool = ItemStack(Material.BLUE_WOOL, 1)
        val bluewoolMeta = bluewool.itemMeta
        bluewoolMeta?.setDisplayName("§b青チームに参加")
        bluewool.itemMeta = bluewoolMeta
        inventory.setItem(12, bluewool)

        // スロット14番目 黄羊毛
        val yellowwool = ItemStack(Material.YELLOW_WOOL, 1)
        val yellowwoolmeta = yellowwool.itemMeta
        yellowwoolmeta?.setDisplayName("§e黄チームに参加")
        yellowwool.itemMeta = yellowwoolmeta
        inventory.setItem(14, yellowwool)

        // スロット16番目 緑羊毛
        val greenwool = ItemStack(Material.GREEN_WOOL, 1)
        val greenwoolmeta = greenwool.itemMeta
        greenwoolmeta?.setDisplayName("§a緑チームに参加")
        greenwool.itemMeta = greenwoolmeta
        inventory.setItem(16, greenwool)

        // スロット18番目 矢
        val arrow = ItemStack(Material.ARROW, 1)
        val arrowmeta = arrow.itemMeta
        arrowmeta?.setDisplayName("§fメインメニューへ戻る")
        arrow.itemMeta = arrowmeta
        inventory.setItem(18, arrow)

        // スロット26番目 薄灰羊毛
        val graywool = ItemStack(Material.GRAY_WOOL, 1)
        val graywoolmeta = graywool.itemMeta
        graywoolmeta?.setDisplayName("§8チームから離脱する")
        graywoolmeta?.lore = listOf("§7チームに加入していないとゲームに参加できません")
        graywool.itemMeta = graywoolmeta
        inventory.setItem(26, graywool)

        return inventory
    }
}