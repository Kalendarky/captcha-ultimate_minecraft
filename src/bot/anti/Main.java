package bot.anti;

import java.util.HashMap;

import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin implements Listener
{
	HashMap<String, String> hm = new HashMap<String, String>();

	
	@Override
	public void onEnable()
	{
		System.out.println("Captcha Enabled.");
	    
		Bukkit.getPluginManager().registerEvents(this, this);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
			String GeneratedCaptcha;
			GeneratedCaptcha = random_captcha();
			Player p = e.getPlayer();
			hm.put(p.getName(), GeneratedCaptcha);
			p.sendMessage(ChatColor.RED + "[Captcha] Please, write to the chat: " + ChatColor.AQUA + "/captcha " + ChatColor.BLUE + GeneratedCaptcha);
			this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				 
				  public void run() 
				  {
				      if(hm.get(p.getName()) != null)
				    	  p.kickPlayer("You do not say /captcha specialcode!");
				  }
			}, 260L);
	}
	public String random_captcha() 
	{
	    String generatedString = RandomStringUtils.randomAlphabetic(4) + RandomStringUtils.randomNumeric(2);
	 
	    return generatedString;
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		Player p = (Player)sender;
		
		if (cmd.getName().equalsIgnoreCase("captcha")) 
		{
			if (args.length == 1)
			{
				String true_code = (String) hm.get(p.getName());
				
				if (hm.get(p.getName()) != null)
				{
					if(true_code.equals(args[0]))
					{
						hm.remove(p.getName());
						p.sendMessage("Captcha code is good ! Welcome!");
						return true;
					}
					else if(!(true_code.equals(args[0])))
						p.sendMessage("Captcha code is bad !");
				}
				else
					p.sendMessage("Already done.");
			}
		}
		return false;
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e)
	{
		if (hm.get(e.getPlayer().getName()) != null) 
		{
			e.setCancelled(true);
		} 
		else 
		{
			e.setCancelled(false);
		}
	}
	@EventHandler
	public void playerChat(AsyncPlayerChatEvent e)
	{
		if(hm.get(e.getPlayer().getName()) != null)
			e.setCancelled(true);
	}
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) 
	{
		Player p = e.getPlayer();
		hm.remove(p.getName());
	}
	@EventHandler
	public void dmg(EntityDamageEvent e) 
	{
		Entity en = e.getEntity();
		
		if(en instanceof Player) 
		{
			Player player = (Player) en;
			if(hm.get(player.getName()) != null)
				e.setCancelled(true);
		}
	}
}
