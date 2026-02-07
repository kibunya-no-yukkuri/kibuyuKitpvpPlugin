package me.kibunya_no_yukku.kibuyu_kitpvp_plugin


import me.kibunya_no_yukku.kibuyu_kitpvp_plugin.Kibuyu_kitpvp_plugin.Companion.shieldMap
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.RayTraceResult
import java.util.*
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

class SkillListener(private val plugin: Kibuyu_kitpvp_plugin) : Listener {

    @EventHandler
    fun onClickIronIngot(event: PlayerInteractEvent) {
        val player = event.player

        // メインハンドクリックのみ対応
        if (event.hand != EquipmentSlot.HAND) return

        // アイテムが鉄インゴットかチェック
        val item = player.inventory.itemInMainHand
        if (item.type != Material.IRON_INGOT) return

        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val kit1Obj = scoreboard.getObjective("kit1") ?: return
        val kit1Score = kit1Obj.getScore(player.name).score
        val oneCtObj = scoreboard.getObjective("1_ct") ?: return
        val oneCtScore = oneCtObj.getScore(player.name).score
        val costObj = scoreboard.getObjective("cost") ?: return
        val costScore = costObj.getScore(player.name).score
        val costUse11Obj = scoreboard.getObjective("cost_use1_1") ?: return
        val costUse11Score = costUse11Obj.getScore(player.name).score
        val costDownAmountObj = scoreboard.getObjective("costDown_buff_amount") ?: return
        val costDownAmountScore = costDownAmountObj.getScore(player.name)
        val costDownObj = scoreboard.getObjective("costDown_buff") ?: return
        val costDownScore = costDownObj.getScore(player.name)

        val downUseCost11 = costUse11Score - costDownAmountScore.score

        if (costScore >= downUseCost11) {
            if(costDownScore.score >= 1){
                costDownScore.score -= 1
            }
        when (kit1Score) {
            1 -> if (oneCtScore < 1) {
                    kit1Skill1(player)
            } else player.sendMessage("§cクールタイム中・・・")

            2 -> kit2Skill1(player)
            3 -> kit3Skill1(player)
            else -> return
        }
        } else player.sendMessage("§cコストが高すぎます！")
    }

