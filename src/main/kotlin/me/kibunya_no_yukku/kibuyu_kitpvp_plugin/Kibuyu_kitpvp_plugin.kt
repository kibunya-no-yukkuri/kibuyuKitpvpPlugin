package me.kibunya_no_yukku.kibuyu_kitpvp_plugin

import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class Kibuyu_kitpvp_plugin : JavaPlugin() {
    companion object {
        // PlayerのUUID → GUIの種類
        val guiMap: MutableMap<UUID, String> = mutableMapOf()

        val shieldMap = mutableMapOf<UUID, Int>()
    }

    // 🔽 ここでフィールドを定義する
    lateinit var listener: EventListener
        private set
    lateinit var skillListener: SkillListener

    override fun onEnable() {
        // Plugin startup logic
        getCommand("menu")?.setExecutor(MenuCommand)
        getCommand("look")?.setExecutor(LookCommand)
        Tick(this).start()

        // 🔽 フィールドに代入
        listener = EventListener(this)
        server.pluginManager.registerEvents(listener, this)
        skillListener = SkillListener(this)
        server.pluginManager.registerEvents(skillListener, this)
        HpSyncTask(this,listener ).runTaskTimer(this, 0L, 1L)

        server.pluginManager.registerEvents(SkillListener(this), this)
        SpeedSyncTask(this).runTaskTimer(this, 0L, 1L) // 20 tick = 1秒ごとに更新


    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}


