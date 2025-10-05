package me.kibunya_no_yukku.kibuyu_kitpvp_gui

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object MapMenu {
    fun create(): Inventory {
        val inventory = Bukkit.createInventory(null, 9*3, "§eMAPを選択")

        // スロット12番目 本棚
        val bookshelf = ItemStack(Material.BOOKSHELF, 1)
        val bookshelfmeta = bookshelf.itemMeta
        bookshelfmeta?.setDisplayName("§5異界ノ書物庫（笑）")
        bookshelf.itemMeta = bookshelfmeta
        inventory.setItem(12, bookshelf)

        // スロット14番目 ?
        val maow = ItemStack(Material.BOOKSHELF, 1)
        val maowmeta = bookshelf.itemMeta
        maowmeta?.setDisplayName("§5???")
        maow.itemMeta = maowmeta
        inventory.setItem(14, maow)

        // スロット18番目 矢
        val arrow = ItemStack(Material.ARROW, 1)
        val arrowmeta = arrow.itemMeta
        arrowmeta?.setDisplayName("§fメインメニューへ戻る")
        arrow.itemMeta = arrowmeta
        inventory.setItem(18, arrow)

        return inventory
    }
}