    fun kit1Skill1(player: Player) {
        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val timeObj = scoreboard.getObjective("timer_other_attack_buff_EX") ?: return
        val attackObj = scoreboard.getObjective("other_attack_buff_EX") ?: return
        val ctObj = scoreboard.getObjective("1_ct") ?: return
        val costObj = scoreboard.getObjective("cost") ?: return
        val buffTimeObj = scoreboard.getObjective("add_buff_time") ?: return
        val costUse11Obj = scoreboard.getObjective("cost_use1_1") ?: return
        val team = scoreboard.getEntryTeam(player.name) ?: return // 発動者のチーム
        val ctScore = ctObj.getScore(player.name)
        val costScore = costObj.getScore(player.name)
        val costUse11Score = costUse11Obj.getScore(player.name)
        val costDownAmountObj = scoreboard.getObjective("costDown_buff_amount") ?: return
        val costDownAmountScore = costDownAmountObj.getScore(player.name)
        val costDownObj = scoreboard.getObjective("costDown_buff") ?: return
        val costDownScore = costDownObj.getScore(player.name)
        ctScore.score += 300
        costScore.score -= costUse11Score.score - costDownAmountScore.score
        if(costDownScore.score <= 0){
            costDownScore.score = 0
            costDownAmountScore.score = 0
        }
        //パーティクル&サウンド
        object : BukkitRunnable() {
            var radius = 0.0

            override fun run() {
                radius += 0.5
                kit1Skill1SpawnCircle(player, radius)
                kit1Skill1SpawnCircle3(player, radius)
                if (radius == 0.5){
                    player.world.playSound(
                        player.location,
                        Sound.BLOCK_NOTE_BLOCK_CHIME,
                        1.0f, // 音量
                        1.0f)  // ピッチ
                }
                if (radius == 3.5) {
                    player.world.playSound(
                        player.location,
                        Sound.BLOCK_NOTE_BLOCK_CHIME,
                        1.0f, // 音量
                        1.0f
                    )  // ピッチ
                }
                if (radius >= 5.1) cancel()
            }
        }.runTaskTimer(plugin, 0L, 1L)
        player.world.playSound(
            player.location,
            Sound.BLOCK_BELL_RESONATE,
            1.0f, // 音量
            1.0f  // ピッチ
        )
        object : BukkitRunnable() {
            var y = 0.0
            override fun run() {
                for (i in 0..6) {
                    y += 0.3
                    kit1Skill1SpawnCircle2(player, y)
                    kit1Skill1SpawnCircle4(player, y)
                    if (y >= 2.2) cancel()
                }
            }
        }.runTaskTimer(plugin, 15L, 0L)

        //バフ付与
        object : BukkitRunnable() {
            override fun run() {

                for (other in player.world.players) {

                    // チームが同じか？
                    if (scoreboard.getEntryTeam(other.name) != team) continue

                    // 半径5マス以内か？
                    if (other.location.distance(player.location) <= 5.0) {
                        val timeScore = timeObj.getScore(other.name)
                        val attackScore = attackObj.getScore(other.name)
                        val buffTimeScore = buffTimeObj.getScore(player.name)
                        if (timeScore.score == 0) {
                            attackScore.score = 43
                        }
                        timeScore.score = 300 * (1 + (buffTimeScore.score / 100))
                        other.sendMessage("§e${player.name}「この音と共に希望があらんことを」", "§c攻撃力§eが15秒間+43！")
                        val item = player.inventory.itemInMainHand
                        player.setCooldown(item.type, 20 * 15)
                        //頭上に八分音符もどき
                        spawnEighthNote(other)

                        player.world.playSound(
                            player.location,
                            Sound.BLOCK_BELL_USE,
                            1.0f, // 音量
                            1.0f  // ピッチ
                        )
                    }
                }

                cancel()
            }
        }.runTaskTimer(plugin, 15L, 0L)
    }
    fun kit1Skill1SpawnCircle(player: Player, radius: Double) {
        val loc = player.location
        val world = player.world

        for (i in 0..20) {
            val angle = 2 * Math.PI * i / 20
            val x = cos(angle) * radius
            val z = sin(angle) * radius

            world.spawnParticle(
                Particle.WAX_ON,
                loc.clone().add(x, 0.1, z),
                1,
                0.0, 0.0, 0.0
            )
        }
    }
    fun kit1Skill1SpawnCircle2(player: Player, y: Double) {
        val loc = player.location
        val world = player.world

        for (i in 0..20) {
            val angle = 2 * Math.PI * i / 20
            val x = cos(angle) * 5.0
            val z = sin(angle) * 5.0

            world.spawnParticle(
                Particle.WAX_ON,
                loc.clone().add(x, y, z),
                1,
                0.0, 0.0, 0.0
            )
        }
    }
    fun kit1Skill1SpawnCircle3(player: Player, radius: Double) {
        val loc = player.location
        val world = player.world

        for (i in 0..20) {
            val angle = 3 * Math.PI * i / 20
            val x = cos(angle) * radius
            val z = sin(angle) * radius

            world.spawnParticle(
                Particle.WAX_OFF,
                loc.clone().add(x, 0.1, z),
                1,
                0.0, 0.0, 0.0
            )
        }
    }
    fun kit1Skill1SpawnCircle4(player: Player, y: Double) {
        val loc = player.location
        val world = player.world

        for (i in 0..20) {
            val angle = 3 * Math.PI * i / 20
            val x = cos(angle) * 5.0
            val z = sin(angle) * 5.0

            world.spawnParticle(
                Particle.WAX_OFF,
                loc.clone().add(x, y, z),
                1,
                0.0, 0.0, 0.0
            )
        }
    }
    fun spawnEighthNote(player: Player) {
        val world = player.world
        val base = player.location.clone().add(0.0, 2.4, 0.0)
        val particle = Particle.WAX_OFF

        // ===== ① 音符の玉 =====
        for (i in 0 until 16) {
            val angle = 2 * Math.PI * i / 16
            val x = Math.cos(angle) * 0.15
            val z = Math.sin(angle) * 0.15

            world.spawnParticle(
                particle,
                base.clone().add(x, 0.0, z),
                1, 0.0, 0.0, 0.0, 0.0
            )
        }

        // ===== ② 縦棒 =====
        for (i in 0 until 12) {
            world.spawnParticle(
                particle,
                base.clone().add(0.15, i * 0.08, 0.0),
                1, 0.0, 0.0, 0.0, 0.0
            )
        }

        // ===== ③ 旗 =====
        for (i in 0 until 12) {
            val t = i / 12.0
            val x = 0.15 + Math.sin(t * Math.PI) * 0.25
            val y = 0.9 + t * 0.25

            world.spawnParticle(
                particle,
                base.clone().add(x, y, 0.0),
                1, 0.0, 0.0, 0.0, 0.0
            )
        }
    }
    fun kit2Skill1(player: Player) {
        player.sendMessage("§eスキル発動！")
    }

