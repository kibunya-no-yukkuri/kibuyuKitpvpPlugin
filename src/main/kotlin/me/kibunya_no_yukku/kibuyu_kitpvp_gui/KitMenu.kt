package me.kibunya_no_yukku.kibuyu_kitpvp_gui

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object KitMenu {
    fun create(): Inventory {
        val inventory = Bukkit.createInventory(null, 9*3, "§bSTRIKER KITを選択")

        // スロット10番目 ケーキ
        val cake = ItemStack(Material.CAKE, 1)
        val cakemeta = cake.itemMeta
        cakemeta?.setDisplayName("§dセリナ")
        cakemeta?.lore = listOf("§7右クリックで詳細を確認")
        cake.itemMeta = cakemeta
        inventory.setItem(10, cake)

        // スロット18番目 矢
        val arrow = ItemStack(Material.ARROW, 1)
        val arrowmeta = arrow.itemMeta
        arrowmeta?.setDisplayName("§fメインメニューへ戻る")
        arrow.itemMeta = arrowmeta
        inventory.setItem(18, arrow)

        return inventory
    }
}