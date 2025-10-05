package me.kibunya_no_yukku.kibuyu_kitpvp_gui

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object ModeMenu {
    fun create(): Inventory {
        val inventory = Bukkit.createInventory(null, 9*3, "§aMODEを選択")

        // スロット13番目 コンパス
        val compass = ItemStack(Material.COMPASS, 1)
        val compassmeta = compass.itemMeta
        compassmeta?.setDisplayName("§d未完成ing")
        compassmeta?.lore = listOf("§k多分いつかできる")
        compass.itemMeta = compassmeta
        inventory.setItem(13, compass)

        // スロット18番目 矢
        val arrow = ItemStack(Material.ARROW, 1)
        val arrowmeta = arrow.itemMeta
        arrowmeta?.setDisplayName("§fメインメニューへ戻る")
        arrow.itemMeta = arrowmeta
        inventory.setItem(18, arrow)

        return inventory
    }
}