    fun kit3Skill1(player: Player) {
        player.sendMessage("§eスキル発動！")
    }


    @EventHandler
    fun onClickCopperIngot(event: PlayerInteractEvent) {
        val player = event.player

        // メインハンドクリックのみ対応
        if (event.hand != EquipmentSlot.HAND) return

        // アイテムが銅インゴットかチェック
        val item = player.inventory.itemInMainHand
        if (item.type != Material.COPPER_INGOT) return

        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val kit1Obj = scoreboard.getObjective("kit1") ?: return
        val kit1Score = kit1Obj.getScore(player.name).score
        val oneCtTwoObj = scoreboard.getObjective("1_ct2") ?: return
        val oneCtTwoScore = oneCtTwoObj.getScore(player.name).score
        val costObj = scoreboard.getObjective("cost") ?: return
        val costScore = costObj.getScore(player.name).score
        val costUse12Obj = scoreboard.getObjective("cost_use1_2") ?: return
        val costUse12Score = costUse12Obj.getScore(player.name).score
        val costDownAmountObj = scoreboard.getObjective("costDown_buff_amount") ?: return
        val costDownAmountScore = costDownAmountObj.getScore(player.name)
        val costDownObj = scoreboard.getObjective("costDown_buff") ?: return
        val costDownScore = costDownObj.getScore(player.name)

        val downUseCost11 = costUse12Score - costDownAmountScore.score

        if (costScore >= downUseCost11) {
            if (costDownScore.score >= 1) {
                costDownScore.score -= 1
            }
            when (kit1Score) {
                1 -> if (oneCtTwoScore < 1) {
                    // 左クリック（空中 or ブロック）を検知.
                    if (event.action == Action.LEFT_CLICK_AIR || event.action == Action.LEFT_CLICK_BLOCK) {
                        event.isCancelled = true  // 必要なら通常の左クリック動作をキャンセル.
                        kit1Skill2Self(player)

                    }
                    //右クリ検知.
                    if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {
                        kit1Skill2(player)
                    }
                } else player.sendMessage("§cクールタイム中・・・")

                2 -> kit2Skill2(player)
                3 -> kit3Skill2(player)
                else -> return
            }
        }
    }

