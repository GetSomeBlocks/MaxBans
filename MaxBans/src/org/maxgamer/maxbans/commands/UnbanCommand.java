package org.maxgamer.maxbans.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.maxgamer.maxbans.banmanager.Ban;
import org.maxgamer.maxbans.banmanager.IPBan;
import org.maxgamer.maxbans.sync.Packet;
import org.maxgamer.maxbans.util.Formatter;
import org.maxgamer.maxbans.util.Util;

public class UnbanCommand extends CmdSkeleton{
    public UnbanCommand(){
        super("maxbans.unban");
        usage = Formatter.secondary + "Usage: /unban <player>";
    }
	public boolean run(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length > 0){
			boolean silent = Util.isSilent(args);
			String banner = Util.getName(sender);
			String name = args[0];
			if(name.isEmpty()){
				sender.sendMessage(Formatter.primary + " No name given.");
				return true;
			}
			
			if(Util.isIP(name)){
				//They gave us an IP instead.
				String ip = name; //For readabilities sake.
				IPBan ipban = plugin.getBanManager().getIPBan(ip);

				if(ipban != null){
					plugin.getBanManager().unbanip(ip);
					plugin.getBanManager().announce(Formatter.secondary + ip + Formatter.primary + " has been unbanned by " + Formatter.secondary + banner + Formatter.primary + ".", silent, sender);
					plugin.getBanManager().addHistory(name, banner, banner + " unbanned " + ip);
				}
				else{
					sender.sendMessage(Formatter.primary + "Could not find a ban for " + Formatter.secondary + ip + Formatter.primary + ".");
				}
				return true;
			}
			else{
				//Assume it's a name they gave us.
				name = plugin.getBanManager().match(name, true);
				String ip = plugin.getBanManager().getIP(name);
				
				Ban ban = plugin.getBanManager().getBan(name);
				IPBan ipban = plugin.getBanManager().getIPBan(ip);
				
				if(ipban == null && ban == null){
					sender.sendMessage(Formatter.primary + "Could not find a ban for " + Formatter.secondary + name + Formatter.primary + ".");
					return true;
				}
				
				if(ban != null){
					plugin.getBanManager().unban(name);
					
					if(plugin.getSyncer() != null){
			    		Packet prop = new Packet();
			    		prop.setCommand("unban");
			    		prop.put("name", name);
			    		plugin.getSyncer().broadcast(prop);
			    	}
				}
				if(ipban != null){
					plugin.getBanManager().unbanip(ip);
					
					if(plugin.getSyncer() != null){
			    		Packet prop = new Packet();
			    		prop.setCommand("unbanip");
			    		prop.put("ip", ip);
			    		plugin.getSyncer().broadcast(prop);
			    	}
				}
				
				plugin.getBanManager().announce(Formatter.secondary + name + Formatter.primary + " has been unbanned by " + Formatter.secondary + banner + Formatter.primary + ".", silent, sender);
				String message = Formatter.secondary + banner + Formatter.primary + " unbanned " + Formatter.secondary + name;
				plugin.getBanManager().addHistory(name, banner, message);
				
				if(plugin.getSyncer() != null){
					//Send the addhistory request.
		    		Packet history = new Packet().setCommand("addhistory").put("string", message).put("banner", banner).put("name", name);
		    		plugin.getSyncer().broadcast(history);
				}
				
				return true;
			}
		}
		else{
			sender.sendMessage(usage);
			return true;
		}
	}
}
