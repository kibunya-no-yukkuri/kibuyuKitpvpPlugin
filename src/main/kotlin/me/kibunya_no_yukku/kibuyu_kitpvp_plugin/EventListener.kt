package me.kibunya_no_yukku.kibuyu_kitpvp_plugin


import me.deecaad.weaponmechanics.weapon.weaponevents.WeaponDamageEntityEvent
import me.deecaad.weaponmechanics.weapon.weaponevents.WeaponReloadEvent
import me.deecaad.weaponmechanics.weapon.weaponevents.WeaponShootEvent
import me.kibunya_no_yukku.kibuyu_kitpvp_gui.*
import me.kibunya_no_yukku.kibuyu_kitpvp_plugin.Kibuyu_kitpvp_plugin.Companion.shieldMap
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import kotlin.math.roundToInt
import net.kyori.adventure.text.Component


class EventListener(private val plugin: JavaPlugin): Listener {

    private val loreMap: Map<Material, List<List<String>>> = mapOf(
        //listOf("§7", "§7"),
        //listOf("§bEXスキル1", "§7", "§7消費コストcost,CT秒","§bEXスキル2","§7", "§7消費コストcost,CT秒"),
        //listOf("§bノーマルスキル1", "§7","§bノーマルスキル2", "§7") ,
        //listOf("§bパッシブスキル1", "§7","§bパッシブスキル2", "§7"),
        //listOf("§bサブスキル1", "§7","§bサブスキル2", "§7")
        Material.CAKE to listOf(
            listOf("§7ヒール、バフを両方持っているバランスの良いサポーター", "§7生存能力も高くサポーターとして完成している", "§7ブルーアーカイブより"),
            listOf("§bEXスキル1「集中治療セットA」", "§7右クリック時、半径5m以内の味方HPを6回復する", "§7左クリック時、自身のHPを4回復する", "§7両者共に消費コストcost30,CT15秒","§bEXスキル2「祝福の響き」","§7半径5m以内の味方の攻撃力を43%加算する(15秒間)","§7消費コストcost60,CT15秒"),
            listOf("§bノーマルスキル1「緊急治療セットB」", "§735秒毎に、HPの最も低い味方に対して2HP回復","§bノーマルスキル2「プレゼントボックスC」", "§7通常攻撃130回毎に、自身の攻撃力を27%加算(10秒間)") ,
            listOf("§bパッシブスキル1「白衣の天使」", "§7自身の治癒力を30増加","§bパッシブスキル2「守護天使の意志」", "§7自身の最大HPを5増加"),
            listOf("§bサブスキル1「天使の微笑み」", "§7味方全員のCC抵抗値を5増加","§bサブスキル2「聖なる加護」", "§7リロード時、自身の移動速度を100%加算(1秒間)","§7CT3秒"),
            listOf("§dアルティメット「神出鬼没」", "§7右クリック時、半径100m以内の一番近い味方にテレポートする","§7左クリック時、視点の30m先にテレポートする")
        ),
        Material.SUNFLOWER to listOf(
            listOf("§7圧倒的な回復力を持つヒーラー", "§7弱体状態の解除、スキルコスト減少、防御デバフとやれることは意外と多い", "§7ブルーアーカイブより"),
            listOf("§bEXスキル1「聖なる加護」", "§7自身に6のシールドを付与(20秒間)","§7さらに弱体状態を1つ解除", "§7消費コストcost30,CT15秒","§bEXスキル2「溢れるハート」","§7視点の先の味方に対して10HP回復し自身に対して5HP回復","§7HPを超えた100%分オーバーHPを付与","§7さらに「ファンサービス」を1個獲得","§7消費コストcost20,CT5秒"),
            listOf("§bノーマルスキル1「浄化の洗礼」", "§725秒毎に、最も近い敵を中心とした円形範囲に5ダメージ","§7さらに防御力を24%減少","§bノーマルスキル2「慈愛の投げキッス」","§730秒毎に最もHPの低い味方一人に対して3HPの回復","§7さらに自身に対して2HPの回復") ,
            listOf("§bパッシブスキル1「応援の心構え」", "§7自身の防御力を5増加","§bパッシブスキル2「たゆまぬ努力」", "§7自身の治癒力を30増加"),
            listOf("§bサブスキル1「慈愛の心」", "§7味方全員のHPを5増加","§bサブスキル2「今だけは楽しんで」", "§7「ファンサービス」を3個獲得時、味方全員のスキルコストを10減少(EXスキルの使用1回分)","§7(「ファンサービス」は初期化されます)"),
            listOf("§dアルティメット「お祈りの時間」", "§7半径30m以内の自身含む味方に対して50HP回復","§7HPを超えた100%分オーバーHPを付与")
        )
        // 他のアイテムも追加可能
    )