    fun kit1Skill2(player: Player) {
        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val healObj = scoreboard.getObjective("heal") ?: return
        val hpObj = scoreboard.getObjective("hp") ?: return
        val oneCtTwoObj = scoreboard.getObjective("1_ct2") ?: return
        val costObj = scoreboard.getObjective("cost") ?: return
        val myTeam = scoreboard.getEntryTeam(player.name) ?: return // 発動者のチーム
        val oneCtTwoScore = oneCtTwoObj.getScore(player.name)
        val costScore = costObj.getScore(player.name)
        val healScore = healObj.getScore(player.name)
        val costUse12Obj = scoreboard.getObjective("cost_use1_2") ?: return
        val costUse12Score = costUse12Obj.getScore(player.name)
        val costDownAmountObj = scoreboard.getObjective("costDown_buff_amount") ?: return
        val costDownAmountScore = costDownAmountObj.getScore(player.name)
        val costDownObj = scoreboard.getObjective("costDown_buff") ?: return
        val costDownScore = costDownObj.getScore(player.name)
        oneCtTwoScore.score += 300
        costScore.score -= costUse12Score.score - costDownAmountScore.score
        if(costDownScore.score <= 0){
            costDownScore.score = 0
            costDownAmountScore.score = 0
        }
        val radius = 5.0

        // 5ブロック以内の同チームプレイヤーを検索（自身は除外）
        val maxDistSq = radius * radius
        val candidates = Bukkit.getOnlinePlayers().filter { p ->
            // 自分じゃない.
            p != player &&
                    // 同じワールド
                    p.world == player.world &&
                    // 半径5ブロック以内
                    p.location.distanceSquared(player.location) <= maxDistSq &&
                    // チームが同じ（味方）
                    scoreboard.getEntryTeam(p.name) == myTeam &&
                    //tag"s"を持っているか.
                    p.scoreboardTags.contains("s")
        }

        if (candidates.isEmpty()) {
            player.sendMessage("§c半径 $radius ブロック以内に味方が見つかりません。")
            costScore.score += costUse12Score.score
            oneCtTwoScore.score -= 290
            return
        }
        // 最も近いプレイヤーを選ぶ
        val nearest = candidates.minByOrNull { it.location.distanceSquared(player.location) }!!


        // ヒール量(6)に治癒力をかけてInt型に治してからHPスコアに反映
        val hpScore = hpObj.getScore(nearest.name)
        val healAmount = 6.0 * (1 + (healScore.score.toDouble() / 100.0))
        hpScore.score += healAmount.roundToInt()

        plugin.listener.markSync(nearest)

        player.sendMessage("§e${nearest.name}のHPを${healAmount.roundToInt()}回復！(現在のHP:${hpScore.score})")
        nearest.sendMessage("§e${player.name} によりHPが${healAmount.roundToInt()}回復！")
        val item = player.inventory.itemInMainHand
        player.setCooldown(item.type, 20 * 15)
        return
    }

    fun kit1Skill2Self(player: Player) {
        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val healObj = scoreboard.getObjective("heal") ?: return
        val hpObj = scoreboard.getObjective("hp") ?: return
        val oneCtTwoObj = scoreboard.getObjective("1_ct2") ?: return
        val costObj = scoreboard.getObjective("cost") ?: return
        val costUse12Obj = scoreboard.getObjective("cost_use1_2") ?: return
        val costUse12Score = costUse12Obj.getScore(player.name)
        val oneCtTwoScore = oneCtTwoObj.getScore(player.name)
        val costScore = costObj.getScore(player.name)
        val healScore = healObj.getScore(player.name)
        val hpScore = hpObj.getScore(player.name)
        val costDownAmountObj = scoreboard.getObjective("costDown_buff_amount") ?: return
        val costDownAmountScore = costDownAmountObj.getScore(player.name)
        val costDownObj = scoreboard.getObjective("costDown_buff") ?: return
        val costDownScore = costDownObj.getScore(player.name)
        //CT&cost処理.
        oneCtTwoScore.score += 300
        costScore.score -= costUse12Score.score - costDownAmountScore.score
        if(costDownScore.score <= 0){
            costDownScore.score = 0
            costDownAmountScore.score = 0
        }

        // hpスコアを +4(治癒力分加算)
        val healAmount = 4.0 * (1 + (healScore.score.toDouble() / 100.0))
        hpScore.score += healAmount.roundToInt()
        //hp同期タスク呼び出し.
        plugin.listener.markSync(player)
        //msg.
        player.sendMessage("§aHPを${healAmount.roundToInt()} 回復！")
        val item = player.inventory.itemInMainHand
        player.setCooldown(item.type, 20 * 15)

        player.world.spawnParticle(
            Particle.HEART,
            player.location,
            50,  // 数
            0.5, 0.5, 0.5, // 広がり
            0.1
        )

        player.world.playSound(
            player.location,
            Sound.ENTITY_PLAYER_ATTACK_SWEEP,
            1.0f,
            1.2f
        )

        return
    }
    fun kit2Skill2(player: Player) {
        player.sendMessage("§aスキル発動！")
    }
    fun kit3Skill2(player: Player) {
        player.sendMessage("§aスキル発動！")
    }

