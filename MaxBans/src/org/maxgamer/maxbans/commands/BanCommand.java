package org.maxgamer.maxbans.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.maxgamer.maxbans.Msg;
import org.maxgamer.maxbans.banmanager.Ban;
import org.maxgamer.maxbans.banmanager.IPBan;
import org.maxgamer.maxbans.banmanager.TempBan;
import org.maxgamer.maxbans.banmanager.TempIPBan;
import org.maxgamer.maxbans.sync.Packet;
import org.maxgamer.maxbans.util.Util;

public class BanCommand extends CmdSkeleton{
    public BanCommand(){
        super("ban", "maxbans.ban");
        //usage = Formatter.secondary + "Usage: /ban <player> [-s] <reason>";
    }
    
	public boolean run(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length > 0){
			boolean silent = Util.isSilent(args);
			
			String name = args[0];
			if(name.isEmpty()){
				//sender.sendMessage(Formatter.primary + " No name given.");
				sender.sendMessage(Msg.get("error.no-player-given"));
				return true;
			}
			
			//Build reason
			String reason = Util.buildReason(args);
			String banner = Util.getName(sender);
			
			if(!Util.isIP(name)){
				name = plugin.getBanManager().match(name);
				if(name == null){
					name = args[0]; //Use exact name then.
				}
				
				Ban ban = plugin.getBanManager().getBan(name);
				if(ban != null && !(ban instanceof TempBan)){
					//sender.sendMessage(Formatter.secondary + "That player is already banned.");
					sender.sendMessage(Msg.get("error.player-already-banned"));
					return true;
				}
				
				plugin.getBanManager().ban(name, reason, banner);
			}
			else{
				IPBan ipban = plugin.getBanManager().getIPBan(name);
				if(ipban != null && !(ipban instanceof TempIPBan)){
					//sender.sendMessage(Formatter.secondary + "That IP is already banned.");
					sender.sendMessage(Msg.get("error.ip-already-banned"));
					return true;
				}
				
				plugin.getBanManager().ipban(name, reason, banner);
			}
			
			/*
			plugin.getBanManager().announce(Formatter.secondary + name + Formatter.primary + " has been banned by " + Formatter.secondary + banner + Formatter.primary + ". Reason: '" + Formatter.secondary + reason + Formatter.primary + "'", silent, sender);
			
			String message = Formatter.secondary + banner + Formatter.primary + " banned " + Formatter.secondary + name + Formatter.primary + " for '" + Formatter.secondary + reason + Formatter.primary + "'";
			plugin.getBanManager().addHistory(name, banner, message);*/
			String message = Msg.get("announcement.player-was-banned", new String[]{"banner", "name", "reason"}, new String[]{banner, name, reason});
			plugin.getBanManager().announce(message, silent, sender);
			plugin.getBanManager().addHistory(name, banner, message);
			
	    	if(plugin.getSyncer() != null){
	    		Packet prop = new Packet();
	    		prop.setCommand("ban");
	    		prop.put("name", name);
	    		prop.put("reason", reason);
	    		prop.put("banner", banner);
	    		plugin.getSyncer().broadcast(prop);
	    		
	    		Packet history = new Packet().setCommand("addhistory").put("string", message).put("banner", banner).put("name", name);
	    		plugin.getSyncer().broadcast(history);
	    	}
	    	
			return true;
		}
		else{
			sender.sendMessage(getUsage());
			return true;
		}
	}
}
