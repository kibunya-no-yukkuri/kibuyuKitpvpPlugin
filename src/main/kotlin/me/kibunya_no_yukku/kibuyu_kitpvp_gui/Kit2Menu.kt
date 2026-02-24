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

        // スロット11番目
        val barrier = ItemStack(Material.BARRIER, 1)
        val barrierMeta = barrier.itemMeta
        barrierMeta?.setDisplayName("§b??")
        barrierMeta?.lore = listOf("§7右クリックで詳細を確認")
        barrier.itemMeta = barrierMeta
        inventory.setItem(11, barrier)

        // スロット12番目
        val barrier2 = ItemStack(Material.BARRIER, 1)
        val barrierMeta2 = barrier2.itemMeta
        barrierMeta2?.setDisplayName("§b??")
        barrierMeta2?.lore = listOf("§7右クリックで詳細を確認")
        barrier2.itemMeta = barrierMeta2
        inventory.setItem(12, barrier2)

        // スロット13番目
        val barrier3 = ItemStack(Material.BARRIER, 1)
        val barrierMeta3 = barrier3.itemMeta
        barrierMeta3?.setDisplayName("§b??")
        barrierMeta3?.lore = listOf("§7右クリックで詳細を確認")
        barrier3.itemMeta = barrierMeta3
        inventory.setItem(13, barrier3)

        // スロット14番目
        val barrier4 = ItemStack(Material.BARRIER, 1)
        val barrierMeta4 = barrier4.itemMeta
        barrierMeta4?.setDisplayName("§b??")
        barrierMeta4?.lore = listOf("§7右クリックで詳細を確認")
        barrier4.itemMeta = barrierMeta4
        inventory.setItem(14, barrier4)

        // スロット15番目
        val barrier5 = ItemStack(Material.BARRIER, 1)
        val barrierMeta5 = barrier5.itemMeta
        barrierMeta5?.setDisplayName("§b??")
        barrierMeta5?.lore = listOf("§7右クリックで詳細を確認")
        barrier5.itemMeta = barrierMeta5
        inventory.setItem(15, barrier5)

        // スロット16番目
        val barrier6 = ItemStack(Material.BARRIER, 1)
        val barrierMeta6 = barrier6.itemMeta
        barrierMeta6?.setDisplayName("§b??")
        barrierMeta6?.lore = listOf("§7右クリックで詳細を確認")
        barrier6.itemMeta = barrierMeta6
        inventory.setItem(16, barrier6)

        // スロット18番目 矢
        val arrow = ItemStack(Material.ARROW, 1)
        val arrowmeta = arrow.itemMeta
        arrowmeta?.setDisplayName("§fメインメニューへ戻る")
        arrow.itemMeta = arrowmeta
        inventory.setItem(18, arrow)

        return inventory
    }
}