    @EventHandler
    fun onClickGoldIngot(event: PlayerInteractEvent) {
        val player = event.player

        // メインハンドクリックのみ対応
        if (event.hand != EquipmentSlot.HAND) return

        // アイテムが金インゴットかチェック
        val item = player.inventory.itemInMainHand
        if (item.type != Material.GOLD_INGOT) return

        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val kit2Obj = scoreboard.getObjective("kit2") ?: return
        val kit2Score = kit2Obj.getScore(player.name).score
        val twoCtOneObj = scoreboard.getObjective("2_ct1") ?: return
        val twoCtOneScore = twoCtOneObj.getScore(player.name).score
        val costObj = scoreboard.getObjective("cost") ?: return
        val costScore = costObj.getScore(player.name).score
        val costUse21Obj = scoreboard.getObjective("cost_use2_1") ?: return
        val costUse21Score = costUse21Obj.getScore(player.name).score
        val costDownAmountObj = scoreboard.getObjective("costDown_buff_amount") ?: return
        val costDownAmountScore = costDownAmountObj.getScore(player.name)
        val costDownObj = scoreboard.getObjective("costDown_buff") ?: return
        val costDownScore = costDownObj.getScore(player.name)

        val downUseCost11 = costUse21Score - costDownAmountScore.score

        if (costScore >= downUseCost11) {
            if (costDownScore.score >= 1) {
                costDownScore.score -= 1
            }

            when (kit2Score) {
                1 -> if (twoCtOneScore < 1) {
                    if (costScore > costUse21Score - 1) {
                        kit021Skill1(player)
                    } else player.sendMessage("§cコストが高すぎます！")
                } else player.sendMessage("§cクールタイム中・・・")

                2 -> kit2Skill2(player)
                3 -> kit3Skill2(player)
                else -> return
            }
        }
    }
    fun kit021Skill1(player: Player) {
        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val healObj = scoreboard.getObjective("heal") ?: return
        val twoCtOneObj = scoreboard.getObjective("2_ct1") ?: return
        val costObj = scoreboard.getObjective("cost") ?: return
        val buffTimeObj = scoreboard.getObjective("add_buff_time") ?: return
        val shieldObj = scoreboard.getObjective("shield") ?: return
        val shieldTimeObj = scoreboard.getObjective("shield_time") ?: return
        val twoCtOneScore = twoCtOneObj.getScore(player.name)
        val costScore = costObj.getScore(player.name)
        val healScore = healObj.getScore(player.name)
        val buffTimeScore = buffTimeObj.getScore(player.name)
        val shieldScore = shieldObj.getScore(player.name)
        val shieldTimeScore = shieldTimeObj.getScore(player.name)
        val costUse21Obj = scoreboard.getObjective("cost_use2_1") ?: return
        val costUse21Score = costUse21Obj.getScore(player.name).score
        val costDownAmountObj = scoreboard.getObjective("costDown_buff_amount") ?: return
        val costDownAmountScore = costDownAmountObj.getScore(player.name)
        val costDownObj = scoreboard.getObjective("costDown_buff") ?: return
        val costDownScore = costDownObj.getScore(player.name)
        //CT&cost処理.
        twoCtOneScore.score += 200
        costScore.score -= costUse21Score - costDownAmountScore.score
        if(costDownScore.score <= 0){
            costDownScore.score = 0
            costDownAmountScore.score = 0
        }

        //Double型でシールド6に治癒力の倍率をかける
        val shieldAmount: Double = 6.0 * (1 + (healScore.score.toDouble() / 100.0))
        //四捨五入してint型に
        val shieldScoreValue: Int = shieldAmount.roundToInt()
        //int型にしたからスコアにそのまま代入できる
        shieldScore.score = shieldScoreValue

        //Double型でシールド効果時間にバフ持続時間の倍率をかける
        val shieldTimeAmount: Double = 400.0 * (1 + (buffTimeScore.score / 100))
        //四捨五入してint型に
        val shieldTimeScoreValue: Int = shieldTimeAmount.roundToInt()
        //int型にしたからスコアにそのまま代入できる
        shieldTimeScore.score = shieldTimeScoreValue

        //デバフ解除処理.
        deBuffRemove(player, 1)

        //クールダウンを視覚化.
        val item = player.inventory.itemInMainHand
        player.setCooldown(item.type, 20 * 10)
        return
    }




