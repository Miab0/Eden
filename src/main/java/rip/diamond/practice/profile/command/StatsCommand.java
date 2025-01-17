package rip.diamond.practice.profile.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.leaderboard.menu.impl.KitStatsMenu;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

import java.util.concurrent.CompletableFuture;

public class StatsCommand extends Command {
    @CommandArgs(name = "stats", async = true)
    public void execute(CommandArguments command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        String username;
        if (args.length == 0) {
            username = player.getName();
        } else {
            username = args[0];
        }

        OfflinePlayer offlinePlayer = CompletableFuture.supplyAsync(() -> Bukkit.getOfflinePlayer(username)).join();
        if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) {
            Language.PROFILE_CANNOT_FIND_PLAYER.sendMessage(player);
            return;
        }

        PlayerProfile profile = PlayerProfile.get(offlinePlayer.getUniqueId());
        if (profile != null) {
            new KitStatsMenu(profile).openMenu(player);
            return;
        }

        PlayerProfile finalProfile = PlayerProfile.createPlayerProfile(offlinePlayer.getUniqueId(), offlinePlayer.getName());
        finalProfile.setTemporary(true);
        finalProfile.load(success -> {
            if (success) {
                new KitStatsMenu(finalProfile).openMenu(player);
                PlayerProfile.getProfiles().remove(offlinePlayer.getUniqueId());
            } else {
                Language.PROFILE_ERROR_CANNOT_LOAD_PLAYER.sendMessage(player);
            }
        });
    }
}
