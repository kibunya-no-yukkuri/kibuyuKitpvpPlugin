package me.kibunya_no_yukku.kibuyu_kitpvp_gui

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object Kit2Menu {
    fun create(): Inventory {
        val inventory = Bukkit.createInventory(null, 9*3, "§bSPECIAL KITを選択")

        // スロット10番目 ひまわり
        val sunFlower = ItemStack(Material.SUNFLOWER, 1)
        val sunFlowerMeta = sunFlower.itemMeta
        sunFlowerMeta?.setDisplayName("§6マリー")
        sunFlowerMeta?.lore = listOf("§7右クリックで詳細を確認")
        sunFlower.itemMeta = sunFlowerMeta
        inventory.setItem(10, sunFlower)

        // スロット18番目 矢
        val arrow = ItemStack(Material.ARROW, 1)
        val arrowmeta = arrow.itemMeta
        arrowmeta?.setDisplayName("§fメインメニューへ戻る")
        arrow.itemMeta = arrowmeta
        inventory.setItem(18, arrow)

        return inventory
    }
}