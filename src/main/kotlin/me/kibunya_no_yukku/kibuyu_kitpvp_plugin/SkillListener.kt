package me.kibunya_no_yukku.kibuyu_kitpvp_plugin


import me.deecaad.weaponmechanics.WeaponMechanicsAPI
import me.kibunya_no_yukku.kibuyu_kitpvp_plugin.Kibuyu_kitpvp_plugin.Companion.shieldMap
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.entity.Vex
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
import org.bukkit.Location
import org.bukkit.util.Vector
import org.bukkit.FluidCollisionMode
import org.bukkit.NamespacedKey
import org.bukkit.SoundCategory
import org.bukkit.attribute.Attribute
import org.bukkit.entity.BlockDisplay
import org.bukkit.entity.Interaction
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scoreboard.Team
import kotlin.toString

class SkillListener(private val plugin: Kibuyu_kitpvp_plugin) : Listener {

    fun healAmount(healAmount: Double, healScore: Int): Int {
        val finalAmount = healAmount * (1 + (healScore / 100.0))
        return finalAmount.roundToInt()

    }

    fun costAmount(costAmount: Int, costDownScore: Int): Int {
       val finalAmount = costAmount - costDownScore
        return finalAmount
    }

    fun buffTimeAmount( buffTimeAmount: Double, buffTimeScore: Int): Int {
        val finalAmount = buffTimeAmount * (1 + (buffTimeScore / 100.0))

        return finalAmount.roundToInt()

    }



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

        val downUseCost11 = costAmount(costUse11Score,costDownAmountScore.score)

        if (costScore >= downUseCost11) {
            if(costDownScore.score >= 1){
                costDownScore.score -= 1
            }
        when (kit1Score) {
            1 -> if (oneCtScore < 1) {
                    kit1Skill1(player)
            } else player.sendMessage("§cクールタイム中・・・")

            2 ->  if (oneCtScore < 1) {
                    kit2Skill1(player)
            } else player.sendMessage("§cクールタイム中・・・")
            3 ->  if (oneCtScore < 1) {
                kit3Skill1(player)
            } else player.sendMessage("§cクールタイム中・・・")
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
        costScore.score -= costAmount(costUse11Score.score,costDownAmountScore.score)
        if(costDownScore.score <= 0){
            costDownScore.score = 0
            costDownAmountScore.score = 0
        }
        val item = player.inventory.itemInMainHand
        player.setCooldown(item.type, 20 * 15)
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
                        attackScore.score = 43

                        timeScore.score = buffTimeAmount(300.0,buffTimeScore.score)


                        other.sendMessage("§e${player.name}「この音と共に希望があらんことを」", "§c攻撃力§eが15秒間+43！")
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
        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val attackObj = scoreboard.getObjective("self_attack_buff_EX") ?: return
        val attackScore = attackObj.getScore(player.name)
        val speedObj = scoreboard.getObjective("self_speed_buff_EX") ?: return
        val speedScore = speedObj.getScore(player.name)
        val speedDebuffObj = scoreboard.getObjective("self_speed_debuff_EX") ?: return
        val speedDebuffScore = speedDebuffObj.getScore(player.name)
        val attackDebuffObj = scoreboard.getObjective("self_attack_debuff_EX") ?: return
        val attackDebuffScore = attackDebuffObj.getScore(player.name)
        val attackTimeObj = scoreboard.getObjective("timer_self_attack_buff_EX") ?: return
        val attackTimeScore = attackTimeObj.getScore(player.name)
        val speedTimeObj = scoreboard.getObjective("timer_self_speed_buff_EX") ?: return
        val speedTimeScore = speedTimeObj.getScore(player.name)
        val speedDebuffTimeObj = scoreboard.getObjective("timer_self_speed_debuff_EX") ?: return
        val speedDebuffTimeScore = speedDebuffTimeObj.getScore(player.name)
        val attackDebuffTimeObj = scoreboard.getObjective("timer_self_attack_debuff_EX") ?: return
        val attackDebuffTimeScore = attackDebuffTimeObj.getScore(player.name)
        val speedDebuffTimeULTObj = scoreboard.getObjective("timer_self_speed_debuff_ULT") ?: return
        val speedDebuffTimeULTScore = speedDebuffTimeULTObj.getScore(player.name)
        val attackDebuffTimeULTObj = scoreboard.getObjective("timer_self_attack_debuff_ULT") ?: return
        val attackDebuffTimeULTScore = attackDebuffTimeULTObj.getScore(player.name)
        val buffTimeObj = scoreboard.getObjective("add_buff_time") ?: return
        val buffTimeScore = buffTimeObj.getScore(player.name)
        val costDownAmountObj = scoreboard.getObjective("costDown_buff_amount") ?: return
        val costDownAmountScore = costDownAmountObj.getScore(player.name)
        val costDownObj = scoreboard.getObjective("costDown_buff") ?: return
        val costDownScore = costDownObj.getScore(player.name)
        val ctObj = scoreboard.getObjective("1_ct") ?: return
        val ctScore = ctObj.getScore(player.name)
        val costUse11Obj = scoreboard.getObjective("cost_use1_1") ?: return
        val costUse11Score = costUse11Obj.getScore(player.name)
        val costObj = scoreboard.getObjective("cost") ?: return
        val costScore = costObj.getScore(player.name)
        //CT.
        ctScore.score += 300
        costScore.score -= costAmount(costUse11Score.score,costDownAmountScore.score)
        if(costDownScore.score <= 0){
            costDownScore.score = 0
            costDownAmountScore.score = 0
        }
        val item = player.inventory.itemInMainHand
        player.setCooldown(item.type, 20 * 15)

        //エフェクト.
        player.addPotionEffect(PotionEffect(PotionEffectType.SLOWNESS, 30, 4, false, true, true))
        player.playSound(player.location, Sound.BLOCK_WOOD_BREAK, 1.0f, 0f)
        player.playSound(player.location, Sound.BLOCK_WOOD_BREAK, 1.0f, 0f)
        player.playSound(player.location, Sound.BLOCK_WOOD_BREAK, 1.0f, 0f)
        player.playSound(player.location, Sound.BLOCK_WOOD_BREAK, 1.0f, 0f)
        object : BukkitRunnable() {
            var count = 0
            override fun run() {
                if (count >= 3) { // 3回鳴らしたら終了.
                    cancel()
                    return
                }
                player.playSound(player.location, Sound.ENTITY_GENERIC_DRINK, 1.0f, 1.0f)
                count++

                player.world.spawnParticle(
                    Particle.ENTITY_EFFECT,
                    player.location,
                    20,
                    1.0,1.0,1.0,
                    Color.fromRGB(49, 240, 255)
                )
            }
        }.runTaskTimer(plugin, 7L, 6L) // 0L = 最初すぐ, 10L = 0.5秒ごと(20ticks = 1秒).



        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            object : BukkitRunnable() {
                var count = 0
                override fun run() {
                    if (count >= 200) {
                        cancel()
                        return
                    }
                    player.world.spawnParticle(
                        Particle.TRIAL_SPAWNER_DETECTION_OMINOUS,
                        player.location,
                        7,
                        1.0, 1.0, 1.0,
                        0.01
                    )
                    count++
                }
            }.runTaskTimer(plugin, 0L, 1L)
            player.playSound(player.location, Sound.BLOCK_GLASS_BREAK, 1.0f, 0f)
            player.playSound(player.location, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.0f, 2f)
            player.playSound(player.location, Sound.BLOCK_BREWING_STAND_BREW, 1.0f, 1f)
            val dust = Particle.DustOptions(
                Color.fromRGB(49, 240, 255),
                1.5f // サイズ
            )
            player.world.spawnParticle(
                Particle.DUST,
                player.location,
                100,
                1.0, 1.0, 1.0 ,
                dust
            )

        //バフ.
        attackScore.score = 50
        attackTimeScore.score = buffTimeAmount(200.0,buffTimeScore.score)

        speedScore.score = 75
        speedTimeScore.score = buffTimeAmount(200.0,buffTimeScore.score)

        //デバフ解除
        speedDebuffTimeULTScore.score = 0
        attackDebuffTimeULTScore.score = 0
        player.removePotionEffect(PotionEffectType.NAUSEA)

        //バフ後デバフ.
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            player.addPotionEffect(
                PotionEffect(
                    PotionEffectType.NAUSEA,
                    200,
                    0,
                    false, // 環境
                    false, // パーティクル
                    true  // アイコン
                )
            )
        }, 140L)
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            attackDebuffScore.score = 50
            attackDebuffTimeScore.score = buffTimeAmount(300.0,buffTimeScore.score)

            speedDebuffScore.score = 50
            speedDebuffTimeScore.score = buffTimeAmount(300.0,buffTimeScore.score)

