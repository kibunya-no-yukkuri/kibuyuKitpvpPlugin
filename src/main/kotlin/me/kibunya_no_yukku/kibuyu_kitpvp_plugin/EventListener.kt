package me.kibunya_no_yukku.kibuyu_kitpvp_plugin


import me.deecaad.weaponmechanics.WeaponMechanicsAPI
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
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Vex
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.meta.Damageable
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot
import java.util.UUID

class EventListener(private val plugin: JavaPlugin): Listener {

    private val loreMap: Map<Material, List<List<String>>> = mapOf(
        //listOf("§7", "§7"),
        //listOf("§bEXスキル1", "§7", "§7消費コストcost,CT秒","§bEXスキル2","§7", "§7消費コストcost,CT秒"),
        //listOf("§bノーマルスキル1", "§7","§bノーマルスキル2", "§7") ,
        //listOf("§bパッシブスキル1", "§7","§bパッシブスキル2", "§7"),
        //listOf("§bサブスキル1", "§7","§bサブスキル2", "§7")
        Material.CAKE to listOf(
            listOf("§7ヒール、バフを両方持っているバランスの良いサポーター", "§7生存能力も高くサポーターとして完成している","§7右クリックでページをめくる"),
            listOf("§bEXスキル1「集中治療セットA」", "§7右クリック時、半径5m以内の味方HPを6回復する", "§7左クリック時、自身のHPを4回復する", "§7両者共に消費コストcost30,CT15秒","§bEXスキル2「祝福の響き」","§7半径5m以内の味方の攻撃力を43%加算する(15秒間)","§7消費コストcost60,CT15秒"),
            listOf("§bノーマルスキル1「緊急治療セットB」", "§735秒毎に、HPの最も低い味方に対して2HP回復","§bノーマルスキル2「プレゼントボックスC」", "§7通常攻撃130回毎に、自身の攻撃力を27%加算(10秒間)") ,
            listOf("§bパッシブスキル1「白衣の天使」", "§7自身の治癒力を30増加","§bパッシブスキル2「守護天使の意志」", "§7自身の最大HPを5増加"),
            listOf("§bサブスキル1「天使の微笑み」", "§7味方全員のCC抵抗値を5増加","§bサブスキル2「聖なる加護」", "§7リロード時、自身の移動速度を100%加算(1秒間)","§7CT3秒"),
            listOf("§dアルティメット「神出鬼没」", "§7右クリック時、半径100m以内の一番近い味方にテレポートする","§7左クリック時、視点の30m先にテレポートする", "§7消費ULTコストult50,CT25秒")
        ),
        Material.SUNFLOWER to listOf(
            listOf("§7圧倒的な回復力を持つヒーラー", "§7弱体状態の解除、スキルコスト減少、防御デバフとやれることは意外と多い","§7右クリックでページをめくる"),
            listOf("§bEXスキル1「聖なる加護」", "§7自身に6のシールドを付与(20秒間)","§7さらに弱体状態を1つ解除", "§7消費コストcost30,CT15秒","§bEXスキル2「溢れるハート」","§7視点の先の味方に対して10HP回復し自身に対して5HP回復","§7HPを超えた100%分オーバーHPを付与","§7さらに「ファンサービス」を1個獲得","§7消費コストcost20,CT5秒"),
            listOf("§bノーマルスキル1「浄化の洗礼」", "§725秒毎に、最も近い敵を中心とした円形範囲に5ダメージ","§7さらに防御力を24%減少","§bノーマルスキル2「慈愛の投げキッス」","§730秒毎に最もHPの低い味方一人に対して3HPの回復","§7さらに自身に対して2HPの回復") ,
            listOf("§bパッシブスキル1「応援の心構え」", "§7自身の防御力を5増加","§bパッシブスキル2「たゆまぬ努力」", "§7自身の治癒力を30増加"),
            listOf("§bサブスキル1「慈愛の心」", "§7味方全員のHPを5増加","§bサブスキル2「今だけは楽しんで」", "§7「ファンサービス」を3個獲得時、味方全員のスキルコストを10減少(EXスキルの使用1回分)","§7(「ファンサービス」は初期化されます)"),
            listOf("§dアルティメット「お祈りの時間」", "§7半径30m以内の自身含む味方に対して50HP回復","§7HPを超えた100%分オーバーHPを付与", "§7消費ULTコストult100,CT50秒")
        ),
        // 他のアイテムも追加可能
        Material.STICK to listOf(
            listOf("§7ステータスの高さで敵を殴りまくるアタッカー", "§7スキルによるデバフをウルトで解除し、ウルトによるデバフをスキルで解除する", "§7そんなスキル回しが重要なkit","§7右クリックでページをめくる"),
            listOf("§b武器「§b§l脆い§f魔法剣§b」", "§7攻撃力:2","§7攻撃速度+100", "§b武器スキル「マジカルプロテクト」","§7正面からの攻撃をガードする","§7主な銃撃は25%分のダメージが貫通する"),
            listOf("§bEXスキル1「レアアイテムを無数手にして」", "§7自身の攻撃力を50%、移動速度を75%加算(10秒間)", "§7消費コストcost45,CT15秒","§bEXスキル2「再生！脆い剣を壊しては直し」","§7自身の防御力を100加算(10秒間)","§7さらに魔法剣の耐久値を回復","§7消費コストcost35,CT15秒"),
            listOf("§bノーマルスキル1「手を伸ばしたらリフレイン」", "§7自身のEXスキルによるデバフをアルティメット発動時解除","§7またアルティメットによるデバフをスキル発動時解除する","§bノーマルスキル2「眩しい暗闇の中」","§7EXスキル2発動時、魔法剣の減った耐久値に応じてULTを加算(最大10)") ,
            listOf("§bパッシブスキル1「フル装備を着て」", "§7自身の防御力を50増加","§bパッシブスキル2「未回復でも笑顔を魅せて」", "§7自身HPが10未満の時、CC抵抗値を5加算"),
            listOf("§bサブスキル1「うつくしく倒してあげるから」", "§7敵を倒した時、ULTを40加算","§bサブスキル2「魔法中毒」", "§7EXスキル、アルティメット共に効果時間終了後","§7バフと同種のデバフを自身に付与"),
            listOf("§dアルティメット「変身！」", "§7自身の攻撃力を75%、移動速度を100%、防御力を100加算(15秒間)","§7さらに発動時、コストを10得る", "§7消費ULTコストult75,CT2秒")
        ),
        Material.SPYGLASS to listOf(
            listOf("§7「撃って移動」を繰り返す。", "§7ただそれだけ","§7右クリックでページをめくる"),
            listOf("§b武器「§6AX-50§b」", "§6➣§7 10 §6⟸ §7ダメージ","§6➣§7 5 §6⟸ §7マガジン容量", "§6➣§7 2.25 §6⟸ §7拡散","§6➣§7 250 §6⟸ §7弾速","§6➣§7 -0.001 §6⟸ §7移動速度ボーナス"),
            listOf("§bEXスキル1「深呼吸」", "§7自身の攻撃力を100%分加算(攻撃をするまで)(5秒間)", "§7消費コストcost30,CT20秒","§bEXスキル2「フックショット」","§7視点方向にフックショットを射出(35m)","§7さらに「マーカー」を所持していた場合","§7「マーカー」を一つ消費し","§7使用時、半径10m以内の敵を一人発光させる","§7消費コストcost40,CT15秒"),
            listOf("§bノーマルスキル1「物資補給」", "§750秒毎に自身の防御力を10加算(40秒)","§7また「マーカー」を1つ付与","§bノーマルスキル2「戦地適正」","§760秒毎に攻撃を10%加算(半径15m以内に敵がいない場合)(40秒間)") ,
            listOf("§bパッシブスキル1「急所」", "§7自身の攻撃力を5%加算","§bパッシブスキル2「???」", "§7思いつかない:maow:"),
            listOf("§bサブスキル1「衝撃吸収ブーツ」", "§7落下ダメージを無効化","§bサブスキル2「ポジション変更」", "§7AX-50の攻撃を命中させた際、自身の移動速度を50%加算"),
            listOf("§dアルティメット「究極の一」", "§7威力の高いAX-50ULTIMATEを取り出す","§7一発AX-50ULTIMATEを撃つとAX-50ULTIMATEをしまう", "§7消費ULTコストult100,CT50秒")
        ),
        Material.SHIELD to listOf(
        listOf("§7誇りと信念を胸に刻み、最後のその瞬間まで、戦場に救護の手を！", "§7maow","§7右クリックでページをめくる"),
        listOf("§b武器「§d救護§fの§b証明」", "§6➣§7 0.75 §6⟸ §71ペレットあたりのダメージ", "§6➣§7 8 §6⟸ §71ペレット数","§6➣§7 5 §6⟸ §7装填弾数", "§6➣§7 6.75 §6⟸ §7拡散","§6➣§7 60 §6⟸ §7弾速","§6➣§7 -0.0005 §6⟸ §7移動速度ボーナス"),
        listOf("§bEXスキル1「誇りと信念」", "§7前方に跳躍した後、自身を中心とした", "§7円形範囲内の敵に対して4ダメージ", "§7さらに、対象の移動速度25%と防御力30%を減少し", "§7消費コストcost40,CT1秒","§bEXスキル2「スーパーアイドルランディング」","§7「ペンライト」を5個獲得（「ペンライト」は最大30個まで重複）","§7さらに、舞台装置を召喚する(10秒間)","§7舞台装置を右クリックした味方に対して移動速度を50%加算(10秒間)","§7消費コストcost40,CT10秒"),
        listOf("§bノーマルスキル1「峻厳な宣告」", "§7「救護の心構え」状態の解除後に発動","§7発動時、円形範囲内の敵に対して、最大HPの10%分のダメージ","§7さらに、移動速度25%と防御力30%を減少（10秒間）","§bノーマルスキル2「あなたに決めました」","§730秒毎に、自身が持つ「ペンライト」を10個単位で消耗し","§7次の攻撃時対象の攻撃力を減少（10秒間）","§70個:20%、10個:23%、20個:27%、30個:32%") ,
        listOf("§bパッシブスキル1「騎士団の意志」", "§7自身の防御力を10加算","§bパッシブスキル2「長時間ライブ」", "§7最大HPを20加算"),
        listOf("§bサブスキル1「不屈の勇気」", "§7Exスキルの発動と同時に、「救護の心構え」状態を付与","§7「救護の心構え」中、再生2を付与(5秒間)","§7すでに「救護の心構え」状態の場合、代わりに防御力30を加算(20秒間)","§bサブスキル2「ファンの声援」", "§7味方がフォーカスする毎に「ペンライト」を2個獲得（「ペンライト」は最大30個まで重複）"),
        listOf("§dアルティメット「最後のその瞬間まで！」", "§7自身に耐性エフェクトレベル1～5を付与し続ける(10秒間)","§7(レベルは自身の残りHPが少ないほど高くなります)", "§7消費ULTコストult88,CT44秒")
        ),
        Material.WOODEN_SHOVEL to listOf(
        listOf("§7maow", "§7maow","§7右クリックでページをめくる"),
        listOf("§b武器「§dテラコメット」", "§6➣§7 1 §6⟸ §7ダメージ", "§6➣§7 8 §6⟸ §71ペレット数","§6➣§7 5 §6⟸ §7装填弾数", "§6➣§7 6.75 §6⟸ §7拡散","§6➣§7 60 §6⟸ §7弾速","§6➣§7 -0.0005 §6⟸ §7移動速度ボーナス"),
        listOf("§bEXスキル1「誇りと信念」", "§7前方に跳躍した後、自身を中心とした", "§7円形範囲内の敵に対して4ダメージ", "§7さらに、対象の移動速度25%と防御力30%を減少し", "§7消費コストcost40,CT1秒","§bEXスキル2「スーパーアイドルランディング」","§7「ペンライト」を5個獲得（「ペンライト」は最大30個まで重複）","§7さらに、舞台装置を召喚する(10秒間)","§7舞台装置を右クリックした味方に対して移動速度を50%加算(10秒間)","§7消費コストcost40,CT10秒"),
        listOf("§bノーマルスキル1「峻厳な宣告」", "§7「救護の心構え」状態の解除後に発動","§7発動時、円形範囲内の敵に対して、最大HPの10%分のダメージ","§7さらに、移動速度25%と防御力30%を減少（10秒間）","§bノーマルスキル2「あなたに決めました」","§730秒毎に、自身が持つ「ペンライト」を10個単位で消耗し","§7次の攻撃時対象の攻撃力を減少（10秒間）","§70個:20%、10個:23%、20個:27%、30個:32%") ,
        listOf("§bパッシブスキル1「騎士団の意志」", "§7自身の防御力を10加算","§bパッシブスキル2「長時間ライブ」", "§7最大HPを20加算"),
        listOf("§bサブスキル1「不屈の勇気」", "§7Exスキルの発動と同時に、「救護の心構え」状態を付与","§7「救護の心構え」中、再生2を付与(5秒間)","§7すでに「救護の心構え」状態の場合、代わりに防御力30を加算(20秒間)","§bサブスキル2「ファンの声援」", "§7味方がフォーカスする毎に「ペンライト」を2個獲得（「ペンライト」は最大30個まで重複）"),
        listOf("§dアルティメット「最後のその瞬間まで！」", "§7自身に耐性エフェクトレベル1～5を付与し続ける(10秒間)","§7(レベルは自身の残りHPが少ないほど高くなります)", "§7消費ULTコストult88,CT44秒")
        )


    )

