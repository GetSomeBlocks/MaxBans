package org.maxgamer.maxbans.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maxgamer.maxbans.MaxBans;
import org.maxgamer.maxbans.banmanager.Mute;

public class MuteCommand implements CommandExecutor{
    private MaxBans plugin;
    public MuteCommand(MaxBans plugin){
        this.plugin = plugin;
    }
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("maxbans.mute")){
			sender.sendMessage(ChatColor.RED + "You don't have permission to do that");
			return true;
		}
		String usage = ChatColor.RED + "Usage: /mute <player>";
		
		if(args.length > 0){
			String name = args[0];
			
			name = plugin.getBanManager().match(name);
			if(name == null){
				name = args[0]; //Use exact name then.
			}
			
			String banner;
			
			Mute mute = plugin.getBanManager().getMute(name);
			if(mute != null){
				plugin.getBanManager().unmute(name);
				sender.sendMessage(ChatColor.GREEN + "Unmuted " + name);
				return true;
			}
			
			if(sender instanceof Player){
				banner = ((Player) sender).getName();
			}
			else{
				banner = "Console";
			}
			
			plugin.getBanManager().mute(name, banner);
			
			Player p = Bukkit.getPlayerExact(name);
			if(p != null){
				p.sendMessage(ChatColor.RED + " You have been muted.");
			}
			sender.sendMessage(ChatColor.AQUA + "Muted " + ChatColor.AQUA + name);
			
			return true;
		}
		else{
			sender.sendMessage(usage);
			return true;
		}
	}
}