    @EventHandler
    fun onClickNetheriteIngot(event: PlayerInteractEvent) {
        val player = event.player

        // メインハンドクリックのみ対応
        if (event.hand != EquipmentSlot.HAND) return

        // アイテムがネザライトインゴットかチェック
        val item = player.inventory.itemInMainHand
        if (item.type != Material.NETHERITE_INGOT) return

        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard

        // 必要な Objective を取得。
        val kit2Obj = scoreboard.getObjective("kit2") ?: return
        val twoCtTwoObj = scoreboard.getObjective("2_ct2") ?: return
        val costObj = scoreboard.getObjective("cost") ?: return
        val costUse22Obj = scoreboard.getObjective("cost_use2_2") ?: return


        // スコア値を取得（0がデフォ）
        val kit2Score = kit2Obj.getScore(player.name).score
        val twoCtTwoScore = twoCtTwoObj.getScore(player.name).score
        val costScore = costObj.getScore(player.name).score
        val costUse22Score = costUse22Obj.getScore(player.name).score

        val costDownAmountObj = scoreboard.getObjective("costDown_buff_amount") ?: return
        val costDownAmountScore = costDownAmountObj.getScore(player.name)
        val costDownObj = scoreboard.getObjective("costDown_buff") ?: return
        val costDownScore = costDownObj.getScore(player.name)

        val downUseCost11 = costUse22Score - costDownAmountScore.score

        if (costScore >= downUseCost11) {
            if (costDownScore.score >= 1) {
                costDownScore.score -= 1
            }

            when (kit2Score) {
                1 -> {
                    if (twoCtTwoScore < 1) {
                        if (costScore >= costUse22Score) {
                            kit021Skill2(player)
                        } else {
                            player.sendMessage("§cコストが高すぎます！(cost=$costScore, need=$costUse22Score)")
                        }
                    } else {
                        player.sendMessage("§cクールタイム中・・・(ct=$twoCtTwoScore)")
                    }
                }

                2 -> {
                    player.sendMessage("§7[DEBUG] kit2 == 2 -> kit2Skill2")
                    kit2Skill2(player)
                }

                3 -> {
                    player.sendMessage("§7[DEBUG] kit2 == 3 -> kit3Skill2")
                    kit3Skill2(player)
                }

                else -> {
                    player.sendMessage("§7[DEBUG] kit2 not 1/2/3 (kit2=$kit2Score)")
                }
            }
        }
    }

