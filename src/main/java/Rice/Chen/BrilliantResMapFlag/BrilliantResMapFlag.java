package Rice.Chen.BrilliantResMapFlag;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import Rice.Chen.BrilliantResMapFlag.flags.UseMapFlag;
import com.bekvon.bukkit.residence.Residence;

public class BrilliantResMapFlag extends JavaPlugin {

    private Residence residence;
    private UseMapFlag useMapFlag;

    @Override
    public void onEnable() {
        Plugin resPlugin = getServer().getPluginManager().getPlugin("Residence");
        if (resPlugin == null || !resPlugin.isEnabled()) {
            getLogger().warning("Residence 未安裝或未啟用，正在停用插件。");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.residence = (Residence) resPlugin;
        this.useMapFlag = new UseMapFlag(this, residence);

        getServer().getPluginManager().registerEvents(useMapFlag, this);
        useMapFlag.registerFlag();
        getLogger().info("BrilliantResMapFlag 已啟用。");
    }

    @Override
    public void onDisable() {
        getLogger().info("BrilliantResMapFlag 已停用。");
    }
}