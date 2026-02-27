package me.kibunya_no_yukku.kibuyu_kitpvp_plugin

import me.deecaad.weaponmechanics.utils.CustomTag
import me.kibunya_no_yukku.kibuyu_kitpvp_plugin.Kibuyu_kitpvp_plugin.Companion.shieldMap
import net.kyori.adventure.text.Component
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin


class Tick(private val plugin: Kibuyu_kitpvp_plugin) {




    //kit付与
    fun start() {
        object : BukkitRunnable() {
            override fun run() {
                val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
                val kitGiveObjective = scoreboard.getObjective("kit_give") ?: return
                val kit1Obj = scoreboard.getObjective("kit1") ?: return
                val kit2Obj = scoreboard.getObjective("kit2") ?: return

                val score = kitGiveObjective.getScore("give").score
                if (score == 1) {

                    for (player in Bukkit.getOnlinePlayers()) {

                        if (player.scoreboardTags.contains("s")) {

                            val kit1Score = kit1Obj.getScore(player.name).score
                            val kit2Score = kit2Obj.getScore(player.name).score

                            if (kit1Score == 1) {
                                Bukkit.dispatchCommand(
                                    Bukkit.getConsoleSender(),
                                    "wm give ${player.name} Tactical_Therapy 1 slot:4"
                                )
                                Bukkit.dispatchCommand(
                                    Bukkit.getConsoleSender(),
                                    "item replace entity ${player.name} container.1 with diamond[minecraft:item_model=wind_charge,custom_name=\"§l§d神出鬼没 ULT:50\",minecraft:lore=[\"§r§7右クリック時、半径100m以内の一番近い味方にテレポートする\",\"§r§7左クリック時、視点の30m先にテレポートする\",\"§r§7消費ULTコスト50,CT25秒\"]]"
                                )
                                Bukkit.dispatchCommand(
                                    Bukkit.getConsoleSender(),
                                    "item replace entity ${player.name} container.3 with iron_ingot[minecraft:item_model=bell,custom_name=\"§l§b祝福の響き C:65\",minecraft:lore=[\"§r§7半径5m以内の味方の攻撃力を43%加算する(15秒間)\",\"§r§7消費コストcost60,CT15秒\"]]"
                                )
                                Bukkit.dispatchCommand(
                                    Bukkit.getConsoleSender(),
                                    "item replace entity ${player.name} container.2 with copper_ingot[minecraft:item_model=red_dye,custom_name=\"§l§b集中治療セットA C:30\",minecraft:lore=[\"§r§7右クリック時、半径5m以内の味方HPを6回復する\",\"§r§7左クリック時、自身のHPを4回復する\",\"§r§7両者共に消費コストcost30,CT15秒\"]]"
                                )
                            }
                            if (kit2Score == 1) {
                                Bukkit.dispatchCommand(
                                    Bukkit.getConsoleSender(),
                                    "wm give ${player.name} piety 1 slot:5"
                                )
                                Bukkit.dispatchCommand(
                                    Bukkit.getConsoleSender(),
                                    "item replace entity ${player.name} container.8 with emerald[minecraft:item_model=nether_star,custom_name=\"§l§dお祈りの時間 ULT100\",minecraft:lore=[\"§r§7半径30m以内の自身含む味方に対して50HP回復\",\"§r§7HPを超えた100%分オーバーHPを付与\",\"§r§7消費ULTコスト100,CT50秒\"]]"
                                )
                                Bukkit.dispatchCommand(
                                    Bukkit.getConsoleSender(),
                                    "item replace entity ${player.name} container.7 with netherite_ingot[minecraft:item_model=honeycomb,custom_name=\"§l§b溢れるハート C20\",minecraft:lore=[\"§r§7視点の先の味方に対して10HP回復し自身に対して5HP回復\",\"§r§7HPを超えた100%分オーバーHPを付与\",\"§r§7さらに「ファンサービス」を1個獲得\",\"§r§7消費コストcost20,CT5秒\"]]"
                                )
                                Bukkit.dispatchCommand(
                                    Bukkit.getConsoleSender(),
                                    "item replace entity ${player.name} container.6 with gold_ingot[minecraft:item_model=heart_of_the_sea,custom_name=\"§l§b聖なる加護 C:30\",minecraft:lore=[\"§r§7自身に6のシールドを付与(20秒間)\",\"§r§7さらに弱体状態を1つ解除\"]]"
                                )
                            }
                        }
                    }

                    Bukkit.broadcast(Component.text("§aキットが配布されました！"))
                    kitGiveObjective.getScore("give").score = 0

                }

            }
        }.runTaskTimer(plugin, 0L, 20L) // 1秒毎.





        //NS実行
        object : BukkitRunnable() {
            override fun run() {
                for (player in Bukkit.getOnlinePlayers()) {
                    val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
                    val ns2Obj = scoreboard.getObjective("NS_timer2") ?: return
                    val ns3Obj = scoreboard.getObjective("NS_timer1_2") ?: return
                    val ns4Obj = scoreboard.getObjective("NS_timer2_2") ?: return
                    val kit1Obj = scoreboard.getObjective("kit1") ?: return
                    val kit2Obj = scoreboard.getObjective("kit2") ?: return
                    val ns2Score = ns2Obj.getScore(player.name)
                    val ns3Score = ns3Obj.getScore(player.name)
                    val ns4Score = ns4Obj.getScore(player.name)
                    val kit1Score = kit1Obj.getScore(player.name)
                    val kit2Score = kit2Obj.getScore(player.name)

                    if(ns2Score.score <= 0) {
                        when (kit1Score.score) {
                            1 -> {
                                kit101NS2(player)
                            }
                            //2 -> {kit102NS2(player)}

                        }
                    }
                    if(ns3Score.score <= 0) {
                        when (kit2Score.score) {
                            1 -> {
                                kit201NS1(plugin, player)
                            }

                        }
                    }
                    if(ns4Score.score <= 0) {
                        when (kit2Score.score) {
                            1 -> {
                                kit201NS2(player)
                            }

                        }
                    }
                }
            }
            //ここからNS処理関数.
            //ki1のNS2の処理.
            fun kit101NS2(player: Player){

                val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
                val healObj = scoreboard.getObjective("heal") ?: return
                val hpObj = scoreboard.getObjective("hp") ?: return
                val maxHpObj = scoreboard.getObjective("max_hp") ?: return
                val ns2Obj = scoreboard.getObjective("NS_timer2") ?: return
                val ns2MaxObj = scoreboard.getObjective("NS_timer2_max") ?: return
                val myTeam = scoreboard.getEntryTeam(player.name) ?: return // 発動者のチーム
                val ns2Score = ns2Obj.getScore(player.name)
                val ns2MaxScore = ns2MaxObj.getScore(player.name)
                val healScore = healObj.getScore(player.name)
                ns2Score.score = ns2MaxScore.score


                // 同じワールドの同チームプレイヤーを検索（自身含む)
                val candidates = Bukkit.getOnlinePlayers().filter { p ->
                    // 同じワールド
                    p.world == player.world &&
                            // tag"s"を持っているか.
                            p.scoreboardTags.contains("s")
                    // チームが同じ（味方）
                    p.scoreboard.getEntryTeam(p.name) == myTeam
                }

                if (candidates.isEmpty()) {
                    player.sendMessage("§e緊急治療セットB発動")
                    player.sendMessage("§c味方が見つかりません")
                    return
                }
                // 最もHPが低いプレイヤーを探す
                val nearest = candidates
                    .filter { hpObj.getScore(it.name).score < maxHpObj.getScore(it.name).score }
                    .minByOrNull { hpObj.getScore(it.name).score }
                if (nearest == null) {
                    player.sendMessage("§e緊急治療セットB発動！")
                    player.sendMessage("§cHPの減っている味方がいませんでした")
                    player.sendMessage("§e次緊急治療セットB発動までの時間を20秒短くしました")
                    ns2Score.score -= 400
                    return
                }


                // 音を出す.
                nearest.world.playSound(nearest.location, Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 1.0f, 2.0f)

                // パーティクルを出す.
                nearest.world.spawnParticle(
                    Particle.HEART,
                    nearest.location.add(0.0, 1.0, 0.0),
                    10, 0.5, 0.5, 0.5, 0.0
                )

                // スコアを +2*治癒力/100
                val hpScore = nearest.let { hpObj.getScore(it.name) }
                val healAmount = 2.0 * (1 + (healScore.score.toDouble() / 100.0))
                hpScore.score += healAmount.roundToInt()

                plugin.listener.markSync(nearest)
                nearest.let { player.sendMessage("§e緊急治療セットB発動！") }
                nearest.let { player.sendMessage("§e${it.name}のHPを${healAmount.roundToInt()}回復！(現在のHP: ${hpScore.score})") }
                nearest.sendMessage("§aHPが ${player.name}により${healAmount.roundToInt()}回復！")

                return
            }

            fun kit201NS1(plugin: JavaPlugin, player: Player){

                val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
                val ns201Obj = scoreboard.getObjective("NS_timer1_2") ?: return
                val ns201MaxObj = scoreboard.getObjective("NS_timer1_max2") ?: return
                val ns201Score = ns201Obj.getScore(player.name)
                val ns201MaxScore = ns201MaxObj.getScore(player.name)
                //nsタイマーリセット
                ns201Score.score = ns201MaxScore.score

                val myTeam = player.scoreboard.getEntryTeam(player.name)

                // 半径100ブロック以内のプレイヤーを探す
                val nearbyEnemies = player.getNearbyEntities(100.0, 100.0, 100.0)
                    .filterIsInstance<Player>()
                    .filter { it != player }
                    .filter { enemy ->
                        val enemyTeam = enemy.scoreboard.getEntryTeam(enemy.name)
                        // チームが違うプレイヤーを敵とする
                        enemyTeam != null && enemyTeam != myTeam
                    }

                // 一番近い敵
                val target = nearbyEnemies.minByOrNull { it.location.distance(player.location) } ?: return
                val targetLocation = target.location.clone()

                // 予告パーティクル（即時）
                val world = targetLocation.world
                val particleCount = 30
                for (i in 0 until particleCount) {
                    val angle = 2 * Math.PI * i / particleCount
                    val x = cos(angle) * 3.0
                    val z = sin(angle) * 3.0
                    world?.spawnParticle(
                        Particle.DUST,
                        targetLocation.clone().add(x, 0.0, z),
                        1,
                        Particle.DustOptions(Color.RED, 2f) // 赤い予告サークル
                    )
                }

                object : BukkitRunnable() {
                    var ticks = 0
                    val start = target.location.clone().add(0.0, 20.0, 0.0) // 頭上10ブロック
                    val end = target.location.clone() // ターゲットの位置
                    val totalTicks = 30 // 1.5秒 (30tick)

                    override fun run() {
                        if (ticks > totalTicks) {
                            cancel() // 1.5秒経ったら終了
                            return
                        }

                        // 補間 (線形補間: Lerp)
                        val progress = ticks.toDouble() / totalTicks.toDouble()
                        val currentY = start.y + (end.y - start.y) * progress
                        val currentLoc = start.clone().apply { y = currentY }

                        // パーティクルを出す
                        currentLoc.world?.spawnParticle(
                            Particle.WAX_ON, // 好きなパーティクルに変更可能
                            currentLoc,
                            3, 0.1, 0.1, 0.1, 0.0
                        )
                        currentLoc.world?.spawnParticle(
                            Particle.FLAME, // 好きなパーティクルに変更可能
                            currentLoc,
                            3, 0.1, 0.1, 0.1, 0.0
                        )

                        ticks++
                    }
                }.runTaskTimer(plugin, 0L, 1L) // 1tickごとに実行

                // 1秒後に範囲ダメージ
                object : BukkitRunnable() {
                    override fun run() {
                        val enemiesInRange = targetLocation.world?.getNearbyEntities(targetLocation, 3.0, 3.0, 3.0)
                            ?.filterIsInstance<Player>()
                            ?.filter { enemy ->
                                val enemyTeam = enemy.scoreboard.getEntryTeam(enemy.name)
                                enemy != player && enemyTeam != null && enemyTeam != myTeam
                            }

                        if (enemiesInRange != null) {
                            val defenseDeBuffObj = scoreboard.getObjective("other_defense_debuff_NS") ?: return
                            val defenseDeBuffTimeObj = scoreboard.getObjective("timer_other_defense_debuff_NS") ?: return
                            val deBuffTimeObj = scoreboard.getObjective("add_debuff_time") ?: return
                            val attackObj = scoreboard.getObjective("attack") ?: return
                            for (enemy in enemiesInRange) {
                                val defenseDeBuffScore = defenseDeBuffObj.getScore(enemy.name)
                                val defenseDeBuffTimeScore = defenseDeBuffTimeObj.getScore(enemy.name)
                                val deBuffTimeScore = deBuffTimeObj.getScore(enemy.name)
                                val attackScore = attackObj.getScore(enemy.name)

                                //防御デバフ&デバフ時間計算
                                defenseDeBuffScore.score = 24
                                val debuffTimeIncrease = 300 * (1 + (deBuffTimeScore.score / 100.0))
                                defenseDeBuffTimeScore.score = debuffTimeIncrease.roundToInt()
                                //ダメージ計算
                                val bonusDamage = 5.0 * (attackScore.score.toDouble() / 100.0)
                                val damageAmount = 5 + bonusDamage
                                enemy.damage(damageAmount, player) // ダメージを与える
                            }
                        }

                        val world = targetLocation.world
                        world?.spawnParticle(
                            Particle.EXPLOSION,
                            targetLocation, // そのままターゲットの座標
                            25,             // パーティクルの個数
                            1.0, 1.0, 1.0,  // 広がり（X, Y, Z 方向）
                            1.0             // 速度
                        )
                        world?.spawnParticle(
                            Particle.WAX_ON,
                            targetLocation, // そのままターゲットの座標
                            100,             // パーティクルの個数
                            0.1, 0.1, 0.1,  // 広がり（X, Y, Z 方向）
                            15.0             // 速度
                        )
                    }
                }.runTaskLater(plugin, 30L) // 20tick = 1秒後
            }

            fun kit201NS2(player: Player){

                val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
                val healObj = scoreboard.getObjective("heal") ?: return
                val hpObj = scoreboard.getObjective("hp") ?: return
                val maxHpObj = scoreboard.getObjective("max_hp") ?: return
                val ns2Obj = scoreboard.getObjective("NS_timer2_2") ?: return
                val ns2MaxObj = scoreboard.getObjective("NS_timer2_max2") ?: return
                val myTeam = scoreboard.getEntryTeam(player.name) ?: return // 発動者のチーム
                val ns2Score = ns2Obj.getScore(player.name)
                val ns2MaxScore = ns2MaxObj.getScore(player.name)
                val healScore = healObj.getScore(player.name)
                val playerHpScore = hpObj.getScore(player.name)
                ns2Score.score = ns2MaxScore.score

                val playerHealAmount = 2.0 * (1 + (healScore.score.toDouble() / 100.0))
                //プレイヤーのHPに回復量を+
                playerHpScore.score += playerHealAmount.roundToInt()
                //HP同期.
                plugin.listener.markSync(player)
                //メッセージ
                player.sendMessage("§e慈愛の投げキッス発動！")
                player.sendMessage("§aHPが${playerHealAmount.roundToInt()} 回復！")

                // 同じワールドの同チームプレイヤーを検索（自身含む)
                val candidates = Bukkit.getOnlinePlayers().filter { p ->
                    p != player && // 自分を除外
                            p.world == player.world && // 同じワールド
                            p.scoreboardTags.contains("s") && // tag"s"を持つ
                            p.scoreboard.getEntryTeam(p.name) == myTeam // チームが同じ
                }

                if (candidates.isEmpty()) {
                    player.sendMessage("§c味方が見つかりません")
                    return
                }
                // 最もHPが低い味方プレイヤーを選ぶ
                val nearest = candidates
                    .filter { hpObj.getScore(it.name).score < maxHpObj.getScore(it.name).score }
                    .minByOrNull { hpObj.getScore(it.name).score }
                if (nearest == null) {
                    player.sendMessage("§cHPの減っている味方がいませんでした")
                    return
                }


                // 音を出す.
                nearest.world.playSound(nearest.location, Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 1.0f, 2.0f)

                // パーティクルを出す.
                nearest.world.spawnParticle(
                    Particle.HEART,
                    nearest.location.add(0.0, 1.0, 0.0),
                    10, 0.5, 0.5, 0.5, 0.0
                )

                //
                val hpScore = nearest.let { hpObj.getScore(it.name) }
                //ヒール量計算
                val healAmount = 3.0 * (1 + (healScore.score.toDouble() / 100.0))
                hpScore.score += healAmount.roundToInt()
                //HP同期.
                plugin.listener.markSync(nearest)
                //メッセージ.
                nearest.let { player.sendMessage("§e${it.name}のHPを+${healAmount.roundToInt()} 回復！(現在のHP: ${hpScore.score})") }
                nearest.sendMessage("§aHPが ${player.name}により${healAmount.roundToInt()} 回復！")

                return
            }





        }.runTaskTimer(plugin, 0L, 20L) // 1秒毎.







        //シールド処理&アクションバー表示&オーバーhp処理(毎tick処理).
        object : BukkitRunnable() {
            override fun run() {
                for (player in Bukkit.getOnlinePlayers()) {
                    //ここからシールドパーティクル
                    val shieldInt = shieldMap[player.uniqueId] ?: 0 // UUID から取得、無ければ 0.

                    if (shieldInt >= 1) {
                        //パーティクル.
                        val world = player.world
                        val center = player.location.clone().add(0.0, 1.0, 0.0)

                        val points = 24
                        val radius = 0.9

                        for (i in 0 until points) {
                            val angle = 2 * Math.PI * i / points
                            val x = cos(angle) * radius
                            val z = sin(angle) * radius

                            world.spawnParticle(
                                Particle.ENCHANTED_HIT,
                                center.clone().add(x, 0.0, z),
                                1,
                                0.0, 0.0, 0.0,
                                0.01
                            )
                        }
                    }
                    val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
                    val shieldObj = scoreboard.getObjective("shield") ?: return
                    val shieldTimeObj = scoreboard.getObjective("shield_time") ?: return
                    val costDownAmountObj = scoreboard.getObjective("costDown_buff_amount") ?: return
                    val costDownAmountScore = costDownAmountObj.getScore(player.name).score
                    val shieldScore = shieldObj.getScore(player.name)
                    val shieldTimeScore = shieldTimeObj.getScore(player.name)
                    if(shieldScore.score >= 1){
                        if(shieldTimeScore.score >= 1) {


                            val duration = shieldTimeScore.score.toLong()

                            //シールド付与呼び出し.
                            plugin.skillListener.giveShield(player, shieldScore.score, duration )

                            //ここにスコアset0処理.
                            shieldScore.score = 0
                            shieldTimeScore.score = 0
                        }
                        else {
                        //シールド量あるのに時間指定がないとき:rage:.
                            //ここにスコアset0処理.
                            shieldScore.score = 0
                            shieldTimeScore.score = 0
                            //負荷軽減のために何も書かない.←やっぱバグりそうだから書く.
                        }
                    }
                    else {
                        //シールド量ないのに時間指定だけあるor両方ない:rage:.
                        //ここにスコアset0処理.
                        shieldScore.score = 0
                        shieldTimeScore.score = 0
                        //負荷軽減のために何も書かない.←やっぱバグりそうだから書く.
                    }
                    //ここからアクションバー.
                    val shieldHp = shieldMap[player.uniqueId] ?: 0

                    // WeaponMechanics から残弾取得
                    val weaponStack = player.inventory.itemInMainHand
                    val ammoLeft = if (weaponStack.type != Material.AIR) {
                        CustomTag.AMMO_LEFT.getInteger(weaponStack)
                    } else 0

                    //ultコスト取得
                    val ultObj = scoreboard.getObjective("ult") ?: return
                    val ultScore = ultObj.getScore(player.name).score
                    val ultUse1Obj = scoreboard.getObjective("ult_use_1") ?: return
                    val ultUse1Score = ultUse1Obj.getScore(player.name).score
                    val ultUse2Obj = scoreboard.getObjective("ult_use_2") ?: return
                    val ultUse2Score = ultUse2Obj.getScore(player.name).score


                    player.sendActionBar(
                        Component.text("§e残弾数: $ammoLeft 発 | シールド: $shieldHp | コスト減少値: $costDownAmountScore | ウルト: $ultScore /$ultUse1Score,$ultUse2Score")
                    )

                    //ここからオーバーHP処理.
                    //オブジェクト名は頭文字を合わせたもの.
                    val sOHBUObj = scoreboard.getObjective("self_over_hp_buff_ULT") ?: return
                    val sOHBEObj = scoreboard.getObjective("self_over_hp_buff_EX") ?: return
                    val sOHBNObj = scoreboard.getObjective("self_over_hp_buff_NS") ?: return
                    val sOHBPObj = scoreboard.getObjective("self_over_hp_buff_PS") ?: return
                    val sOHBSObj = scoreboard.getObjective("self_over_hp_buff_SS") ?: return
                    val oOHBUObj = scoreboard.getObjective("other_over_hp_buff_ULT") ?: return
                    val oOHBEObj = scoreboard.getObjective("other_over_hp_buff_EX") ?: return
                    val oOHBNObj = scoreboard.getObjective("other_over_hp_buff_NS") ?: return
                    val oOHBPObj = scoreboard.getObjective("other_over_hp_buff_PS") ?: return
                    val oOHBSObj = scoreboard.getObjective("other_over_hp_buff_SS") ?: return
                    val tSOHBUObj = scoreboard.getObjective("timer_self_over_hp_buff_ULT") ?: return
                    val tSOHBEObj = scoreboard.getObjective("timer_self_over_hp_buff_EX") ?: return
                    val tSOHBNObj = scoreboard.getObjective("timer_self_over_hp_buff_NS") ?: return
                    val tSOHBPObj = scoreboard.getObjective("timer_self_over_hp_buff_PS") ?: return
                    val tSOHBSObj = scoreboard.getObjective("timer_self_over_hp_buff_SS") ?: return
                    val tOOHBUObj = scoreboard.getObjective("timer_other_over_hp_buff_ULT") ?: return
                    val tOOHBEObj = scoreboard.getObjective("timer_other_over_hp_buff_EX") ?: return
                    val tOOHBNObj = scoreboard.getObjective("timer_other_over_hp_buff_NS") ?: return
                    val tOOHBPObj = scoreboard.getObjective("timer_other_over_hp_buff_PS") ?: return
                    val tOOHBSObj = scoreboard.getObjective("timer_other_over_hp_buff_SS") ?: return
                    val sOHBURSObj = scoreboard.getObjective("self_over_hp_buff_ULT_remove_speed") ?: return
                    val sOHBERSObj = scoreboard.getObjective("self_over_hp_buff_EX_remove_speed") ?: return
                    val sOHBNRSObj = scoreboard.getObjective("self_over_hp_buff_NS_remove_speed") ?: return
                    val sOHBPRSObj = scoreboard.getObjective("self_over_hp_buff_PS_remove_speed") ?: return
                    val sOHBSRSObj = scoreboard.getObjective("self_over_hp_buff_SS_remove_speed") ?: return
                    val oOHBURSObj = scoreboard.getObjective("other_over_hp_buff_ULT_remove_speed") ?: return
                    val oOHBERSObj = scoreboard.getObjective("other_over_hp_buff_EX_remove_speed") ?: return
                    val oOHBNRSObj = scoreboard.getObjective("other_over_hp_buff_NS_remove_speed") ?: return
                    val oOHBPRSObj = scoreboard.getObjective("other_over_hp_buff_PS_remove_speed") ?: return
                    val oOHBSRSObj = scoreboard.getObjective("other_over_hp_buff_SS_remove_speed") ?: return
                    val sOHBUScore = sOHBUObj.getScore(player.name)
                    val sOHBEScore = sOHBEObj.getScore(player.name)
                    val sOHBNScore = sOHBNObj.getScore(player.name)
                    val sOHBPScore = sOHBPObj.getScore(player.name)
                    val sOHBSScore = sOHBSObj.getScore(player.name)
                    val oOHBUScore = oOHBUObj.getScore(player.name)
                    val oOHBEScore = oOHBEObj.getScore(player.name)
                    val oOHBNScore = oOHBNObj.getScore(player.name)
                    val oOHBPScore = oOHBPObj.getScore(player.name)
                    val oOHBSScore = oOHBSObj.getScore(player.name)
                    val tSOHBUScore = tSOHBUObj.getScore(player.name).score
                    val tSOHBEScore = tSOHBEObj.getScore(player.name).score
                    val tSOHBNScore = tSOHBNObj.getScore(player.name).score
                    val tSOHBPScore = tSOHBPObj.getScore(player.name).score
                    val tSOHBSScore = tSOHBSObj.getScore(player.name).score
                    val tOOHBUScore = tOOHBUObj.getScore(player.name).score
                    val tOOHBEScore = tOOHBEObj.getScore(player.name).score
                    val tOOHBNScore = tOOHBNObj.getScore(player.name).score
                    val tOOHBPScore = tOOHBPObj.getScore(player.name).score
                    val tOOHBSScore = tOOHBSObj.getScore(player.name).score
                    val sOHBURSScore = sOHBURSObj.getScore(player.name).score
                    val sOHBERSScore = sOHBERSObj.getScore(player.name).score
                    val sOHBNRSScore = sOHBNRSObj.getScore(player.name).score
                    val sOHBPRSScore = sOHBPRSObj.getScore(player.name).score
                    val sOHBSRSScore = sOHBSRSObj.getScore(player.name).score
                    val oOHBURSScore = oOHBURSObj.getScore(player.name).score
                    val oOHBERSScore = oOHBERSObj.getScore(player.name).score
                    val oOHBNRSScore = oOHBNRSObj.getScore(player.name).score
                    val oOHBPRSScore = oOHBPRSObj.getScore(player.name).score
                    val oOHBSRSScore = oOHBSRSObj.getScore(player.name).score

                    fun safeDivide(numerator: Int, denominator: Int): Double {
                        // 分母が0なら安全に0.0を返す
                        return if (denominator == 0) 0.0 else numerator.toDouble() / denominator
                    }

                    fun safeRound(value: Double): Int {
                        // NaNやInfinityなら0を返す
                        return if (value.isNaN() || value.isInfinite()) 0 else value.roundToInt()
                    }

                    // ↓ここから修正版
                    val tSOHBUScoreD = safeDivide(tSOHBUScore, sOHBURSScore)
                    val tSOHBEScoreD = safeDivide(tSOHBEScore, sOHBERSScore)
                    val tSOHBNScoreD = safeDivide(tSOHBNScore, sOHBNRSScore)
                    val tSOHBPScoreD = safeDivide(tSOHBPScore, sOHBPRSScore)
                    val tSOHBSScoreD = safeDivide(tSOHBSScore, sOHBSRSScore)

                    val tOOHBUScoreD = safeDivide(tOOHBUScore, oOHBURSScore)
                    val tOOHBEScoreD = safeDivide(tOOHBEScore, oOHBERSScore)
                    val tOOHBNScoreD = safeDivide(tOOHBNScore, oOHBNRSScore)
                    val tOOHBPScoreD = safeDivide(tOOHBPScore, oOHBPRSScore)
                    val tOOHBSScoreD = safeDivide(tOOHBSScore, oOHBSRSScore)

                    // ↓安全にスコアへ反映
                    sOHBUScore.score = safeRound(tSOHBUScoreD)
                    sOHBEScore.score = safeRound(tSOHBEScoreD)
                    sOHBNScore.score = safeRound(tSOHBNScoreD)
                    sOHBPScore.score = safeRound(tSOHBPScoreD)
                    sOHBSScore.score = safeRound(tSOHBSScoreD)

                    oOHBUScore.score = safeRound(tOOHBUScoreD)
                    oOHBEScore.score = safeRound(tOOHBEScoreD)
                    oOHBNScore.score = safeRound(tOOHBNScoreD)
                    oOHBPScore.score = safeRound(tOOHBPScoreD)
                    oOHBSScore.score = safeRound(tOOHBSScoreD)


                    // オーバーHP全部入れる.
                    val overHpNames = listOf(
                        "timer_self_over_hp_buff_ULT",
                        "timer_self_over_hp_buff_EX",
                    "timer_self_over_hp_buff_NS",
                    "timer_self_over_hp_buff_PS",
                        "timer_self_over_hp_buff_SS",
                        "timer_other_over_hp_buff_ULT",
                        "timer_other_over_hp_buff_EX",
                        "timer_other_over_hp_buff_NS",
                        "timer_other_over_hp_buff_PS",
                   "timer_other_over_hp_buff_SS"
                    )

                    // 実際に存在し、スコアが1以上のものだけを抽出.
                    val activeDebuffs = overHpNames.mapNotNull { name ->
                        scoreboard.getObjective(name)?.let { it to it.getScore(player.name) }
                    }.filter { (_, score) -> score.score > 0 }

                    //オーバーHPがなかった場合何もしない.
                    if (activeDebuffs.isEmpty()) {
                        continue// returnではなくcontinue！
                    }
                    //オーバーHPがあった場合HP同期処理を実行.
                    plugin.listener.markSync(player)
                }

            }
        }.runTaskTimer(plugin, 0L, 1L) // 1tick毎.
        //SS処理
        object : BukkitRunnable() {
            override fun run() {
                for (player in Bukkit.getOnlinePlayers()) {
                    val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
                    val fanSaObj = scoreboard.getObjective("fan_service") ?: return
                    val fanSaScore = fanSaObj.getScore(player.name)

                    if(fanSaScore.score >= 3){

                        player.sendMessage("§dSS発動！")
                        fanSaScore.score = 0

                    }
                    else{
                        return
                    }

                }
            }
        }.runTaskTimer(plugin, 0L, 1L) // 1tick毎



        //ここからウルトコスト増加
        object : BukkitRunnable() {
            override fun run() {
                for (player in Bukkit.getOnlinePlayers()) {
                    val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
                    val ultObj = scoreboard.getObjective("ult") ?: return
                    val ultUse1Obj = scoreboard.getObjective("ult_use_1") ?: return
                    val ultUse2Obj = scoreboard.getObjective("ult_use_2") ?: return

                    val ultScore = ultObj.getScore(player.name)
                    val ultUse1Score = ultUse1Obj.getScore(player.name).score
                    val ultUse2Score = ultUse2Obj.getScore(player.name).score
                    if(ultUse1Score <= ultUse2Score){
                        if(ultScore.score < ultUse2Score)
                            ultScore.score += 1
                    }
                    if(ultUse2Score < ultUse1Score){
                        if(ultScore.score < ultUse1Score)
                            ultScore.score += 1
                    }

                }

            }
        }.runTaskTimer(plugin, 0L, 20L) // 1秒毎.

        //ここからコスト回復処理
        object : BukkitRunnable() {
            override fun run() {
                for (player in Bukkit.getOnlinePlayers()) {
                    val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
                    val costObj = scoreboard.getObjective("cost") ?: return
                    val costIntervalObj = scoreboard.getObjective("cost_interval") ?: return
                    val costIntervalCObj = scoreboard.getObjective("cost_interval_count") ?: return

                    val costScore = costObj.getScore(player.name)
                    val costIntervalScore = costIntervalObj.getScore(player.name).score
                    val costIntervalCScore = costIntervalCObj.getScore(player.name)



                    if(costScore.score < 100){

                        costIntervalCScore.score -= 1

                        if(costIntervalCScore.score <= 0) {

                            costScore.score += 1
                            costIntervalCScore.score = costIntervalScore / 100
                        }

                    }

                }

            }
        }.runTaskTimer(plugin, 0L, 1L) // 1tick毎.





    }
}
