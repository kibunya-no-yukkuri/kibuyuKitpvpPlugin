package me.kibunya_no_yukku.kibuyu_kitpvp_gui

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

object MainMenu {
    fun create(): Inventory {
        val inventory = Bukkit.createInventory(null, 9*3, "§bメインメニュー")

        // スロット22番目 コンパス
        val compass = ItemStack(Material.COMPASS, 1)
        val compassmeta = compass.itemMeta
        compassmeta?.setDisplayName("§fロビーへテレポート")
        compassmeta?.lore = listOf("§7ロビーへテレポートします")
        compass.itemMeta = compassmeta
        inventory.setItem(22, compass)

        // スロット10番目 赤羊毛
        val redwool = ItemStack(Material.RED_WOOL, 1)
        val redwoolMeta = redwool.itemMeta
        redwoolMeta?.setDisplayName("§cTEAM選択")
        redwoolMeta?.lore = listOf("§7参加するチームを選択できます","§7またチームを離脱することもできます")
        redwool.itemMeta = redwoolMeta
        inventory.setItem(10, redwool)

        // スロット12番目 剣
        val ironSword = ItemStack(Material.IRON_SWORD, 1)
        val ironSwordMeta = ironSword.itemMeta
        ironSwordMeta?.setDisplayName("§bSTRIKER KIT選択")
        ironSwordMeta?.lore = listOf("§7使用するSTRIKER KITを選択できます")
        ironSword.itemMeta = ironSwordMeta
        inventory.setItem(12, ironSword)

        // スロット14番目 弓
        val bow = ItemStack(Material.BOW, 1)
        val bowmeta = bow.itemMeta
        bowmeta?.setDisplayName("§bSPECIAL KIT選択")
        bowmeta?.lore = listOf("§7使用するSPECIAL KITを選択できます")
        bow.itemMeta = bowmeta
        inventory.setItem(14, bow)

        // スロット16番目 白紙の地図
        val map = ItemStack(Material.MAP, 1)
        val mapmeta = map.itemMeta
        mapmeta?.setDisplayName("§eMAP選択")
        mapmeta?.lore = listOf("§7マップを選択できます")
        map.itemMeta = mapmeta
        inventory.setItem(16, map)

        // スロット26番目 ネザライトの剣
        val netheritesword = ItemStack(Material.NETHERITE_SWORD, 1)
        val netheriteswordmeta = netheritesword.itemMeta
        netheriteswordmeta?.setDisplayName("§aMODE選択")
        netheriteswordmeta?.lore = listOf("§7モードを選択できます")
        netheriteswordmeta?.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        netheritesword.itemMeta = netheriteswordmeta
        inventory.setItem(26, netheritesword)

        return inventory
    }
}