    private val loreKey = NamespacedKey(plugin, "lore_index")


    fun buffTimeAmount( buffTimeAmount: Double, buffTimeScore: Int): Int {
        val finalAmount = buffTimeAmount * (1 + (buffTimeScore / 100.0))

        return finalAmount.roundToInt()

    }


    @EventHandler
    fun blockingMazikahorikkuSword(event: EntityDamageByEntityEvent){

        val player = event.entity as? Player ?: return

        if (player.isBlocking &&
            player.inventory.itemInMainHand.type == Material.IRON_SWORD
        ) {
            player.playSound(player.location, Sound.BLOCK_AMETHYST_BLOCK_PLACE, 1.0f, 1f)
            val item = player.inventory.itemInMainHand
            val meta = item.itemMeta as? Damageable ?: return

            val maxDurability = item.type.maxDurability

            val currentDamage = meta.damage

            val remaining = maxDurability - currentDamage


            if (remaining <= 3) {

                Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    "item replace entity ${player.name} weapon.mainhand with iron_sword[custom_name=\"§b§l脆い§f魔法剣§7(broken)\",minecraft:lore=[\"§r§7使い古され、壊れてしまった魔法剣\",\"§r§7直せばまだ使えるかもしれない\",\"§r§7☆スキルをつかってけんをなおしてあげよう！☆\"],minecraft:attribute_modifiers=[{type:\"attack_speed\",amount:-3,operation:\"add_value\",id:\"custom:fast_speed\"},{type:\"attack_damage\",amount:1,operation:\"add_value\",id:\"custom:keep_damage\"}],minecraft:item_model=mazikahorikku_sword]"
                )
                player.playSound(player.location, Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f)
            }

            // 最大耐久の5%
            val damageAmount = (maxDurability * 0.05).toInt().coerceAtLeast(1)

            meta.damage = (currentDamage + damageAmount)
                .coerceAtMost(maxDurability.toInt())

            item.itemMeta = meta

        }

    }

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


    //攻撃力バフデバフ計算プレイヤーを関数に送ってからスコア取得したほうが楽じゃね.
    fun attackAmount(attackAmount: Double, attackScore: Int, attackDebuffScore: Int): Double {
        val newAttackDebuff = if (attackDebuffScore > 0) {
            attackDebuffScore / (attackDebuffScore + 100.0)
        } else 0.0

        val newAttackAmount = attackAmount *  (1.0 + (attackScore / 100.0))  // 10%増なら 1.1
        val finalAmount = (newAttackAmount * (1 - newAttackDebuff))

        return finalAmount
    }

    //wmの武器でダメージ与えたの検知 → 一個下のやつでおｋ.
    //@EventHandler
    //fun onWeaponDamage(event: WeaponDamageEntityEvent) {
    //    val shooter = event.entity
    //    if (shooter !is Player) return
    //
        // 攻撃力スコアを取得（例：攻撃力はスコアボードの "attack"）
    //    val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
    //    val attackScore = scoreboard.getObjective("attack")?.getScore(shooter.name)?.score ?: 0
    //
    //    // 元のダメージに攻撃力を反映（ここでは +1 ダメージ/1スコア）
    //    val baseDamage = event.baseDamage
    //    val finalDamage = attackAmount(baseDamage,attackScore)
    //    event.baseDamage = finalDamage
    //}

    @EventHandler
    fun onDamage(event: EntityDamageByEntityEvent) {

        val attacker = event.damager

        if (attacker !is Player) return

        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val attackScore = scoreboard.getObjective("attack")?.getScore(attacker.name)?.score ?: 0
        val attackDebuffScore = scoreboard.getObjective("attack_debuff")?.getScore(attacker.name)?.score ?: 0

        val finalDamage = attackAmount(event.damage,attackScore,attackDebuffScore)

        event.damage = finalDamage

        //攻撃時攻撃力バフの残り回数を減少.
        val shootAttackBuffCountObj = scoreboard.getObjective("self_shoot_attack_buff_count_EX")
        val shootAttackBuffCountScore = shootAttackBuffCountObj?.getScore(attacker.name)
        val shootAttackBuffTimerObj = scoreboard.getObjective("timer_self_shoot_attack_buff_EX")
        val shootAttackBuffTimerScore = shootAttackBuffTimerObj?.getScore(attacker.name)

        shootAttackBuffTimerScore?.let {
            if (shootAttackBuffTimerScore.score >= 1) {
                shootAttackBuffCountScore?.let {
                    if (shootAttackBuffCountScore.score >= 1) {
                        shootAttackBuffCountScore.score -= 1
                    }
                }
            }
        }
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

        //player.sendMessage(
        //    "防御: $baseDefense デバフ: $debuffPercent% → 実効防御: $effectiveDefense → 軽減率: ${(reductionRate * 100).toInt()}% → 最終: $newDamage"
        //)


        val shieldObj = scoreboard.getObjective("shield")
        val shieldScore = shieldObj?.getScore(player.name)

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
            shieldScore?.let { it.score = 0 }
            if(shield > 0) {
                player.world.playSound(
                    player.location,
                    Sound.BLOCK_GLASS_BREAK,
                    1.0f, // 音量
                    1.0f  // ピッチ.
                )
            }
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

                val currentOverHp = timer.toDouble() / speed.toDouble()

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

    @EventHandler
    fun onCommand(event: PlayerCommandPreprocessEvent){
        val player = event.player

        // プレイヤーがTag(s)を持っているか
        if (!player.scoreboardTags.contains("s")) return
        val command = event.message.lowercase()

        if (command.startsWith("/menu") || command.startsWith("/menu")) {
            player.sendMessage("§c現在このコマンドを使用することはできません")
            event.isCancelled = true
        }
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


    @EventHandler
    fun kit102SS1(event : PlayerDeathEvent){
        val victim = event.entity
        val killer = victim.killer ?: return

        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val kit1Obj = scoreboard.getObjective("kit1") ?: return
        val kit1Score = kit1Obj.getScore(killer.name)
        val ultObj = scoreboard.getObjective("ult") ?: return
        val ultScore = ultObj.getScore(killer.name)
        if(kit1Score.score == 2){
            ultScore.score += 40
            killer.sendMessage("§a${victim.name} を倒しました！")
        }
    }

    @EventHandler
    fun kit202ult(event: WeaponShootEvent) {
        val player = event.entity as? Player ?: return

        for (slot in 0..8) {
            val item = player.inventory.getItem(slot) ?: continue
            val weaponTitle = WeaponMechanicsAPI.getWeaponTitle(item)
            if (weaponTitle == "sniper_ult") {

                Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    "wm give ${player.name} sniper 1 slot:${slot}"
                )

            }
        }
    }

    @EventHandler
    fun kit202ss1(event: EntityDamageEvent) {
        val player = event.entity as? Player ?: return
        // 落下ダメージ以外は無視
        if (event.cause != EntityDamageEvent.DamageCause.FALL) return

        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val obj = scoreboard.getObjective("kit2") ?: return
        val score = obj.getScore(player.name).score
        if (score == 2) {
            event.isCancelled = true
            }
        }

    @EventHandler
    fun kit202ss2(event: EntityDamageByEntityEvent) {
        val damager = event.damager as? Player ?: return
        val item = damager.inventory.itemInMainHand
        val weaponTitle = WeaponMechanicsAPI.getWeaponTitle(item)
        if (weaponTitle == "sniper_ult" || weaponTitle == "sniper") {
            val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
            val speedBuffTimeObj = scoreboard.getObjective("timer_self_speed_buff_SS") ?: return
            val speedBuffTimeScore = speedBuffTimeObj.getScore(damager.name)
            val speedBuffObj = scoreboard.getObjective("self_speed_buff_SS") ?: return
            val speedBuffScore = speedBuffObj.getScore(damager.name)

            speedBuffTimeScore.score = 20
            speedBuffScore.score = 50
        }
    }

    @EventHandler
    fun kit103NS2(event: EntityDamageByEntityEvent) {
        val damager = event.damager as? Player ?: return
        val player = event.entity as? Player ?: return

        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val kit1Obj = scoreboard.getObjective("kit1") ?: return
        val kit1Score = kit1Obj.getScore(damager.name)
        val countObj = scoreboard.getObjective("mine_NS_count") ?: return
        val countScore = countObj.getScore(damager.name)
        if(kit1Score.score == 3){
            if(countScore.score >= 1) {
                val debuffObj = scoreboard.getObjective("add_debuff_time") ?: return
                val debuffScore = debuffObj.getScore(damager.name)

                val attackObj = scoreboard.getObjective("other_attack_debuff_NS") ?: return
                val attackScore = attackObj.getScore(player.name)
                val attackTimeObj = scoreboard.getObjective("timer_other_attack_debuff_NS") ?: return
                val attackTimeScore = attackTimeObj.getScore(player.name)

                attackScore.score = countScore.score
                attackTimeScore.score = buffTimeAmount(400.0, debuffScore.score)
                countScore.score = 0
            }
        }
    }

    @EventHandler
    fun kit103ULT(event: EntityDamageEvent) {
        val player = event.entity as? Player ?: return

        if (player.scoreboardTags.contains("mine_ult")){
            player.playSound(player.location, Sound.BLOCK_HEAVY_CORE_BREAK, 5.0f, 1.0f)
        }
    }

    @EventHandler
    fun kit203NS2(e: EntityDeathEvent) {
        val vex = e.entity
        if (vex !is Vex) return
        //ケイちゃん.
        val key = NamespacedKey(plugin, "vex_owner")
        //オーナーはだれ？.
        val ownerUUID = vex.persistentDataContainer.get(
            key,
            PersistentDataType.STRING
        ) ?: return

        val player = Bukkit.getPlayer(UUID.fromString(ownerUUID)) ?: return

        // スコアボード取得
        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        val shield = scoreboard.getObjective("shield") ?: return
        val shieldTime = scoreboard.getObjective("shield_time") ?: return
        val shieldScore = shield.getScore(player.name)
        val shieldTimeScore = shieldTime.getScore(player.name)
        shieldScore.score = 5
        shieldTimeScore.score = 200
    }




    //サイドバーセット.
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        val manager = Bukkit.getScoreboardManager()

        val board = manager.newScoreboard
        val main = manager.mainScoreboard

        for (team in main.teams) {

            val newTeam = board.getTeam(team.name) ?: board.registerNewTeam(team.name)

            for (entry in team.entries) {
                newTeam.addEntry(entry)
            }

            val color = when (team.name) {
                "red" -> NamedTextColor.RED
                "blue" -> NamedTextColor.BLUE
                "yellow" -> NamedTextColor.YELLOW
                "green" -> NamedTextColor.GREEN
                else -> null
            }

            if (color != null) {
                newTeam.color(color)
            }
        }

        val obj = board.registerNewObjective(
            "sidebar",
            Criteria.DUMMY,
            Component.text("ゲーム情報", NamedTextColor.GOLD)
        )

        obj.displaySlot = DisplaySlot.SIDEBAR

        var score = 15

        fun createLine(teamName: String, entry: String) {
            val team = board.getTeam(teamName) ?: board.registerNewTeam(teamName)
            team.addEntry(entry)
            obj.getScore(entry).score = score--
        }

        createLine("sb_map", "§0§r")

        createLine("sb_red", "§1§r")
        createLine("sb_blue", "§2§r")
        createLine("sb_yellow", "§3§r")
        createLine("sb_green", "§4§r")

        createLine("sb_buff", "§5§r")
        createLine("sb_debuff", "§6§r")

        createLine("sb_players", "§7§r")

        player.scoreboard = board
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
                    11 -> { // kit1を2に(マジカホリック)
                        if (event.isLeftClick) {
                            ki1Score?.let { it.score = 2 }
                            player.sendMessage("§bストライカーキットをマジカホリックに変更しました")
                            ns2MaxScore.score = 700
                            costUse11Score.score = 45
                            costUse12Score.score = 35
                            costUse11AScore.score = 45
                            costUse12AScore.score = 35
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
                    12 -> { // kit1を3に(ミネ)
                        if (event.isLeftClick) {
                            ki1Score?.let { it.score = 3 }
                            player.sendMessage("§bストライカーキットをミネに変更しました")
                            ns2MaxScore.score = 600
                            costUse11Score.score = 40
                            costUse12Score.score = 40
                            costUse11AScore.score = 40
                            costUse12AScore.score = 40
                            utUse1Score.score = 88
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
                    11 -> { // kit2を2に(スナイパー)
                        if(event.isLeftClick) {
                            ki2Score?.let { it.score = 2 }
                            player.sendMessage("§bスペシャルキットをスナイパーに変更しました")
                            ns1MaxScore2.score = 1000
                            ns2MaxScore2.score = 1200
                            costUse21Score.score = 30
                            costUse22Score.score = 40
                            costUse21AScore.score = 30
                            costUse22AScore.score = 40
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
                    12 -> { // kit2を3に(ミスティ)
                        if(event.isLeftClick) {
                            ki2Score?.let { it.score = 3 }
                            player.sendMessage("§bスペシャルキットをミスティに変更しました")
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