            player.addPotionEffect(
                PotionEffect(
                    PotionEffectType.NAUSEA,
                    60,
                    0,
                    false, // 環境
                    false, // パーティクル
                    true  // アイコン
                )
            )
        }, 200L)
        }, 30L)
    }

    fun kit3Skill1(player: Player) {
        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val speedDebuffObj = scoreboard.getObjective("other_speed_debuff_EX") ?: return
        val speedDebuffTimeObj = scoreboard.getObjective("timer_other_speed_debuff_EX") ?: return
        val defenseDebuffObj = scoreboard.getObjective("other_defense_debuff_EX") ?: return
        val defenseDebuffTimeObj = scoreboard.getObjective("timer_other_defense_debuff_EX") ?: return
        val speedDebuffNSObj = scoreboard.getObjective("other_speed_debuff_NS") ?: return
        val speedDebuffTimeNSObj = scoreboard.getObjective("timer_other_speed_debuff_NS") ?: return
        val defenseDebuffNSObj = scoreboard.getObjective("other_defense_debuff_NS") ?: return
        val defenseDebuffTimeNSObj = scoreboard.getObjective("timer_other_defense_debuff_NS") ?: return
        val defenseBuffSSObj = scoreboard.getObjective("self_defense_buff_SS") ?: return
        val defenseBuffTimeSSObj = scoreboard.getObjective("timer_self_defense_buff_SS") ?: return
        val maxHpObj = scoreboard.getObjective("max_hp") ?: return
        val maxHpScore = maxHpObj.getScore(player.name)
        val buffTimeObj = scoreboard.getObjective("add_debuff_time") ?: return
        val debuffTimeScore = buffTimeObj.getScore(player.name)
        val costDownAmountObj = scoreboard.getObjective("costDown_buff_amount") ?: return
        val costDownAmountScore = costDownAmountObj.getScore(player.name)
        val costDownObj = scoreboard.getObjective("costDown_buff") ?: return
        val costDownScore = costDownObj.getScore(player.name)
        val ctObj = scoreboard.getObjective("1_ct") ?: return
        val ctScore = ctObj.getScore(player.name)
        val costUse11Obj = scoreboard.getObjective("cost_use1_1") ?: return
        val costUse11Score = costUse11Obj.getScore(player.name)
        val costObj = scoreboard.getObjective("cost") ?: return
        val costScore = costObj.getScore(player.name)
        //CT.
        ctScore.score += 20
        costScore.score -= costAmount(costUse11Score.score,costDownAmountScore.score)
        if(costDownScore.score <= 0){
            costDownScore.score = 0
            costDownAmountScore.score = 0
        }
        val item = player.inventory.itemInMainHand
        player.setCooldown(item.type, 20 * 1)

        val dir = player.eyeLocation.direction.normalize()

        // 水平成分
        val horizontal = dir.clone().setY(0.0).normalize()

        // 上下の強さ（絶対値）
        val yAbs = kotlin.math.abs(dir.y)

        // 横は上下向くほど弱く
        val horizontalPower = (1.0 * (1 - yAbs * 0.5)).coerceAtLeast(0.2)

        // 上方向は一定（または少しだけ影響させる）
        val upPower = (0.8 + (1 - yAbs) * 0.2).coerceAtLeast(0.6)

        // 適用
        val velocity = horizontal.multiply(horizontalPower).setY(upPower)
        player.velocity = velocity

        player.playSound(player.location, Sound.ITEM_FIRECHARGE_USE, 1.0f, 1.5f)
        player.addPotionEffect(PotionEffect(PotionEffectType.SLOW_FALLING, 30, 0, false, false, true ))
        // 落下フェーズまでの待機（10tick = 0.5秒）
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {

            val dir = player.eyeLocation.direction.normalize()

            // 水平成分だけ取り出す
            val horizontal = dir.clone().setY(0.0).normalize()


            val velocity = horizontal.multiply(0.7).setY(-2.5)
            player.velocity = velocity

            // 着地検知ループ
            object : BukkitRunnable() {
                override fun run() {
                    if (player.isOnGround) {

                        val loc = player.location

                        // 着地エフェクト
                        player.world.spawnParticle(
                            Particle.EXPLOSION,
                            loc,
                            1
                        )

                        player.world.spawnParticle(
                            Particle.BLOCK,
                            loc,
                            50,
                            0.5, 0.1, 0.5,
                            Bukkit.createBlockData(Material.STONE)
                        )

                        player.world.playSound(
                            loc,
                            Sound.ENTITY_GENERIC_EXPLODE,
                            1f,
                            1f
                        )

                        // 周囲ダメージ
                        val radius = 5.0
                        val myTeam = scoreboard.getEntryTeam(player.name)
                        for (entity in player.world.getNearbyEntities(loc, radius, radius, radius)) {
                            val targetTeam = scoreboard.getEntryTeam(entity.name)
                            if (entity is LivingEntity && entity != player && myTeam != targetTeam) {
                                entity.damage(4.0,player)

                                val speedDebuffScore = speedDebuffObj.getScore(entity.name)
                                val speedDebuffTimeScore = speedDebuffTimeObj.getScore(entity.name)
                                val defenseDebuffScore = defenseDebuffObj.getScore(entity.name)
                                val defenseDebuffTimeScore = defenseDebuffTimeObj.getScore(entity.name)

                                speedDebuffScore.score = 25
                                defenseDebuffScore.score = 30

                                speedDebuffTimeScore.score = buffTimeAmount(200.0,debuffTimeScore.score)
                                defenseDebuffTimeScore.score = buffTimeAmount(200.0,debuffTimeScore.score)

                                val pull = loc.toVector().subtract(entity.location.toVector()).normalize()

                                // 距離で強さ変えると自然になる
                                val distance = entity.location.distance(loc)
                                val strength = (2 * (1 - distance / radius)).coerceAtLeast(0.3)

                                entity.velocity = pull.multiply(strength).setY(0.4)


                            }
                        }

                        val points = 60 // 多いほどなめらか

                        for (i in 0 until points) {
                            val angle = 2 * Math.PI * i / points

                            val x = cos(angle) * radius
                            val z = sin(angle) * radius

                            val particleLoc = loc.clone().add(x, 0.1, z)

                            val dust = Particle.DustOptions(Color.fromRGB(157, 132, 237), 1.5f)
                            player.world.spawnParticle(
                                Particle.DUST,
                                particleLoc,
                                1,
                                0.0, 0.0, 0.0,
                                0.0,
                                dust
                            )
                        }

                        if (!player.scoreboardTags.contains("kyugo")){
                            player.addScoreboardTag("kyugo")
                            player.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 100, 1, false, false, true ))
                        }else {
                            val defenseBuffSSScore = defenseBuffSSObj.getScore(player.name)
                            val defenseBuffTimeSSScore = defenseBuffTimeSSObj.getScore(player.name)
                            defenseBuffSSScore.score = 30
                            defenseBuffTimeSSScore.score = buffTimeAmount(200.0,debuffTimeScore.score)
                            cancel()
                            return
                        }


                        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                            player.addPotionEffect(PotionEffect(PotionEffectType.SLOWNESS, 10, 4, false, true, true))
                        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                            player.removeScoreboardTag("kyugo")
                            val loc = player.location.clone().add(0.0, 1.0, 0.0)
                            val myTeam = scoreboard.getEntryTeam(player.name)
                            for (entity in player.world.getNearbyEntities(loc, radius, 1.5, radius)) {
                                val targetTeam = scoreboard.getEntryTeam(entity.name)
                                if (entity is LivingEntity && entity != player && myTeam != targetTeam) {
                                    val damageNS = maxHpScore.score / 10
                                    entity.damage(damageNS.toDouble(),player)

                                    val speedDebuffNSScore = speedDebuffNSObj.getScore(entity.name)
                                    val speedDebuffTimeNSScore = speedDebuffTimeNSObj.getScore(entity.name)
                                    val defenseDebuffNSScore = defenseDebuffNSObj.getScore(entity.name)
                                    val defenseDebuffTimeNSScore = defenseDebuffTimeNSObj.getScore(entity.name)

                                    speedDebuffNSScore.score = 25
                                    defenseDebuffNSScore.score = 30

                                    speedDebuffTimeNSScore.score = buffTimeAmount(200.0,debuffTimeScore.score)
                                    defenseDebuffTimeNSScore.score = buffTimeAmount(200.0,debuffTimeScore.score)

                                    val pull = loc.toVector().subtract(entity.location.toVector()).normalize()

                                    // 距離で強さ変えると自然になる
                                    val distance = entity.location.distance(loc)
                                    val strength = (2 * (1 - distance / radius)).coerceAtLeast(0.3)

                                    entity.velocity = pull.multiply(strength).setY(0.4)

                                }
                            }

                            var r = 0.0
                            repeat(13) {

                                r += 0.3

                                val points = 40
                                val loc = player.location.clone().add(0.0, 1.0, 0.0)

                                for (i in 0 until points) {
                                    val angle = 2 * Math.PI * i / points

                                    val x = cos(angle) * r
                                    val z = sin(angle) * r

                                    val particleLoc = loc.clone().add(x, 0.1, z)
                                    val dust = Particle.DustOptions(Color.fromRGB(157, 132, 237), 1.5f)
                                    player.world.spawnParticle(
                                        Particle.DUST,
                                        particleLoc,
                                        1,
                                        0.0, 0.0, 0.0,
                                        0.0,
                                        dust
                                    )
                                }

                            }

                        }, 10L)
                        }, 90L)
                        cancel()
                    }
                }
            }.runTaskTimer(plugin, 0L, 1L)

        }, 15L)
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

        val downUseCost12 = costAmount(costUse12Score,costDownAmountScore.score)

        if (costScore >= downUseCost12) {
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

                2 -> if (oneCtTwoScore < 1) {
                        kit2Skill2(player)
                }else player.sendMessage("§cクールタイム中・・・")
                3 -> if (oneCtTwoScore < 1) {
                        kit3Skill2(player)
                }else player.sendMessage("§cクールタイム中・・・")
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
        costScore.score -= costAmount(costUse12Score.score,costDownAmountScore.score)
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
        val healAmount = healAmount(6.0,healScore.score)
        hpScore.score += healAmount

        plugin.listener.markSync(nearest)

        player.sendMessage("§e${nearest.name}のHPを${healAmount}回復！(現在のHP:${hpScore.score})")
        nearest.sendMessage("§e${player.name} によりHPが${healAmount}回復！")
        val item = player.inventory.itemInMainHand
        player.setCooldown(item.type, 20 * 15)

        //自身に効果音
        player.world.playSound(
            player.location,
            Sound.ENTITY_PLAYER_ATTACK_SWEEP,
            1.0f,
            1.2f
        )
        //対象にパーティクル.
        nearest.world.spawnParticle(
            Particle.HEART,
            nearest.location,
            50,  // 数
            0.5, 0.5, 0.5, // 広がり
            0.1
        )

        nearest.world.playSound(
            nearest.location,
            Sound.ENTITY_PLAYER_ATTACK_SWEEP,
            1.0f,
            1.2f
        )

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
        costScore.score -= costAmount(costUse12Score.score,costDownAmountScore.score)
        if(costDownScore.score <= 0){
            costDownScore.score = 0
            costDownAmountScore.score = 0
        }

        // hpスコアを +4(治癒力分加算)
        val healAmount = healAmount(4.0,healScore.score)
        hpScore.score += healAmount
        //hp同期タスク呼び出し.
        plugin.listener.markSync(player)
        //msg.
        player.sendMessage("§aHPを${healAmount} 回復！")
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
        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val defenseObj = scoreboard.getObjective("self_defense_buff_EX") ?: return
        val defenseScore = defenseObj.getScore(player.name)
        val defenseDebuffObj = scoreboard.getObjective("self_defense_debuff_EX") ?: return
        val defenseDebuffScore = defenseDebuffObj.getScore(player.name)
        val defenseTimeObj = scoreboard.getObjective("timer_self_defense_buff_EX") ?: return
        val defenseTimeScore = defenseTimeObj.getScore(player.name)
        val defenseDebuffTimeObj = scoreboard.getObjective("timer_self_defense_debuff_EX") ?: return
        val defenseDebuffTimeScore = defenseDebuffTimeObj.getScore(player.name)
        val defenseDebuffTimeULTObj = scoreboard.getObjective("timer_self_defense_debuff_ULT") ?: return
        val defenseDebuffTimeULTScore = defenseDebuffTimeULTObj.getScore(player.name)
        val buffTimeObj = scoreboard.getObjective("add_buff_time") ?: return
        val buffTimeScore = buffTimeObj.getScore(player.name)
        val oneCtTwoObj = scoreboard.getObjective("1_ct2") ?: return
        val oneCtTwoScore = oneCtTwoObj.getScore(player.name)
        val costObj = scoreboard.getObjective("cost") ?: return
        val costScore = costObj.getScore(player.name)
        val costUse12Obj = scoreboard.getObjective("cost_use1_2") ?: return
        val costUse12Score = costUse12Obj.getScore(player.name)
        val costDownAmountObj = scoreboard.getObjective("costDown_buff_amount") ?: return
        val costDownAmountScore = costDownAmountObj.getScore(player.name)
        val costDownObj = scoreboard.getObjective("costDown_buff") ?: return
        val costDownScore = costDownObj.getScore(player.name)
        val ultObj = scoreboard.getObjective("ult") ?: return
        val ultScore = ultObj.getScore(player.name)
        //CT.
        oneCtTwoScore.score += 300
        costScore.score -= costAmount(costUse12Score.score,costDownAmountScore.score)
        if(costDownScore.score <= 0){
            costDownScore.score = 0
            costDownAmountScore.score = 0
        }
        val item = player.inventory.itemInMainHand
        player.setCooldown(item.type, 20 * 15)

        //エフェクト.
        player.playSound(player.location, Sound.BLOCK_ANVIL_USE, 1.0f, 0f)
        player.addPotionEffect(PotionEffect(PotionEffectType.SLOWNESS, 30, 4, false, true, true))


        Bukkit.getScheduler().runTaskLater(plugin, Runnable {

        //バフ.
        defenseScore.score = 100
        defenseTimeScore.score = buffTimeAmount(200.0,buffTimeScore.score)
        //デバフ解除
        defenseDebuffTimeULTScore.score = 0

        //バフ後デバフ.
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            defenseDebuffScore.score = 100
            defenseDebuffTimeScore.score = buffTimeAmount(300.0,buffTimeScore.score)

        }, 200L)

        //ホットバーのどこに剣があるか判定後その場所にアイテムをリプレイス.
        for (slot in 0..8) {
            val item = player.inventory.getItem(slot) ?: continue
            val meta = item.itemMeta as? Damageable ?: continue
            if (!meta.hasItemModel()) continue
            if (meta.itemModel == NamespacedKey.fromString("minecraft:mazikahorikku_sword")) {
                val currentDamage = meta.damage
                val chargeUlt = currentDamage / 25
                ultScore.score += chargeUlt

                Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    "item replace entity ${player.name} container.${slot} with iron_sword[custom_name=\"§b§l脆い§f魔法剣\",minecraft:lore=[\"§r§7使い古された魔法剣\",\"§r§7手入れがされているため、まだ使える\",\"§r§7☆モンスターをうつくしくたおしてあげよう！☆\"],minecraft:attribute_modifiers=[{type:\"attack_speed\",amount:100,operation:\"add_value\",id:\"custom:fast_speed\"},{type:\"attack_damage\",amount:2,operation:\"add_value\",id:\"custom:keep_damage\"}],minecraft:item_model=mazikahorikku_sword,minecraft:blocks_attacks={}]"
                )
                if(currentDamage >= 1){
                    player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 1.0f, 0f,)
                }
            }
        }
        }, 30L)

    }
    fun kit3Skill2(player: Player) {
        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val oneCtTwoObj = scoreboard.getObjective("1_ct2") ?: return
        val oneCtTwoScore = oneCtTwoObj.getScore(player.name)
        val costObj = scoreboard.getObjective("cost") ?: return
        val costScore = costObj.getScore(player.name)
        val costUse12Obj = scoreboard.getObjective("cost_use1_2") ?: return
        val costUse12Score = costUse12Obj.getScore(player.name)
        val costDownAmountObj = scoreboard.getObjective("costDown_buff_amount") ?: return
        val costDownAmountScore = costDownAmountObj.getScore(player.name)
        val costDownObj = scoreboard.getObjective("costDown_buff") ?: return
        val costDownScore = costDownObj.getScore(player.name)
        val team = scoreboard.getEntryTeam(player.name) ?: return
        //CT.
        oneCtTwoScore.score += 200
        costScore.score -= costAmount(costUse12Score.score,costDownAmountScore.score)
        if(costDownScore.score <= 0){
            costDownScore.score = 0
            costDownAmountScore.score = 0
        }
        val itemC = player.inventory.itemInMainHand
        player.setCooldown(itemC.type, 20 * 10)

        var count = 0

        val targetMaterial = Material.BREEZE_ROD // 判定したいアイテム.

        for (item in player.inventory.contents) {
            if (item != null && item.type == targetMaterial) {
                count += item.amount
            }
        }
        if(count <= 30) {
            val item = ItemStack(Material.BREEZE_ROD)
            val meta = item.itemMeta!!

            meta.displayName(
                Component.text("ペンライト").color(NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false)
            )
            meta.lore(listOf(Component.text("NS2で使用する", NamedTextColor.GRAY)))
            item.itemMeta = meta
            repeat(5) {
                player.inventory.addItem(item)
            }
        }

        val loc = player.location.clone().add(0.0, 0.5, 0.0)

        val display = player.world.spawn(loc, BlockDisplay::class.java)
        display.block = Material.NOTE_BLOCK.createBlockData()
        display.isPersistent = false
        display.setRotation(0f, 0f)

        val transform = display.transformation
        transform.translation.set(-0.5f, -0.5f, -0.5f)
        display.transformation = transform

        display.interpolationDuration = 0
        display.interpolationDelay = 0

        // 識別用タグ
        val name = player.name
        display.addScoreboardTag("stage_equipment")
        display.addScoreboardTag("${name}s_stage_equipment")
        team.addEntry(display.uniqueId.toString())
        display.isGlowing = true
        display.world.playSound(
            display.location,
            "minecraft:tomodati_one_step",
            1.0f, // 音量
            1.0f  // ピッチ
        )
        display.world.playSound(
            display.location,
            Sound.BLOCK_WOOD_BREAK,
            1.0f, // 音量
            1.0f  // ピッチ
        )
        rotateDisplay(display)

        val loc2 = player.location
        val interaction = player.world.spawn(loc2, Interaction::class.java)
        interaction.interactionWidth = 1.2f
        interaction.interactionHeight = 1.2f
        interaction.isPersistent = false
        interaction.setRotation(0f, 0f)

        interaction.addScoreboardTag("stage_equipment")
        interaction.addScoreboardTag("${name}s_stage_equipment")
        team.addEntry(interaction.uniqueId.toString())

        interaction.addPassenger(display)

        // 10秒後削除
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            display.remove()
            interaction.remove()
        }, 200L) // 20tick = 1秒 → 200tick = 10秒
    }

    fun rotateDisplay(display: BlockDisplay) {
        object : BukkitRunnable() {
            var yaw = 0f
            var count = 0
            override fun run() {
                if (display.isDead || !display.isValid) {
                    cancel()
                    return
                }
                count += 1
                yaw += 2f // ← 回転速度（小さいほどゆっくり）

                if(count % 10 == 0){
                    display.world.spawnParticle(
                        Particle.NOTE,
                        display.location.clone().add(0.0, 0.7, 0.0),
                        1,
                        0.0, 0.0, 0.0,
                    )
                }
                display.setRotation(yaw, 0f)
            }
        }.runTaskTimer(plugin, 0L, 1L) // 毎tick
    }

    @EventHandler
    fun onClickDisplay(event: PlayerInteractAtEntityEvent) {
        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val player = event.player
        val entity = event.rightClicked

        if (entity !is Interaction) return
        if (!entity.scoreboardTags.contains("stage_equipment")) return
        // 同じチームのみ有効
        val teamE = scoreboard.getEntryTeam(player.name) ?: return
        val teamP = scoreboard.getEntryTeam(entity.uniqueId.toString()) ?: return
        if (teamP != teamE) return

        // バフ付与
        entity.world.spawnParticle(
            Particle.END_ROD,
            entity.location,
            20,
            1.0, 1.0, 1.0,
            0.1
        )
        player.world.playSound(player.location,Sound.ENTITY_ITEM_PICKUP,1.0f, 0.0f)

        val buffTimeObj = scoreboard.getObjective("add_buff_time") ?: return
        val buffTimeScore = buffTimeObj.getScore(player.name)
        val speedTimeObj = scoreboard.getObjective("timer_other_speed_buff_EX") ?: return
        val speedTimeScore = speedTimeObj.getScore(player.name)
        val speedObj = scoreboard.getObjective("other_speed_buff_EX") ?: return
        val speedScore = speedObj.getScore(player.name)

        speedTimeScore.score = buffTimeAmount(200.0,buffTimeScore.score)
        speedScore.score = 50

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

        val downUseCost21 = costAmount(costUse21Score,costDownAmountScore.score)

        if (costScore >= downUseCost21) {
            if (costDownScore.score >= 1) {
                costDownScore.score -= 1
            }

            when (kit2Score) {
                1 -> if (twoCtOneScore < 1) {
                    if (costScore > costUse21Score - 1) {
                        kit021Skill1(player)
                    } else player.sendMessage("§cコストが高すぎます！")
                } else player.sendMessage("§cクールタイム中・・・")

                2 -> if (twoCtOneScore < 1) {
                    if (costScore > costUse21Score - 1) {
                        kit022Skill1(player)
                    } else player.sendMessage("§cコストが高すぎます！")
                } else player.sendMessage("§cクールタイム中・・・")
                3 -> if (twoCtOneScore < 1) {
                    if (costScore > costUse21Score - 1) {
                        kit023Skill1(player)
                    }
                }
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
        costScore.score -= costAmount(costUse21Score,costDownAmountScore.score)
        if(costDownScore.score <= 0){
            costDownScore.score = 0
            costDownAmountScore.score = 0
        }

        //Double型でシールド6に治癒力の倍率をかける
        val shieldAmount = healAmount(6.0,healScore.score)
        //四捨五入してint型に
        val shieldScoreValue: Int = shieldAmount
        //int型にしたからスコアにそのまま代入できる
        shieldScore.score = shieldScoreValue

        //シールド効果時間にバフ持続時間の倍率をかける
        val shieldTimeAmount = buffTimeAmount(400.0,buffTimeScore.score)

        shieldTimeScore.score = shieldTimeAmount

        //デバフ解除処理.
        deBuffRemove(player, 1)

        //クールダウンを視覚化.
        val item = player.inventory.itemInMainHand
        player.setCooldown(item.type, 20 * 10)
        return
    }

    fun kit022Skill1(player: Player) {
        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val shootBuffAmountObj = scoreboard.getObjective("self_shoot_attack_buff_amount_EX") ?: return
        val shootBuffAmountScore = shootBuffAmountObj.getScore(player.name)
        val shootBuffCountObj = scoreboard.getObjective("self_shoot_attack_buff_count_EX") ?: return
        val shootBuffCountScore = shootBuffCountObj.getScore(player.name)
        val shootBuffTimerObj = scoreboard.getObjective("timer_self_shoot_attack_buff_EX") ?: return
        val shootBuffTimerScore = shootBuffTimerObj.getScore(player.name)
        val buffTimeObj = scoreboard.getObjective("add_buff_time") ?: return
        val buffTimeScore = buffTimeObj.getScore(player.name)
        val twoCtOneObj = scoreboard.getObjective("2_ct1") ?: return
        val twoCtOneScore = twoCtOneObj.getScore(player.name)
        val costObj = scoreboard.getObjective("cost") ?: return
        val costScore = costObj.getScore(player.name)
        val costUse21Obj = scoreboard.getObjective("cost_use2_1") ?: return
        val costUse21Score = costUse21Obj.getScore(player.name)
        val costDownAmountObj = scoreboard.getObjective("costDown_buff_amount") ?: return
        val costDownAmountScore = costDownAmountObj.getScore(player.name)
        val costDownObj = scoreboard.getObjective("costDown_buff") ?: return
        val costDownScore = costDownObj.getScore(player.name)
        //CT.
        twoCtOneScore.score += 400
        costScore.score -= costAmount(costUse21Score.score,costDownAmountScore.score)
        if(costDownScore.score <= 0){
            costDownScore.score = 0
            costDownAmountScore.score = 0
        }
        val item = player.inventory.itemInMainHand
        player.setCooldown(item.type, 20 * 20)


        player.playSound(player.location, Sound.ENTITY_BLAZE_AMBIENT, 1.0f, 0.9f)

        val dust = Particle.DustOptions(
            Color.fromRGB(171, 171, 171),
            1f // サイズ
        )
        player.world.spawnParticle(
            Particle.DUST,
            player.location.clone().add(0.0, 1.0, 0.0),
            100,
            1.0, 1.0, 1.0 ,
            dust
        )
        player.world.spawnParticle(
            Particle.END_ROD,
            player.location.clone().add(0.0, 1.0, 0.0),
            20,
            1.0, 1.0, 1.0 ,
            0.1
        )

        //攻撃力を100加算.
        shootBuffAmountScore.score = 100
        //1回まで.
        shootBuffCountScore.score = 1
        //5秒間の間.
        shootBuffTimerScore.score = buffTimeAmount(100.0, buffTimeScore.score)
    }

    fun kit023Skill1(player: Player) {

        val world = player.world
        val loc = player.location

        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val playerTeam: Team? = scoreboard.getEntryTeam(player.name)

        val shield = scoreboard.getObjective("shield") ?: return
        val shieldTime = scoreboard.getObjective("shield_time") ?: return
        val shieldScore = shield.getScore(player.name)
        val shieldTimeScore = shieldTime.getScore(player.name)

        // ヴェックス生成関数
        fun spawnVex(name: String): Vex {
            val vex = world.spawn(loc, Vex::class.java)

            // 名前設定
            vex.customName(Component.text(name, NamedTextColor.LIGHT_PURPLE))
            vex.isCustomNameVisible = true

            // HP設定（最大10）
            vex.getAttribute(Attribute.MAX_HEALTH)?.baseValue = 10.0
            vex.health = 10.0

            // ★召喚者UUIDを保存
            val key = NamespacedKey(plugin, "vex_owner")

            vex.persistentDataContainer.set(
                key,
                PersistentDataType.STRING,
                player.uniqueId.toString()
            )

            // チーム追加（同じチームに所属させる）
            playerTeam?.addEntry(vex.uniqueId.toString())

            return vex
        }

        val vex1 = spawnVex("カボちゃん")
        val vex2 = spawnVex("ランちゃん")

        // 30秒後（20tick * 30 = 600tick）に削除
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            if (!vex1.isDead) {
                vex1.remove()
                shieldScore.score = 5
                shieldTimeScore.score = 200
            }
            if (!vex2.isDead) {
                vex2.remove()
                shieldScore.score = 5
                shieldTimeScore.score = 200
            }
        }, 600L)

        fun startVexAI(vex: Vex, player: Player) {

            val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
            val playerTeam = scoreboard.getEntryTeam(player.name)

            Bukkit.getScheduler().runTaskTimer(plugin, Runnable {

                if (vex.isDead || !vex.isValid) return@Runnable

                // ★ プレイヤーが殴った敵を最優先
                val lastTargetUUID = lastHitMap[player.uniqueId]
                if (lastTargetUUID != null) {
                    val target = Bukkit.getEntity(lastTargetUUID)

                    if (target is LivingEntity && target.isValid && !target.isDead) {
                        vex.target = target
                        return@Runnable
                    }
                }

                val current = vex.target

                // ターゲット維持
                if (current != null && current.isValid && !current.isDead) return@Runnable

                // ===== 通常ターゲット探索 =====
                var nearestEnemy: Player? = null
                var nearestDistance = Double.MAX_VALUE

                for (p in vex.world.players) {

                    if (p.uniqueId == player.uniqueId) continue

                    val team = scoreboard.getEntryTeam(p.name)
                    if (playerTeam != null && team == playerTeam) continue

                    val dist = p.location.distance(vex.location)
                    if (dist < 20 && dist < nearestDistance) {
                        nearestDistance = dist
                        nearestEnemy = p
                    }
                }

                if (nearestEnemy != null) {
                    vex.target = nearestEnemy
                    return@Runnable
                }

                // ===== 追従 =====
                if (vex.location.distance(player.location) > 5) {
                    vex.target = player
                }

            }, 0L, 10L)
        }
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

        val downUseCost11 = costAmount(costUse22Score,costDownAmountScore.score)

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
                    if (twoCtTwoScore < 1) {
                        if (costScore >= costUse22Score) {
                            kit022Skill2(player)
                        } else {
                            player.sendMessage("§cコストが高すぎます！(cost=$costScore, need=$costUse22Score)")
                        }
                    } else {
                        player.sendMessage("§cクールタイム中・・・(ct=$twoCtTwoScore)")
                    }
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
        val timerOtherHpBuffObj = scoreboard.getObjective("timer_other_over_hp_buff_EX") ?: return
        val removeOtherHpBuffObj = scoreboard.getObjective("other_over_hp_buff_EX_remove_speed") ?: return
        //CT&cost処理.
        twoCtTwoScore.score += 160
        costScore.score -= costAmount(costUse22Score,costDownAmountScore.score)
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
        val playerHealAmount = healAmount(5.0,healScore.score)
        //プレイヤーのHPに回復量を+
        playerHpScore.score += playerHealAmount

        player.sendMessage("§e自身のHPを${playerHealAmount} 回復！")

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

            player.sendMessage("§eさらに${playerHpAmount} のオーバーHPを獲得")

            // 1tick後にオーバーHP分の回復
            Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                playerHpScore.score += playerHpAmount
            }, 1L)

        }

        //hpスコア同期フラグ
        plugin.listener.markSync(player)

        //CT可視化
        val item = player.inventory.itemInMainHand
        player.setCooldown(item.type, 20 * 8)

        //音
        Bukkit.dispatchCommand(
            player,
            "playsound minecraft:tomodati_one_step master @a ~ ~ ~"
        )


        // 視線上のプレイヤーを探す
        val target = getTargetTeammate(player, 50.0) ?: return
        //ターゲットのhpスコアを取得
        val targetHpScore = hpObj.getScore(target.name)
        val targetHpMaxScore = hpMaxObj.getScore(target.name)
        // 回復量計算
        val targetHealAmount = healAmount(10.0,healScore.score)
        //ターゲットのHPに回復量を+
        targetHpScore.score += targetHealAmount

        target.sendMessage("§e${player.name} によりHPが${targetHealAmount} 回復！")

        if(targetHpScore.score > targetHpMaxScore.score){

            var targetHpAmount = targetHpScore.score
            val targetHpMaxAmount = targetHpMaxScore.score
            targetHpAmount -= targetHpMaxAmount

            val timerOtherHpBuffScore = timerOtherHpBuffObj.getScore(target.name)
            val removeOtherHpBuffScore = removeOtherHpBuffObj.getScore(target.name)

            //playerHpScore.score -= playerHpAmount


            removeOtherHpBuffScore.score = 20
            timerOtherHpBuffScore.score = targetHpAmount * 20

            target.sendMessage("§eさらに${targetHpAmount} のオーバーHPを獲得")

            // 1tick後にオーバーHP分の回復
            Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                targetHpScore.score += targetHpAmount
            }, 1L)
        }

        //hpスコア同期フラグ
        plugin.listener.markSync(target)

        player.sendMessage("§e${target.name}のHPを${targetHealAmount} 回復！(現在のHP:${targetHpScore.score})")
    }


    fun kit022Skill2(player: Player) {
        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val twoCtTwoObj = scoreboard.getObjective("2_ct2") ?: return
        val twoCtTwoScore = twoCtTwoObj.getScore(player.name)
        val costObj = scoreboard.getObjective("cost") ?: return
        val costScore = costObj.getScore(player.name)
        val costUse22Obj = scoreboard.getObjective("cost_use2_2") ?: return
        val costUse22Score = costUse22Obj.getScore(player.name)
        val costDownAmountObj = scoreboard.getObjective("costDown_buff_amount") ?: return
        val costDownAmountScore = costDownAmountObj.getScore(player.name)
        val costDownObj = scoreboard.getObjective("costDown_buff") ?: return
        val costDownScore = costDownObj.getScore(player.name)
        val myTeam = scoreboard.getEntryTeam(player.name)
        //CT.
        twoCtTwoScore.score += 300
        costScore.score -= costAmount(costUse22Score.score,costDownAmountScore.score)
        if(costDownScore.score <= 0){
            costDownScore.score = 0
            costDownAmountScore.score = 0
        }
        val item = player.inventory.itemInMainHand
        player.setCooldown(item.type, 20 * 15)

        //マーカーカウント.
        var count = 0

        val targetMaterial = Material.REDSTONE_TORCH // 判定したいアイテム

        for (item in player.inventory.contents) {
            if (item != null && item.type == targetMaterial) {
                count += item.amount
            }
        }

        //敵発光.
        if(count >= 1) {
            var nearest: Player? = null
            var nearestDistance = Double.MAX_VALUE

            for (target in player.world.players) {

                if (target == player) continue

                val distance = target.location.distance(player.location)
                if (distance > 10) continue

                val targetTeam = scoreboard.getEntryTeam(target.name)

                // 同じチームならスキップ.
                if (myTeam != null && myTeam == targetTeam) continue

                // 一番近いプレイヤーを更新.
                if (distance < nearestDistance) {
                    nearestDistance = distance
                    nearest = target
                }
            }

            // 見つかった場合のみ発光.
            if(nearest != null){
                for (i in player.inventory.contents.indices) {
                    val item = player.inventory.getItem(i) ?: continue

                    if (item.type == targetMaterial) {
                        if (item.amount > 1) {
                            item.amount -= 1
                        } else {
                            player.inventory.setItem(i, null)
                        }
                    }
                }
            }
            nearest?.addPotionEffect(
                PotionEffect(
                    PotionEffectType.GLOWING,
                    20 * 5,
                    0,
                    false,
                    false
                )
            )
        }

        //グラップル.
        val world = player.world
        val eye = player.eyeLocation
        val direction = eye.direction.normalize()

        val maxDistance = 35.0

        val result = world.rayTrace(
            eye,
            direction,
            maxDistance,
            FluidCollisionMode.NEVER,
            true,   // passableブロック無視（草とか）
            0.5     // エンティティの当たり判定の太さ
        ) { entity ->
            entity != player // 自分は除外
        }

        val hit = result?.hitEntity != null || result?.hitBlock != null

        val hitLocation = when {
            result?.hitEntity != null -> result.hitEntity!!.location
            result?.hitBlock != null -> result.hitPosition.toLocation(world)
            else -> eye.clone().add(direction.clone().multiply(maxDistance))
        }

        val safeLocation = hitLocation.clone().subtract(direction.clone().multiply(0.1))

        // 距離を計算
        val distance = eye.distance(safeLocation)

        player.playSound(player.location, Sound.BLOCK_DISPENSER_LAUNCH, 1.0f, 1.2f)
        player.playSound(player.location, Sound.ITEM_CROSSBOW_QUICK_CHARGE_1, 1.0f, 1.0f)

        object : BukkitRunnable() {
            var currentDistance = 0.0
            val step = 0.5
            override fun run() {
                repeat(6) {
                    if (currentDistance >= distance) {
                        if (hit) {
                            val power = getPower(distance)
                            val velocity = direction.clone().multiply(power)
                            velocity.y += 0.4
                            player.velocity = velocity

                            player.playSound(player.location, Sound.ITEM_FIRECHARGE_USE, 1.0f, 1.5f)

                            result.let {
                                if (it.hitEntity is Player) {
                                    val target = result.hitEntity as Player
                                    val targetTeam = scoreboard.getEntryTeam(target.name)
                                    if (myTeam != null && myTeam != targetTeam) {
                                        target.damage(6.0, player)
                                    }
                                }
                            }
                        }
                        cancel()
                        return
                    }

                    val point = eye.clone().add(direction.clone().multiply(currentDistance))

                    world.spawnParticle(
                        Particle.END_ROD,
                        point,
                        1,
                        0.0, 0.0, 0.0,
                        0.01
                    )

                    currentDistance += step
                }
            }
        }.runTaskTimer(plugin, 0L, 1L) // 1tickごと（0.05秒）
    }

