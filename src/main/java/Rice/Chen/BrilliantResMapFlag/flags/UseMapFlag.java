package Rice.Chen.BrilliantResMapFlag.flags;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.FlagPermissions;

public class UseMapFlag implements Listener {

    private final JavaPlugin plugin;
    private final Residence residence;

    public UseMapFlag(JavaPlugin plugin, Residence residence) {
        this.plugin = plugin;
        this.residence = residence;
    }

    public void registerFlag() {
        FlagPermissions.addFlag("使用地圖（usemap）");
        plugin.getLogger().info("已註冊「使用地圖（usemap）」權限。");
    }

    public boolean canUseMap(Player player, Location loc) {
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