    private val loreKey = NamespacedKey(plugin, "lore_index")



    //銃撃った人検知
    @EventHandler
    fun onWeaponShoot(event: WeaponShootEvent) {
        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val shoot = scoreboard.getObjective("shoot")
        val user = event.entity

        if (user is Player) {


            val shootScore = shoot?.getScore(user.name)
            shootScore?.let { it.score += 1 } ?: run { shoot?.getScore(user.name)?.score = 1 }
        } else return
    }

    //銃リロードした人検知
    @EventHandler
    fun onWeaponReload(event: WeaponReloadEvent) {

        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val reload = scoreboard.getObjective("reload")

        val user = event.entity

        if (user is Player) {
            // 銃のリロードを検知した際の処理


            val reloadScore = reload?.getScore(user.name)
            reloadScore?.let { it.score += 1 } ?: run { reload?.getScore(user.name)?.score = 1 }
        } else return

    }


    fun attackAmount(attackAmount: Double, attackScore: Int): Double {
        val finalAmount = attackAmount * (1 + (attackScore / 100.0))
        return finalAmount
    }

    //wmの武器でダメージ与えたの検知
    @EventHandler
    fun onWeaponDamage(event: WeaponDamageEntityEvent) {
        val shooter = event.entity
        if (shooter !is Player) return

        // 攻撃力スコアを取得（例：攻撃力はスコアボードの "attack"）
        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val attackScore = scoreboard.getObjective("attack")?.getScore(shooter.name)?.score ?: 0

        // 元のダメージに攻撃力を反映（ここでは +1 ダメージ/1スコア）
        val baseDamage = event.baseDamage
        val finalDamage = attackAmount(baseDamage,attackScore)
        event.baseDamage = finalDamage
    }



    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        // プレイヤー退出時シールドを削除
        shieldMap.remove(player.uniqueId)
    }



    //被ダメージ時防御計算+シールド計算
    @EventHandler
    fun onDamage(event: EntityDamageEvent) {

        val player = event.entity as? Player ?: return

        val originalFinal = event.finalDamage

        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard

        // 基礎防御力
        val defenseObj = scoreboard.getObjective("defense")
        val baseDefense = defenseObj?.getScore(player.name)?.score ?: 0

        // 防御デバフ率（%)
        val debuffObj = scoreboard.getObjective("defense_debuff")
        val debuffPercent = debuffObj?.getScore(player.name)?.score ?: 0

        // 実効防御力 = 防御力 × (1 - デバフ率)
        val effectiveDefense = baseDefense * (1 - (debuffPercent / 100.0))

        // 軽減率 = effectiveDefense / (effectiveDefense + 100.0)
        val reductionRate = if (effectiveDefense > 0) {
            effectiveDefense / (effectiveDefense + 100.0)
        } else 0.0

        // 最終ダメージ計算
        val newDamage = (originalFinal * (1 - reductionRate)).coerceAtLeast(0.0)

        event.damage = newDamage

        player.sendMessage(
            "防御: $baseDefense デバフ: $debuffPercent% → 実効防御: $effectiveDefense → 軽減率: ${(reductionRate * 100).toInt()}% → 最終: $newDamage"
        )




        val shieldInt = shieldMap[player.uniqueId] ?: 0 // UUID から取得、無ければ 0.
        val shield = shieldInt.toDouble()// Int → Double に変換.

        //if (shield <= 0) return // シールドが無ければ処理しない.


        val damage = event.damage // 今回受ける予定のダメージ.
        if (shield >= damage) {
            // シールドがダメージを全吸収できる場合.
            //シールドからダメージを引く.
            val shieldRest = shield - damage
            //残ったシールドを四捨五入
            val shieldFinal: Int = shieldRest.roundToInt()
            //シールドの値を代入
            shieldMap[player.uniqueId] = shieldFinal
            event.damage = 0.0
        } else {
            // シールドが足りない場合 → 残りをHPに通す.
            shieldMap[player.uniqueId] = 0
            event.damage = damage - shield
        }


        //ここからオーバーhp
        val remaining = subtractOverHp(player, event.damage)

        if (remaining <= 0) {
            event.damage = 0.0
        } else {
            event.damage = remaining
        }
    }



    val syncNeeded = mutableSetOf<Player>()

    //ダメージ時オーバーHP処理
    fun subtractOverHp(player: Player, damage: Double): Double {

        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        var remainingDamage = damage

        val indexes = listOf("ULT", "EX", "NS", "PS", "SS") // オーバーHP付与スキル
        val indexes2 = listOf("self", "other") // オーバーHPのバフ実行者
        for (i1 in indexes) {
            for (i2 in indexes2) {

                val timerObj = scoreboard.getObjective("timer_${i2}_over_hp_buff_${i1}") ?: continue
                val speedObj = scoreboard.getObjective("${i2}_over_hp_buff_${i1}_remove_speed") ?: continue

                val timerScore = timerObj.getScore(player.name)
                val speedScore = speedObj.getScore(player.name)

                if (!timerScore.isScoreSet || !speedScore.isScoreSet) continue

                val timer = timerScore.score
                val speed = speedScore.score

                if (speed <= 0) continue

                var currentOverHp = timer.toDouble() / speed.toDouble()

                if (currentOverHp <= 0) continue

                if (currentOverHp >= remainingDamage) {

                    // 必要なtimer減算量 = damage × speed
                    val timerReduction = (remainingDamage * speed).toInt()
                    timerScore.score = (timer - timerReduction).coerceAtLeast(0)

                    return 0.0
                } else {

                    // 全部使う
                    remainingDamage -= currentOverHp
                    timerScore.score = 0
                }
            }
        }
        return remainingDamage
    }






    @EventHandler
    fun onDamageHP(event: EntityDamageEvent) {
        val player = event.entity as? Player ?: return
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            syncToScore(player)
        }, 1L)
    }

    @EventHandler
    fun onHeal(event: EntityRegainHealthEvent) {
        val player = event.entity as? Player ?: return
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            syncToScore(player)
        }, 1L)
    }

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        val player = event.player
        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val maxHpObj = scoreboard.getObjective("max_hp") ?: return
        val hpObj = scoreboard.getObjective("hp") ?: return

        // max_hp スコアを読み込む
        val maxHp = maxHpObj.getScore(player.name).score.coerceAtLeast(1)

        // HP スコアを最大値に戻す
        hpObj.getScore(player.name).score = maxHp

        // プレイヤーの体力も最大に戻す
        player.getAttribute(Attribute.MAX_HEALTH)?.baseValue = maxHp.toDouble()
        player.health = maxHp.toDouble()
    }

    private fun syncToScore(player: Player) {
        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val hpObj = scoreboard.getObjective("hp") ?: return
        hpObj.getScore(player.name).score = player.health.toInt()

        markSync(player)
    }
    fun markSync(player: Player) {
        syncNeeded.add(player)
    }
    fun unmarkSync(player: Player) {
        syncNeeded.remove(player)
    }



    //GUI系
    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {

        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val kit1 = scoreboard.getObjective("kit1")
        val kit2 = scoreboard.getObjective("kit2")

        val player = event.whoClicked as? Player ?: return
        val guiType = Kibuyu_kitpvp_plugin.guiMap[player.uniqueId] ?: return

        event.isCancelled = true // GUI内のアイテムを取られないようにする

        if (event.clickedInventory != event.inventory) return

        when (guiType) {
            "main" -> {
                when (event.slot) {
                    22 -> { // ロビーtp
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tp ${player.name} 0 -60 0")
                        player.closeInventory()
                    }

                    10 -> { // TEAM選択
                        val teamMenu = TeamMenu.create()
                        player.openInventory(teamMenu)
                        Kibuyu_kitpvp_plugin.guiMap[player.uniqueId] = "team"
                    }

                    12 -> { // KIT1選択
                        val kitMenu = KitMenu.create()
                        player.openInventory(kitMenu)
                        Kibuyu_kitpvp_plugin.guiMap[player.uniqueId] = "kit"
                    }

                    14 -> { // KIT2選択
                        val kitMenu = Kit2Menu.create()
                        player.openInventory(kitMenu)
                        Kibuyu_kitpvp_plugin.guiMap[player.uniqueId] = "kit2"
                    }

                    16 -> { // MAP選択
                        val mapMenu = MapMenu.create()
                        player.openInventory(mapMenu)
                        Kibuyu_kitpvp_plugin.guiMap[player.uniqueId] = "map"
                    }

                    26 -> { // MODE選択
                        val modeMenu = ModeMenu.create()
                        player.openInventory(modeMenu)
                        Kibuyu_kitpvp_plugin.guiMap[player.uniqueId] = "mode"
                    }
                }
            }

            "team" -> {
                when (event.slot) {
                    10 -> { // 赤チーム
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join red ${player.name}")
                        player.sendMessage("§c赤チームに参加しました！")
                        player.closeInventory()
                    }

                    12 -> { // 青チーム
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join blue ${player.name}")
                        player.sendMessage("§b青チームに参加しました！")
                        player.closeInventory()
                    }

                    14 -> { // 黄チーム
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join yellow ${player.name}")
                        player.sendMessage("§e黄チームに参加しました！")
                        player.closeInventory()
                    }

                    16 -> { // 緑チーム
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join green ${player.name}")
                        player.sendMessage("§a緑チームに参加しました！")
                        player.closeInventory()
                    }

                    18 -> { // メインメニューへ戻る
                        val inventory = MainMenu.create()
                        player.openInventory(inventory)
                        Kibuyu_kitpvp_plugin.guiMap[player.uniqueId] = "main"
                    }

                    26 -> { // チームを離脱
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team leave ${player.name}")
                        player.sendMessage("§8チームから離脱しました")
                        player.closeInventory()
                    }
                }


            }

            "kit" -> {
                // KIT選択時の処理
                val ki1Score = kit1?.getScore(player.name)
                val ns2MaxObj = scoreboard.getObjective("NS_timer2_max") ?: return
                val ns2MaxScore = ns2MaxObj.getScore(player.name)
                val costUse11AObj = scoreboard.getObjective("cost_use1_1_amount") ?: return
                val costUse11AScore = costUse11AObj.getScore(player.name)
                val costUse12AObj = scoreboard.getObjective("cost_use1_2_amount") ?: return
                val costUse12AScore = costUse12AObj.getScore(player.name)
                val costUse11Obj = scoreboard.getObjective("cost_use1_1") ?: return
                val costUse11Score = costUse11Obj.getScore(player.name)
                val costUse12Obj = scoreboard.getObjective("cost_use1_2") ?: return
                val costUse12Score = costUse12Obj.getScore(player.name)
                val ultUse1Obj = scoreboard.getObjective("ult_use_1") ?: return
                val utUse1Score = ultUse1Obj.getScore(player.name)



                when (event.slot) {
                    10 -> { // kit1を1に(クリセリナ)
                        if(event.isLeftClick) {
                            ki1Score?.let { it.score = 1 }
                            player.sendMessage("§bストライカーキットをセリナに変更しました")
                            ns2MaxScore.score = 700
                            costUse11Score.score = 60
                            costUse12Score.score = 30
                            costUse11AScore.score = 60
                            costUse12AScore.score = 30
                            utUse1Score.score = 50
                            player.closeInventory()
                        }
                        if(event.isRightClick) {
                            val clicked: ItemStack = event.currentItem ?: return
                            if (clicked.type == Material.AIR) return
                            val meta = clicked.itemMeta ?: return
                            val container = meta.persistentDataContainer
                            val patterns = loreMap[clicked.type] ?: return
                            val currentIndex = container.get(loreKey, PersistentDataType.INTEGER) ?: -1
                            val nextIndex = (currentIndex + 1) % patterns.size

                            val newLore = patterns[nextIndex].map { Component.text(it) }
                            meta.lore(newLore)

                            container.set(loreKey, PersistentDataType.INTEGER, nextIndex)
                            clicked.itemMeta = meta
                            event.inventory.setItem(event.slot, clicked)
                        }
                    }
                    11 -> { // kit1を2に()
                        if (event.isLeftClick) {
                            ki1Score?.let { it.score = 2 }
                            player.sendMessage("§bストライカーキットをマジカホリックに変更しました")
                            ns2MaxScore.score = 700
                            costUse11Score.score = 60
                            costUse12Score.score = 30
                            costUse11AScore.score = 60
                            costUse12AScore.score = 30
                            utUse1Score.score = 75
                            player.closeInventory()
                        }
                        if (event.isRightClick) {
                            val clicked: ItemStack = event.currentItem ?: return
                            if (clicked.type == Material.AIR) return
                            val meta = clicked.itemMeta ?: return
                            val container = meta.persistentDataContainer
                            val patterns = loreMap[clicked.type] ?: return
                            val currentIndex = container.get(loreKey, PersistentDataType.INTEGER) ?: -1
                            val nextIndex = (currentIndex + 1) % patterns.size

                            val newLore = patterns[nextIndex].map { Component.text(it) }
                            meta.lore(newLore)

                            container.set(loreKey, PersistentDataType.INTEGER, nextIndex)
                            clicked.itemMeta = meta
                            event.inventory.setItem(event.slot, clicked)
                        }
                    }
                    12 -> { // kit1を2に()
                        if (event.isLeftClick) {
                            ki1Score?.let { it.score = 3 }
                            player.sendMessage("§bストライカーキットを???に変更しました")
                            ns2MaxScore.score = 700
                            costUse11Score.score = 60
                            costUse12Score.score = 30
                            costUse11AScore.score = 60
                            costUse12AScore.score = 30
                            utUse1Score.score = 50
                            player.closeInventory()
                        }
                        if (event.isRightClick) {
                            val clicked: ItemStack = event.currentItem ?: return
                            if (clicked.type == Material.AIR) return
                            val meta = clicked.itemMeta ?: return
                            val container = meta.persistentDataContainer
                            val patterns = loreMap[clicked.type] ?: return
                            val currentIndex = container.get(loreKey, PersistentDataType.INTEGER) ?: -1
                            val nextIndex = (currentIndex + 1) % patterns.size

                            val newLore = patterns[nextIndex].map { Component.text(it) }
                            meta.lore(newLore)

                            container.set(loreKey, PersistentDataType.INTEGER, nextIndex)
                            clicked.itemMeta = meta
                            event.inventory.setItem(event.slot, clicked)
                        }
                    }
                    13 -> { // kit1を2に()
                        if (event.isLeftClick) {
                            ki1Score?.let { it.score = 4 }
                            player.sendMessage("§bストライカーキットを???に変更しました")
                            ns2MaxScore.score = 700
                            costUse11Score.score = 60
                            costUse12Score.score = 30
                            costUse11AScore.score = 60
                            costUse12AScore.score = 30
                            utUse1Score.score = 50
                            player.closeInventory()
                        }
                        if (event.isRightClick) {
                            val clicked: ItemStack = event.currentItem ?: return
                            if (clicked.type == Material.AIR) return
                            val meta = clicked.itemMeta ?: return
                            val container = meta.persistentDataContainer
                            val patterns = loreMap[clicked.type] ?: return
                            val currentIndex = container.get(loreKey, PersistentDataType.INTEGER) ?: -1
                            val nextIndex = (currentIndex + 1) % patterns.size

                            val newLore = patterns[nextIndex].map { Component.text(it) }
                            meta.lore(newLore)

                            container.set(loreKey, PersistentDataType.INTEGER, nextIndex)
                            clicked.itemMeta = meta
                            event.inventory.setItem(event.slot, clicked)
                        }
                    }
                    14 -> { // kit1を2に()
                        if (event.isLeftClick) {
                            ki1Score?.let { it.score = 5 }
                            player.sendMessage("§bストライカーキットを???に変更しました")
                            ns2MaxScore.score = 700
                            costUse11Score.score = 60
                            costUse12Score.score = 30
                            costUse11AScore.score = 60
                            costUse12AScore.score = 30
                            utUse1Score.score = 50
                            player.closeInventory()
                        }
                        if (event.isRightClick) {
                            val clicked: ItemStack = event.currentItem ?: return
                            if (clicked.type == Material.AIR) return
                            val meta = clicked.itemMeta ?: return
                            val container = meta.persistentDataContainer
                            val patterns = loreMap[clicked.type] ?: return
                            val currentIndex = container.get(loreKey, PersistentDataType.INTEGER) ?: -1
                            val nextIndex = (currentIndex + 1) % patterns.size

                            val newLore = patterns[nextIndex].map { Component.text(it) }
                            meta.lore(newLore)

                            container.set(loreKey, PersistentDataType.INTEGER, nextIndex)
                            clicked.itemMeta = meta
                            event.inventory.setItem(event.slot, clicked)
                        }
                    }
                    15 -> { // kit1を2に()
                        if (event.isLeftClick) {
                            ki1Score?.let { it.score = 6 }
                            player.sendMessage("§bストライカーキットを???に変更しました")
                            ns2MaxScore.score = 700
                            costUse11Score.score = 60
                            costUse12Score.score = 30
                            costUse11AScore.score = 60
                            costUse12AScore.score = 30
                            utUse1Score.score = 50
                            player.closeInventory()
                        }
                        if (event.isRightClick) {
                            val clicked: ItemStack = event.currentItem ?: return
                            if (clicked.type == Material.AIR) return
                            val meta = clicked.itemMeta ?: return
                            val container = meta.persistentDataContainer
                            val patterns = loreMap[clicked.type] ?: return
                            val currentIndex = container.get(loreKey, PersistentDataType.INTEGER) ?: -1
                            val nextIndex = (currentIndex + 1) % patterns.size

                            val newLore = patterns[nextIndex].map { Component.text(it) }
                            meta.lore(newLore)

                            container.set(loreKey, PersistentDataType.INTEGER, nextIndex)
                            clicked.itemMeta = meta
                            event.inventory.setItem(event.slot, clicked)
                        }
                    }
                    16 -> { // kit1を2に()
                        if (event.isLeftClick) {
                            ki1Score?.let { it.score = 7 }
                            player.sendMessage("§bストライカーキットを???に変更しました")
                            ns2MaxScore.score = 700
                            costUse11Score.score = 60
                            costUse12Score.score = 30
                            costUse11AScore.score = 60
                            costUse12AScore.score = 30
                            utUse1Score.score = 50
                            player.closeInventory()
                        }
                        if (event.isRightClick) {
                            val clicked: ItemStack = event.currentItem ?: return
                            if (clicked.type == Material.AIR) return
                            val meta = clicked.itemMeta ?: return
                            val container = meta.persistentDataContainer
                            val patterns = loreMap[clicked.type] ?: return
                            val currentIndex = container.get(loreKey, PersistentDataType.INTEGER) ?: -1
                            val nextIndex = (currentIndex + 1) % patterns.size

                            val newLore = patterns[nextIndex].map { Component.text(it) }
                            meta.lore(newLore)

                            container.set(loreKey, PersistentDataType.INTEGER, nextIndex)
                            clicked.itemMeta = meta
                            event.inventory.setItem(event.slot, clicked)
                        }
                    }

                    18 -> { // メインメニューへ戻る
                        val inventory = MainMenu.create()
                        player.openInventory(inventory)
                        Kibuyu_kitpvp_plugin.guiMap[player.uniqueId] = "main"
                    }
                }
            }

            "kit2" -> {
                // KIT選択時の処理
                val ki2Score = kit2?.getScore(player.name)
                val ns1MaxObj = scoreboard.getObjective("NS_timer1_max2") ?: return
                val ns1MaxScore2 = ns1MaxObj.getScore(player.name)
                val ns2MaxObj = scoreboard.getObjective("NS_timer2_max2") ?: return
                val ns2MaxScore2 = ns2MaxObj.getScore(player.name)
                val costUse21AObj = scoreboard.getObjective("cost_use2_1_amount") ?: return
                val costUse21AScore = costUse21AObj.getScore(player.name)
                val costUse22AObj = scoreboard.getObjective("cost_use2_2_amount") ?: return
                val costUse22AScore = costUse22AObj.getScore(player.name)
                val costUse21Obj = scoreboard.getObjective("cost_use2_1") ?: return
                val costUse21Score = costUse21Obj.getScore(player.name)
                val costUse22Obj = scoreboard.getObjective("cost_use2_2") ?: return
                val costUse22Score = costUse22Obj.getScore(player.name)
                val ultUse2Obj = scoreboard.getObjective("ult_use_2") ?: return
                val utUse2Score = ultUse2Obj.getScore(player.name)

                when (event.slot) {
                    10 -> { // kit2を1に(マリー)
                        if(event.isLeftClick) {
                            ki2Score?.let { it.score = 1 }
                            player.sendMessage("§bスペシャルキットをマリーに変更しました")
                            ns1MaxScore2.score = 500
                            ns2MaxScore2.score = 600
                            costUse21Score.score = 30
                            costUse22Score.score = 20
                            costUse21AScore.score = 30
                            costUse22AScore.score = 20
                            utUse2Score.score = 100
                            player.closeInventory()
                        }
                        if(event.isRightClick) {
                            val clicked: ItemStack = event.currentItem ?: return
                            if (clicked.type == Material.AIR) return
                            val meta = clicked.itemMeta ?: return
                            val container = meta.persistentDataContainer
                            val patterns = loreMap[clicked.type] ?: return
                            val currentIndex = container.get(loreKey, PersistentDataType.INTEGER) ?: -1
                            val nextIndex = (currentIndex + 1) % patterns.size

                            val newLore = patterns[nextIndex].map { Component.text(it) }
                            meta.lore(newLore)

                            container.set(loreKey, PersistentDataType.INTEGER, nextIndex)
                            clicked.itemMeta = meta
                            event.inventory.setItem(event.slot, clicked)
                        }
                    }
                    11 -> { // kit2を2に()
                        if(event.isLeftClick) {
                            ki2Score?.let { it.score = 2 }
                            player.sendMessage("§bスペシャルキットを??に変更しました")
                            ns1MaxScore2.score = 500
                            ns2MaxScore2.score = 600
                            costUse21Score.score = 30
                            costUse22Score.score = 20
                            costUse21AScore.score = 30
                            costUse22AScore.score = 20
                            utUse2Score.score = 100
                            player.closeInventory()
                        }
                        if(event.isRightClick) {
                            val clicked: ItemStack = event.currentItem ?: return
                            if (clicked.type == Material.AIR) return
                            val meta = clicked.itemMeta ?: return
                            val container = meta.persistentDataContainer
                            val patterns = loreMap[clicked.type] ?: return
                            val currentIndex = container.get(loreKey, PersistentDataType.INTEGER) ?: -1
                            val nextIndex = (currentIndex + 1) % patterns.size

                            val newLore = patterns[nextIndex].map { Component.text(it) }
                            meta.lore(newLore)

                            container.set(loreKey, PersistentDataType.INTEGER, nextIndex)
                            clicked.itemMeta = meta
                            event.inventory.setItem(event.slot, clicked)
                        }
                    }
                    12 -> { // kit2を2に()
                        if(event.isLeftClick) {
                            ki2Score?.let { it.score = 3 }
                            player.sendMessage("§bスペシャルキットを??に変更しました")
                            ns1MaxScore2.score = 500
                            ns2MaxScore2.score = 600
                            costUse21Score.score = 30
                            costUse22Score.score = 20
                            costUse21AScore.score = 30
                            costUse22AScore.score = 20
                            utUse2Score.score = 100
                            player.closeInventory()
                        }
                        if(event.isRightClick) {
                            val clicked: ItemStack = event.currentItem ?: return
                            if (clicked.type == Material.AIR) return
                            val meta = clicked.itemMeta ?: return
                            val container = meta.persistentDataContainer
                            val patterns = loreMap[clicked.type] ?: return
                            val currentIndex = container.get(loreKey, PersistentDataType.INTEGER) ?: -1
                            val nextIndex = (currentIndex + 1) % patterns.size

                            val newLore = patterns[nextIndex].map { Component.text(it) }
                            meta.lore(newLore)

                            container.set(loreKey, PersistentDataType.INTEGER, nextIndex)
                            clicked.itemMeta = meta
                            event.inventory.setItem(event.slot, clicked)
                        }
                    }
                    13 -> { // kit2を2に()
                        if(event.isLeftClick) {
                            ki2Score?.let { it.score = 4 }
                            player.sendMessage("§bスペシャルキットを??に変更しました")
                            ns1MaxScore2.score = 500
                            ns2MaxScore2.score = 600
                            costUse21Score.score = 30
                            costUse22Score.score = 20
                            costUse21AScore.score = 30
                            costUse22AScore.score = 20
                            utUse2Score.score = 100
                            player.closeInventory()
                        }
                        if(event.isRightClick) {
                            val clicked: ItemStack = event.currentItem ?: return
                            if (clicked.type == Material.AIR) return
                            val meta = clicked.itemMeta ?: return
                            val container = meta.persistentDataContainer
                            val patterns = loreMap[clicked.type] ?: return
                            val currentIndex = container.get(loreKey, PersistentDataType.INTEGER) ?: -1
                            val nextIndex = (currentIndex + 1) % patterns.size

                            val newLore = patterns[nextIndex].map { Component.text(it) }
                            meta.lore(newLore)

                            container.set(loreKey, PersistentDataType.INTEGER, nextIndex)
                            clicked.itemMeta = meta
                            event.inventory.setItem(event.slot, clicked)
                        }
                    }
                    14 -> { // kit2を2に()
                        if(event.isLeftClick) {
                            ki2Score?.let { it.score = 5 }
                            player.sendMessage("§bスペシャルキットを??に変更しました")
                            ns1MaxScore2.score = 500
                            ns2MaxScore2.score = 600
                            costUse21Score.score = 30
                            costUse22Score.score = 20
                            costUse21AScore.score = 30
                            costUse22AScore.score = 20
                            utUse2Score.score = 100
                            player.closeInventory()
                        }
                        if(event.isRightClick) {
                            val clicked: ItemStack = event.currentItem ?: return
                            if (clicked.type == Material.AIR) return
                            val meta = clicked.itemMeta ?: return
                            val container = meta.persistentDataContainer
                            val patterns = loreMap[clicked.type] ?: return
                            val currentIndex = container.get(loreKey, PersistentDataType.INTEGER) ?: -1
                            val nextIndex = (currentIndex + 1) % patterns.size

                            val newLore = patterns[nextIndex].map { Component.text(it) }
                            meta.lore(newLore)

                            container.set(loreKey, PersistentDataType.INTEGER, nextIndex)
                            clicked.itemMeta = meta
                            event.inventory.setItem(event.slot, clicked)
                        }
                    }
                    15 -> { // kit2を2に()
                        if(event.isLeftClick) {
                            ki2Score?.let { it.score = 6 }
                            player.sendMessage("§bスペシャルキットを??に変更しました")
                            ns1MaxScore2.score = 500
                            ns2MaxScore2.score = 600
                            costUse21Score.score = 30
                            costUse22Score.score = 20
                            costUse21AScore.score = 30
                            costUse22AScore.score = 20
                            utUse2Score.score = 100
                            player.closeInventory()
                        }
                        if(event.isRightClick) {
                            val clicked: ItemStack = event.currentItem ?: return
                            if (clicked.type == Material.AIR) return
                            val meta = clicked.itemMeta ?: return
                            val container = meta.persistentDataContainer
                            val patterns = loreMap[clicked.type] ?: return
                            val currentIndex = container.get(loreKey, PersistentDataType.INTEGER) ?: -1
                            val nextIndex = (currentIndex + 1) % patterns.size

                            val newLore = patterns[nextIndex].map { Component.text(it) }
                            meta.lore(newLore)

                            container.set(loreKey, PersistentDataType.INTEGER, nextIndex)
                            clicked.itemMeta = meta
                            event.inventory.setItem(event.slot, clicked)
                        }
                    }
                    16 -> { // kit2を2に()
                        if(event.isLeftClick) {
                            ki2Score?.let { it.score = 7 }
                            player.sendMessage("§bスペシャルキットを??に変更しました")
                            ns1MaxScore2.score = 500
                            ns2MaxScore2.score = 600
                            costUse21Score.score = 30
                            costUse22Score.score = 20
                            costUse21AScore.score = 30
                            costUse22AScore.score = 20
                            utUse2Score.score = 100
                            player.closeInventory()
                        }
                        if(event.isRightClick) {
                            val clicked: ItemStack = event.currentItem ?: return
                            if (clicked.type == Material.AIR) return
                            val meta = clicked.itemMeta ?: return
                            val container = meta.persistentDataContainer
                            val patterns = loreMap[clicked.type] ?: return
                            val currentIndex = container.get(loreKey, PersistentDataType.INTEGER) ?: -1
                            val nextIndex = (currentIndex + 1) % patterns.size

                            val newLore = patterns[nextIndex].map { Component.text(it) }
                            meta.lore(newLore)

                            container.set(loreKey, PersistentDataType.INTEGER, nextIndex)
                            clicked.itemMeta = meta
                            event.inventory.setItem(event.slot, clicked)
                        }
                    }

                    18 -> { // メインメニューへ戻る
                        val inventory = MainMenu.create()
                        player.openInventory(inventory)
                        Kibuyu_kitpvp_plugin.guiMap[player.uniqueId] = "main"
                    }
                }
            }

            "map" -> {
                // MAP選択時の処理
                when (event.slot) {
                    13 -> { // 異界の書物庫（笑）
                        Bukkit.dispatchCommand(
                            Bukkit.getConsoleSender(),
                            "scoreboard players set map map_selector 1"
                        )
                        Bukkit.getConsoleSender().sendMessage("§fマップが§5異界の書物庫（笑）§fに変更されました")
                        Bukkit.getOnlinePlayers().forEach { player ->
                            player.sendMessage("§fマップが§5異界の書物庫（笑）§fに変更されました")
                        }
                        player.closeInventory()
                    }

                    18 -> { // メインメニューへ戻る
                        val inventory = MainMenu.create()
                        player.openInventory(inventory)
                        Kibuyu_kitpvp_plugin.guiMap[player.uniqueId] = "main"
                    }
                }
            }

            "mode" -> {
                // MODE選択時の処理
                when (event.slot) {
                    18 -> { // メインメニューへ戻る
                        val inventory = MainMenu.create()
                        player.openInventory(inventory)
                        Kibuyu_kitpvp_plugin.guiMap[player.uniqueId] = "main"
                    }
                }
            }
        }
    }

    //inv閉じた時GUIだったらGUI開いてるよタグを消す
    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player as? Player ?: return
        Kibuyu_kitpvp_plugin.guiMap.remove(player.uniqueId)
    }



}