fun getPower(distance: Double): Double {
    val minPower = 0.5
    val maxPower = 4.0
    val maxDistance = 35.0

    return (distance / maxDistance) * (maxPower - minPower) + minPower
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
            "timer_other_defense_debuff_ULT",
            "timer_other_defense_debuff_EX",
            "timer_other_defense_debuff_NS",
            "timer_other_defense_debuff_PS",
            "timer_other_defense_debuff_SS",
            "timer_other_attack_debuff_ULT",
            "timer_other_attack_debuff_EX",
            "timer_other_attack_debuff_NS",
            "timer_other_attack_debuff_PS",
            "timer_other_attack_debuff_SS",
            "timer_other_speed_debuff_ULT",
            "timer_other_speed_debuff_EX",
            "timer_other_speed_debuff_NS",
            "timer_other_speed_debuff_PS",
            "timer_other_speed_debuff_SS",
            "timer_self_defense_debuff_ULT",
            "timer_self_defense_debuff_EX",
            "timer_self_defense_debuff_NS",
            "timer_self_defense_debuff_PS",
            "timer_self_defense_debuff_SS",
            "timer_self_attack_debuff_ULT",
            "timer_self_attack_debuff_EX",
            "timer_self_attack_debuff_NS",
            "timer_self_attack_debuff_PS",
            "timer_self_attack_debuff_SS",
            "timer_self_speed_debuff_ULT",
            "timer_self_speed_debuff_EX",
            "timer_self_speed_debuff_NS",
            "timer_self_speed_debuff_PS",
            "timer_self_speed_debuff_SS"
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
                val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
                val shieldObj = scoreboard.getObjective("shield") ?: return
                val shieldScore = shieldObj.getScore(player.name)
                if (shieldScore.score > 0) {
                    player.world.playSound(
                        player.location,
                        Sound.BLOCK_GLASS_BREAK,
                        1.0f, // 音量
                        1.0f  // ピッチ
                    )
                }
            }

        }
        task.runTaskLater(plugin, duration) // 1秒ごと
        taskMap[player.uniqueId] = task
    }

    //ここからウルト
    @EventHandler
    fun onClickDiamond(event: PlayerInteractEvent) {
        val player = event.player

        // メインハンドクリックのみ対応
        if (event.hand != EquipmentSlot.HAND) return

        // アイテムがダイヤかチェック
        val item = player.inventory.itemInMainHand
        if (item.type != Material.DIAMOND) return

        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val kit1Obj = scoreboard.getObjective("kit1") ?: return
        val kit1Score = kit1Obj.getScore(player.name).score
        val ultObj = scoreboard.getObjective("ult") ?: return
        val ultScore = ultObj.getScore(player.name)
        val ultUse1Obj = scoreboard.getObjective("ult_use_1") ?: return
        val ultUse1Score = ultUse1Obj.getScore(player.name).score
        val ultCt1UObj = scoreboard.getObjective("ult_ct1") ?: return
        val ultCt1Score = ultCt1UObj.getScore(player.name).score

        if (ultScore.score >= ultUse1Score) {
            if(ultCt1Score < 1) {

                ultScore.score -= ultUse1Score
                ultChat(player,kit1Score,1)

                when (kit1Score) {
                    1 -> {
                        // 左クリック（空中 or ブロック）を検知.
                        if (event.action == Action.LEFT_CLICK_AIR || event.action == Action.LEFT_CLICK_BLOCK) {
                            event.isCancelled = true  // 必要なら通常の左クリック動作をキャンセル.
                            kit101Ult1(player)

                        }
                        //右クリ検知.
                        if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {
                            kit101Ult2(player)
                        }
                    }
                    2 -> kit102Ult(player)
                    3 -> kit103Ult(player)
                    else -> return
                }
            }else player.sendMessage("§cクールタイム中")
        } else player.sendMessage("§cULTコストが足りません")
    }

    fun kit101Ult1(player: Player) {
        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val ultCt1Obj = scoreboard.getObjective("ult_ct1") ?: return
        val ultCt1Score = ultCt1Obj.getScore(player.name)
        //CT&cost処理.
        ultCt1Score.score += 500
        //CT可視化
        val item = player.inventory.itemInMainHand
        player.setCooldown(item.type, 20 * 25)

        val world = player.world
        val start = player.eyeLocation
        val direction: Vector = start.direction.normalize()

        val maxDistance = 30.0

        // レイトレースでブロック判定
        val result = world.rayTraceBlocks(
            start,
            direction,
            maxDistance,
            FluidCollisionMode.NEVER,
            true
        )

        val targetLocation: Location = if (result != null) {

            // 当たった座標
            val hitPos = result.hitPosition.toLocation(world)

            // 少し手前にずらす（ブロック内部に入らないように）
            hitPos.subtract(direction.multiply(1.0))

        } else {
            // 何もなければ30m先
            start.clone().add(direction.multiply(maxDistance))
        }

        // 向きは維持
        targetLocation.yaw = player.location.yaw
        targetLocation.pitch = player.location.pitch

        player.teleport(targetLocation)

    }

    fun kit101Ult2(player: Player) {

        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val ultCt2Obj = scoreboard.getObjective("ult_ct2") ?: return
        val ultCt2Score = ultCt2Obj.getScore(player.name)
        val ultObj = scoreboard.getObjective("ult") ?: return
        val ultScore = ultObj.getScore(player.name)
        //CT&cost処理.
        ultCt2Score.score += 500
        //CT可視化
        val item = player.inventory.itemInMainHand
        player.setCooldown(item.type, 20 * 25)

        val myTeam = scoreboard.getEntryTeam(player.name) ?: return // 発動者のチーム

        val radius = 100

        // 100ブロック以内の同チームプレイヤーを検索（自身は除外）
        val maxDistSq = radius * radius
        val candidates = Bukkit.getOnlinePlayers().filter { p ->
            // 自分じゃない.
            p != player &&
                    // 同じワールド
                    p.world == player.world &&
                    // 半径100ブロック以内
                    p.location.distanceSquared(player.location) <= maxDistSq &&
                    // チームが同じ（味方）
                    scoreboard.getEntryTeam(p.name) == myTeam &&
                    //tag"s"を持っているか.
                    p.scoreboardTags.contains("s")
        }

        if (candidates.isEmpty()) {
            player.sendMessage("§c半径 $radius ブロック以内に味方が見つかりません。")
            ultCt2Score.score -= 495
            ultScore.score += 50
            val item = player.inventory.itemInMainHand
            player.setCooldown(item.type, 5)
            return
        }
        // 最も近いプレイヤーを選ぶ
        val nearest = candidates.minByOrNull { it.location.distanceSquared(player.location) }!!

        player.teleport(nearest.location)

    }

    fun kit102Ult(player: Player) {
        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val ultCt2Obj = scoreboard.getObjective("ult_ct2") ?: return
        val ultCt2Score = ultCt2Obj.getScore(player.name)
        val attackObj = scoreboard.getObjective("self_attack_buff_ULT") ?: return
        val attackScore = attackObj.getScore(player.name)
        val defenseObj = scoreboard.getObjective("self_defense_buff_ULT") ?: return
        val defenseScore = defenseObj.getScore(player.name)
        val speedObj = scoreboard.getObjective("self_speed_buff_ULT") ?: return
        val speedScore = speedObj.getScore(player.name)
        val speedDebuffObj = scoreboard.getObjective("self_speed_debuff_ULT") ?: return
        val speedDebuffScore = speedDebuffObj.getScore(player.name)
        val attackDebuffObj = scoreboard.getObjective("self_attack_debuff_ULT") ?: return
        val attackDebuffScore = attackDebuffObj.getScore(player.name)
        val defenseDebuffObj = scoreboard.getObjective("self_defense_debuff_ULT") ?: return
        val defenseDebuffScore = defenseDebuffObj.getScore(player.name)
        val attackTimeObj = scoreboard.getObjective("timer_self_attack_buff_ULT") ?: return
        val attackTimeScore = attackTimeObj.getScore(player.name)
        val defenseTimeObj = scoreboard.getObjective("timer_self_defense_buff_ULT") ?: return
        val defenseTimeScore = defenseTimeObj.getScore(player.name)
        val speedTimeObj = scoreboard.getObjective("timer_self_speed_buff_ULT") ?: return
        val speedTimeScore = speedTimeObj.getScore(player.name)
        val speedDebuffTimeObj = scoreboard.getObjective("timer_self_speed_debuff_ULT") ?: return
        val speedDebuffTimeScore = speedDebuffTimeObj.getScore(player.name)
        val attackDebuffTimeObj = scoreboard.getObjective("timer_self_attack_debuff_ULT") ?: return
        val attackDebuffTimeScore = attackDebuffTimeObj.getScore(player.name)
        val defenseDebuffTimeObj = scoreboard.getObjective("timer_self_defense_debuff_ULT") ?: return
        val defenseDebuffTimeScore = defenseDebuffTimeObj.getScore(player.name)
        val speedDebuffTimeEXObj = scoreboard.getObjective("timer_self_speed_debuff_EX") ?: return
        val speedDebuffTimeEXScore = speedDebuffTimeEXObj.getScore(player.name)
        val attackDebuffTimeEXObj = scoreboard.getObjective("timer_self_attack_debuff_EX") ?: return
        val attackDebuffTimeEXScore = attackDebuffTimeEXObj.getScore(player.name)
        val defenseDebuffTimeEXObj = scoreboard.getObjective("timer_self_defense_debuff_EX") ?: return
        val defenseDebuffTimeEXScore = defenseDebuffTimeEXObj.getScore(player.name)
        val buffTimeObj = scoreboard.getObjective("add_buff_time") ?: return
        val buffTimeScore = buffTimeObj.getScore(player.name)
        val costObj = scoreboard.getObjective("cost") ?: return
        val costScore = costObj.getScore(player.name)
        //CT&cost処理.
        ultCt2Score.score += 40
        //CT可視化
        val item = player.inventory.itemInMainHand
        player.setCooldown(item.type, 20 * 2)

        //エフェクト.
        player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 50.0f, 1f,)
        player.playSound(player.location, Sound.ENTITY_ALLAY_DEATH, 50.0f, 1.5f,)

        object : BukkitRunnable() {
            var count = 0
            override fun run() {
                if (count >= 5) {
                    cancel()
                    return }
                player.world.spawnParticle(Particle.WAX_OFF, player.location, 20, 1.0, 1.0, 1.0, 0.1)
                player.world.spawnParticle(Particle.END_ROD, player.location, 20, 1.0, 1.0, 1.0, 0.1)
                player.world.spawnParticle(Particle.GLOW, player.location, 20, 1.0, 1.0, 1.0, 0.1)
                player.world.spawnParticle(Particle.GLOW_SQUID_INK, player.location, 20, 1.0, 1.0, 1.0, 0.1)
                player.world.spawnParticle(Particle.FLASH, player.location, 3, 0.1, 0.1, 0.1, 0.1)
                count++
            }
        }.runTaskTimer(plugin, 0L, 1L)
        object : BukkitRunnable() {
            var count = 0
            override fun run() {
                if (count >= 300) {
                    cancel()
                    return
                }
                player.world.spawnParticle(
                    Particle.TRIAL_SPAWNER_DETECTION_OMINOUS,
                    player.location,
                    7,
                    1.0, 1.0, 1.0,
                    0.01
                )
                count++
            }
        }.runTaskTimer(plugin, 0L, 1L)

        //バフ.
        attackScore.score = 75
        attackTimeScore.score = buffTimeAmount(300.0,buffTimeScore.score)

        defenseScore.score = 100
        defenseTimeScore.score = buffTimeAmount(300.0,buffTimeScore.score)

        speedScore.score = 100
        speedTimeScore.score = buffTimeAmount(300.0,buffTimeScore.score)

        //デバフ解除
        speedDebuffTimeEXScore.score = 0
        attackDebuffTimeEXScore.score = 0
        defenseDebuffTimeEXScore.score = 0
        player.removePotionEffect(PotionEffectType.NAUSEA)

        //コスト増加.
        costScore.score += 10

        //バフ後デバフ.
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            player.addPotionEffect(
                PotionEffect(
                    PotionEffectType.NAUSEA,
                    200,
                    0,
                    false, // 環境
                    false, // パーティクル
                    true  // アイコン
                )
            )
        }, 240L)
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            attackDebuffScore.score = 100
            attackDebuffTimeScore.score = buffTimeAmount(300.0,buffTimeScore.score)

            defenseDebuffScore.score = 100
            defenseDebuffTimeScore.score = buffTimeAmount(300.0,buffTimeScore.score)

            speedDebuffScore.score = 100
            speedDebuffTimeScore.score = buffTimeAmount(300.0,buffTimeScore.score)
        }, 300L)

    }

    fun kit103Ult(player: Player) {
        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val ultCt1Obj = scoreboard.getObjective("ult_ct1") ?: return
        val ultCt1Score = ultCt1Obj.getScore(player.name)
        val maxHpObj = scoreboard.getObjective("max_hp") ?: return
        val maxHpScore = maxHpObj.getScore(player.name)
        val hpObj = scoreboard.getObjective("hp") ?: return
        val hpScore = hpObj.getScore(player.name)
        val buffTimeObj = scoreboard.getObjective("add_buff_time") ?: return
        val buffTimeScore = buffTimeObj.getScore(player.name)
        //CT&cost処理.
        ultCt1Score.score += 880
        //CT可視化
        val item = player.inventory.itemInMainHand
        player.setCooldown(item.type, 20 * 44)

        //音.
        player.playSound(player.location, Sound.ITEM_TRIDENT_THUNDER, 10.0f, 2.0f)

        //タグ.
        player.addScoreboardTag("mine_ult")

        //バフ時間延長エフェクト計算.
        val buffTime = buffTimeAmount(200.0, buffTimeScore.score)

        object : BukkitRunnable() {
            var count = 0
            override fun run() {
                if (count >= buffTime) {
                    cancel()
                    player.removeScoreboardTag("mine_ult")
                    return
                }

                //hp割合に応じて耐性エフェクトを付与(hp割合が少ないほどレベルが上がる).
                if (hpScore.score >= (maxHpScore.score / 10 * 7)) {
                    player.addPotionEffect(PotionEffect(PotionEffectType.RESISTANCE, 10, 0, false, false, true ))
                } else if (hpScore.score >= (maxHpScore.score / 10 * 5)) {
                    player.addPotionEffect(PotionEffect(PotionEffectType.RESISTANCE, 10, 1, false, false, true ))
                } else if (hpScore.score >= (maxHpScore.score / 10 * 4)) {
                    player.addPotionEffect(PotionEffect(PotionEffectType.RESISTANCE, 10, 2, false, false, true ))
                } else if (hpScore.score >= (maxHpScore.score / 10 * 2)){
                    player.addPotionEffect(PotionEffect(PotionEffectType.RESISTANCE, 10, 3, false, false, true ))
                } else if (hpScore.score < (maxHpScore.score / 10 * 2)){
                    player.addPotionEffect(PotionEffect(PotionEffectType.RESISTANCE, 10, 4, false, false, true ))
                }

                val dust = Particle.DustOptions(
                    Color.fromRGB(210, 210, 210),
                    1f // サイズ
                )
                player.world.spawnParticle(
                    Particle.DUST,
                    player.location.clone().add(0.0, 1.0, 0.0),
                    25,
                    1.0, 1.0, 1.0 ,
                    dust
                )

                count++

            }
        }.runTaskTimer(plugin, 0L, 2L)


        player.sendMessage("§eウルト発動！")
    }



    @EventHandler
    fun onClickEmerald(event: PlayerInteractEvent) {
        val player = event.player

        // メインハンドクリックのみ対応
        if (event.hand != EquipmentSlot.HAND) return

        // アイテムがエメラルドかチェック
        val item = player.inventory.itemInMainHand
        if (item.type != Material.EMERALD) return

        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val kit2Obj = scoreboard.getObjective("kit2") ?: return
        val kit2Score = kit2Obj.getScore(player.name).score
        val ultObj = scoreboard.getObjective("ult") ?: return
        val ultScore = ultObj.getScore(player.name)
        val ultUse2Obj = scoreboard.getObjective("ult_use_2") ?: return
        val ultUse2Score = ultUse2Obj.getScore(player.name).score
        val ultCt2UObj = scoreboard.getObjective("ult_ct2") ?: return
        val ultCt2Score = ultCt2UObj.getScore(player.name).score

        if (ultScore.score >= ultUse2Score) {
            if(ultCt2Score < 1) {
                ultScore.score -= ultUse2Score
                ultChat(player,kit2Score,2)
                    when (kit2Score) {
                        1 -> kit201Ult(player)
                        2 -> kit202Ult(player)
                        3 -> kit203Ult(player)
                        else -> return
                    }
            }else player.sendMessage("§cクールタイム中")
        } else player.sendMessage("§cULTコストが足りません")
    }

    fun kit201Ult(player: Player) {
        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val healObj = scoreboard.getObjective("heal") ?: return
        val healScore = healObj.getScore(player.name)
        val hpObj = scoreboard.getObjective("hp") ?: return
        val playerHpScore = hpObj.getScore(player.name)
        val hpMaxObj = scoreboard.getObjective("max_hp") ?: return
        val playerHpMaxScore = hpMaxObj.getScore(player.name)
        val ultCt2Obj = scoreboard.getObjective("ult_ct2") ?: return
        val ultCt2Score = ultCt2Obj.getScore(player.name)
        val timerSelfHpBuffObj = scoreboard.getObjective("timer_self_over_hp_buff_ULT") ?: return
        val removeSelfHpBuffObj = scoreboard.getObjective("self_over_hp_buff_ULT_remove_speed") ?: return
        val timerOtherHpBuffObj = scoreboard.getObjective("timer_other_over_hp_buff_ULT") ?: return
        val removeOtherHpBuffObj = scoreboard.getObjective("other_over_hp_buff_ULT_remove_speed") ?: return
        //CT&cost処理.
        ultCt2Score.score += 1000
        //CT可視化
        val item = player.inventory.itemInMainHand
        player.setCooldown(item.type, 20 * 50)

        // 回復量計算
        val playerHealAmount = healAmount(50.0,healScore.score)
        //プレイヤーのHPに回復量を+
        playerHpScore.score += playerHealAmount

        player.sendMessage("§e自身のHPを${playerHealAmount} 回復！")

        if(playerHpScore.score > playerHpMaxScore.score){

            var playerHpAmount = playerHpScore.score
            val playerHpMaxAmount = playerHpMaxScore.score
            playerHpAmount -= playerHpMaxAmount

            val timerSelfHpBuffScore = timerSelfHpBuffObj.getScore(player.name)
            val removeSelfHpBuffScore = removeSelfHpBuffObj.getScore(player.name)

            //100%分増やしたいなら両方同じ数字.
            //両方同じ数字だと1HPがxTickで減るのxがこの値になる.
            removeSelfHpBuffScore.score = 10
            timerSelfHpBuffScore.score = playerHpAmount * 10

            player.sendMessage("§eさらに${playerHpAmount} のオーバーHPを獲得")

            // 1tick後にオーバーHP分の回復
            Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                playerHpScore.score += playerHpAmount
            }, 1L)

        }

        //hpスコア同期フラグ
        plugin.listener.markSync(player)

        //音
        Bukkit.dispatchCommand(
            player,
            "playsound minecraft:tomodati_one_step master @a ~ ~ ~ 5 1"
        )



        for (target in player.world.players) {
            val team = scoreboard.getEntryTeam(player.name) ?: return // 発動者のチーム
            // チームが同じか？
            if (scoreboard.getEntryTeam(target.name) != team) continue
            if (target == player) continue
            // 半径30マス以内か？
            if (target.location.distance(player.location) <= 30.0) {
                val targetHpScore = hpObj.getScore(target.name)
                val targetHpMaxScore = hpMaxObj.getScore(target.name)
                val timerOtherHpBuffScore = timerOtherHpBuffObj.getScore(target.name)
                val removeOtherHpBuffScore = removeOtherHpBuffObj.getScore(target.name)
                // 回復量計算
                val targetHealAmount = healAmount(50.0,healScore.score)
                //ターゲットのHPに回復量を+
                targetHpScore.score += targetHealAmount

                target.sendMessage("§e${player.name} によりHPが${targetHealAmount} 回復！")
                if(targetHpScore.score > targetHpMaxScore.score) {

                    var targetHpAmount = targetHpScore.score
                    val targetHpMaxAmount = targetHpMaxScore.score
                    targetHpAmount -= targetHpMaxAmount


                    removeOtherHpBuffScore.score = 10
                    timerOtherHpBuffScore.score = targetHpAmount * 10

                    target.sendMessage("§eさらに${targetHpAmount} のオーバーHPを獲得")

                    // 1tick後にオーバーHP分の回復
                    Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                        targetHpScore.score += targetHpAmount
                    }, 1L)
                }
            }
        }
    }

    fun kit202Ult(player: Player) {
        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val ultCt2Obj = scoreboard.getObjective("ult_ct2") ?: return
        val ultCt2Score = ultCt2Obj.getScore(player.name)
        //CT&cost処理.
        ultCt2Score.score += 1000
        //CT可視化
        val item = player.inventory.itemInMainHand
        player.setCooldown(item.type, 20 * 50)

        player.playSound(player.location, Sound.ITEM_TRIDENT_THUNDER, 10.0f, 1f)
        player.addPotionEffect(PotionEffect(PotionEffectType.SLOWNESS, 20, 4, false, true, true))

        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
        //ホットバーのどこに剣があるか判定後その場所にアイテムをリプレイス.
        for (slot in 0..8) {
            val item = player.inventory.getItem(slot) ?: continue
            val weaponTitle = WeaponMechanicsAPI.getWeaponTitle(item)
            if (weaponTitle == "sniper") {

                Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    "wm give ${player.name} sniper_ult 1 slot:${slot}"
                )

            }
        }
    }, 20L)
    }

    fun kit203Ult(player: Player) {
        player.sendMessage("§eウルト発動！")
    }

    fun ultChat(player: Player, kit: Int, position: Int){

        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val team = scoreboard.getEntryTeam(player.name)
        val color: TextColor? = team?.color() ?: NamedTextColor.WHITE

        var ult = "バグウルト"
        var name = "バグkit"

        when (position) {
            1 -> when (kit){
                1 -> {ult = "神出鬼没"
                name = "セリナ"}
                2 -> {ult = "変身！"
                    name = "マジカホリック"}
                3 -> {ult = "最後のその瞬間まで！"
                    name = "ミネ"}
                }

            2 -> when (kit) {
                1 -> { ult = "お祈りの時間"
                    name = "マリー" }
                2 -> {ult = "究極の一"
                    name = "スナイパー"}
                }
            }


        Bukkit.broadcast(Component.text("【${player.name}】", color)
            .append(Component.text("ULT≪${ult}≫", NamedTextColor.WHITE))
            .append(Component.text("by${name}", NamedTextColor.GRAY)))
    }
}


val lastHitMap = mutableMapOf<UUID, UUID>()
@EventHandler
fun kit203EX2(e: EntityDamageByEntityEvent) {

    val damager = e.damager
    val victim = e.entity

    if (damager !is Player) return
    if (victim !is LivingEntity) return

    lastHitMap[damager.uniqueId] = victim.uniqueId
}