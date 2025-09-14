package Rice.Chen.BrilliantResMapFlag;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.FlagPermissions;

public class BrilliantResMapFlag extends JavaPlugin implements Listener {

    private Residence residence;

    @Override
    public void onEnable() {
        Plugin resPlugin = getServer().getPluginManager().getPlugin("Residence");
        if (resPlugin == null || !resPlugin.isEnabled()) {
            getLogger().warning("Residence 未安裝或未啟用，正在停用插件。");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.residence = (Residence) resPlugin;

        getServer().getPluginManager().registerEvents(this, this);
        FlagPermissions.addFlag("使用地圖（usemap）");
        getLogger().info("BrilliantResMapFlag 已啟用，「使用地圖（usemap）」權限已註冊。");
    }

    @Override
    public void onDisable() {
        getLogger().info("BrilliantResMapFlag 已停用。");
    }

    private boolean canUseMap(Player player, org.bukkit.Location loc) {
        double y = loc.getY();
        String worldName = loc.getWorld().getName();
        boolean heightDenied = switch (worldName) {
            case "world" -> y > 320 || y < -64;
            case "world_nether" -> y > 320 || y < 0;
            case "world_the_end" -> y > 255 || y < 0;
            default -> false;
        };
        if (heightDenied) {
            player.sendMessage("§7｜§6系統§7｜§f飯娘：§7您無法在此高度使用地圖。");
            return false;
        }

        ClaimedResidence res = residence.getResidenceManager().getByLoc(loc);
        if (res != null) {
            boolean hasPerm = res.getPermissions().playerHas(player, "使用地圖（usemap）", true);
            if (!hasPerm) {
                player.sendMessage("§7｜§6系統§7｜§f飯娘：§7您沒有領地 §e" + res.getName() + " §7的 §e使用地圖（usemap） §7權限。");
                return false;
            }
        }
        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.isOp()) return;

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        if (event.getItem() != null && event.getItem().getType() == Material.MAP && !canUseMap(player, player.getLocation())) {
            event.setCancelled(true);
        }
    }
}