    fun kit021Skill2(player: Player){
        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val healObj = scoreboard.getObjective("heal") ?: return
        val healScore = healObj.getScore(player.name)
        val hpObj = scoreboard.getObjective("hp") ?: return
        val playerHpScore = hpObj.getScore(player.name)
        val hpMaxObj = scoreboard.getObjective("max_hp") ?: return
        val playerHpMaxScore = hpMaxObj.getScore(player.name)
        val costUse22Obj = scoreboard.getObjective("cost_use2_2") ?: return
        val costUse22Score = costUse22Obj.getScore(player.name).score
        val twoCtTwoObj = scoreboard.getObjective("2_ct2") ?: return
        val costObj = scoreboard.getObjective("cost") ?: return
        val twoCtTwoScore = twoCtTwoObj.getScore(player.name)
        val costScore = costObj.getScore(player.name)
        val fanSaObj = scoreboard.getObjective("fan_service") ?: return
        val fanSaScore = fanSaObj.getScore(player.name)
        val costDownAmountObj = scoreboard.getObjective("costDown_buff_amount") ?: return
        val costDownAmountScore = costDownAmountObj.getScore(player.name)
        val costDownObj = scoreboard.getObjective("costDown_buff") ?: return
        val costDownScore = costDownObj.getScore(player.name)
        val timerSelfHpBuffObj = scoreboard.getObjective("timer_self_over_hp_buff_EX") ?: return
        val removeSelfHpBuffObj = scoreboard.getObjective("self_over_hp_buff_EX_remove_speed") ?: return
        //CT&cost処理.
        twoCtTwoScore.score += 100
        costScore.score -= costUse22Score - costDownAmountScore.score
        if(costDownScore.score <= 0){
            costDownScore.score = 0
            costDownAmountScore.score = 0
        }


        //ファンサ処理
        fanSaScore.score += 1
        player.sendMessage("§dファンサービスを獲得(現在の数:${fanSaScore.score})")
        if(fanSaScore.score >= 3){
            fanSaScore.score = 0
            costDownAmountScore.score = 10
            costDownScore.score = 1
            player.sendMessage("§dファンサービスを3つ消費してスキルコストを10減少！")
        }

        // 回復量計算
        val playerHealAmount = 5.0 * (1 + (healScore.score.toDouble() / 100.0))
        //プレイヤーのHPに回復量を+
        playerHpScore.score += playerHealAmount.roundToInt()

        player.sendMessage("§e自身のHPを${playerHealAmount.roundToInt()} 回復！")

        if(playerHpScore.score > playerHpMaxScore.score){

            var playerHpAmount = playerHpScore.score
            val playerHpMaxAmount = playerHpMaxScore.score
            playerHpAmount -= playerHpMaxAmount

            val timerSelfHpBuffScore = timerSelfHpBuffObj.getScore(player.name)
            val removeSelfHpBuffScore = removeSelfHpBuffObj.getScore(player.name)

            //100%分増やしたいなら両方同じ数字.
            //両方同じ数字だと1HPがxTickで減るのxがこの値になる.
            removeSelfHpBuffScore.score = 20
            timerSelfHpBuffScore.score = playerHpAmount * 20

            player.sendMessage("§eさらに${playerHealAmount.roundToInt()} のオーバーHPを獲得")

            // 1tick後にオーバーHP分の回復
            Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                playerHpScore.score += playerHpAmount
            }, 1L)

        }

        //hpスコア同期フラグ
        plugin.listener.markSync(player)

        //CT可視化
        val item = player.inventory.itemInMainHand
        player.setCooldown(item.type, 20 * 5)


        // 視線上のプレイヤーを探す
        val target = getTargetTeammate(player, 50.0) ?: return
        //ターゲットのhpスコアを取得
        val targetHpScore = hpObj.getScore(target.name)
        val targetHpMaxScore = hpMaxObj.getScore(target.name)
        // 回復量計算
        val targetHealAmount = 10.0 * (1 + (healScore.score.toDouble() / 100.0))
        //ターゲットのHPに回復量を+
        targetHpScore.score += targetHealAmount.roundToInt()

        target.sendMessage("§e${player.name} によりHPが${targetHealAmount.roundToInt()} 回復！")

        if(targetHpScore.score > targetHpMaxScore.score){

            var targetHpAmount = targetHpScore.score
            val targetHpMaxAmount = targetHpMaxScore.score
            targetHpAmount -= targetHpMaxAmount

            val timerSelfHpBuffScore = timerSelfHpBuffObj.getScore(target.name)
            val removeSelfHpBuffScore = removeSelfHpBuffObj.getScore(target.name)

            //playerHpScore.score -= playerHpAmount


            removeSelfHpBuffScore.score = 10
            timerSelfHpBuffScore.score = targetHpAmount * 10

            target.sendMessage("§eさらに${targetHealAmount.roundToInt()} のオーバーHPを獲得")

            // 1tick後にオーバーHP分の回復
            Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                targetHpScore.score += targetHpAmount
            }, 1L)
        }

        //hpスコア同期フラグ
        plugin.listener.markSync(target)

        player.sendMessage("§e${target.name}のHPを${targetHealAmount.roundToInt()} 回復！(現在のHP:${targetHpScore.score})")
    }




    //視点の先にいる味方をtargetとして取得
    private fun getTargetTeammate(player: Player, range: Double): Player? {
        val world = player.world
        val direction = player.location.direction
        val start = player.eyeLocation

        // RayTraceで視線上のプレイヤーを検出
        val result: RayTraceResult? = world.rayTraceEntities(
            start,
            direction,
            range
        ) { entity ->
            entity is Player && entity != player
        }

        val target = result?.hitEntity as? Player ?: return null

        // 同じチームかチェック（スコアボードチーム使用）
        val playerTeam = player.scoreboard.getEntryTeam(player.name)
        val targetTeam = player.scoreboard.getEntryTeam(target.name)

        if (playerTeam != null && playerTeam == targetTeam) {
            return target
        }

        return null
    }




    fun deBuffRemove(player: Player, amount: Int){
        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard

        // 対象のデバフ名一覧（必要に応じて追加）
        val debuffNames = listOf(
            "timer_self_defense_debuff_EX",
            "timer_self_defense_debuff_NS",
            "timer_self_defense_debuff_PS",
            "timer_self_defense_debuff_SS",
            "timer_other_defense_debuff_EX",
            "timer_other_defense_debuff_NS",
            "timer_other_defense_debuff_PS",
            "timer_other_defense_debuff_SS"
        )

        // 実際に存在し、スコアが1以上のものだけを抽出
        val activeDebuffs = debuffNames.mapNotNull { name ->
            scoreboard.getObjective(name)?.let { it to it.getScore(player.name) }
        }.filter { (_, score) -> score.score > 0 }

        if (activeDebuffs.isEmpty()) {
            //もしデバフがなかったら
            player.sendMessage("§7解除できるデバフがありません。")
            return
        }

        // ランダムに指定数だけ選択（上限を超えないように調整）
        val toClear = activeDebuffs.shuffled().take(amount)

        // 選ばれたデバフを0に
        for ((objective, score) in toClear) {
            score.score = 0
            player.sendMessage("§aデバフ『${objective.name}』が解除されました！")
        }

        // 残りの数が足りなかった場合に通知
        if (toClear.size < amount) {
            player.sendMessage("§e解除可能なデバフは${toClear.size}個しかありませんでした。")
        }
    }


    val taskMap = mutableMapOf<UUID, BukkitRunnable>()

    fun giveShield(player: Player, amount: Int, duration: Long) {

        //タスクキルして上書きしたい.
        taskMap[player.uniqueId]?.cancel()

        shieldMap[player.uniqueId] = amount

        // duration後に消す.
        val task = object : BukkitRunnable() {
            override fun run() {
                shieldMap.remove(player.uniqueId)
            }

        }
        task.runTaskLater(plugin, duration) // 1秒ごと
        taskMap[player.uniqueId] = task
    }
}