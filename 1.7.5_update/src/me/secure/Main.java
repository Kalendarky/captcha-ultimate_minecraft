package me.secure;

import java.util.HashMap;

import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener
{
    private HashMap<String, String> hm = new HashMap<String, String>();
    private HashMap<String, String> hm2 = new HashMap<String,String>();

    public static Inventory myInventory = Bukkit.createInventory(null, 9, "Please Select diamond!");
    static {
        myInventory.setItem(0, new ItemStack(Material.DIRT, 1));
        myInventory.setItem(6, new ItemStack(Material.DIAMOND, 1));
        myInventory.setItem(2, new ItemStack(Material.GOLD_AXE, 1));
    }


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
        final Player p = e.getPlayer();
        hm.put(p.getName(), GeneratedCaptcha);
        p.sendMessage(ChatColor.RED + "[Captcha] Please, write to the chat: " + ChatColor.AQUA + "/captcha " + ChatColor.BLUE + GeneratedCaptcha);
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

            public void run()
            {
                if(hm.get(p.getName()) != null)
                    p.kickPlayer("Verification failed.");
            }
        }, 240L);
    }
    private String random_captcha()
    {
        return RandomStringUtils.randomAlphabetic(2) + RandomStringUtils.randomNumeric(2) + RandomStringUtils.randomAlphabetic(2);
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
                        hm2.put(p.getName(), "notverif");
                        p.sendMessage(ChatColor.GREEN + "Captcha code is good ! But the verification process is not complete.");
                        p.sendMessage(ChatColor.UNDERLINE + "When you cancel the verification menu, type /captcha and open it again.");
                        p.openInventory(myInventory);
                        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

                            public void run()
                            {
                                if(hm2.get(p.getName()) != null)
                                    p.kickPlayer("Verification failed.");
                            }
                        }, 200L);
                        return true;
                    }
                    else if(!(true_code.equals(args[0])))
                        p.sendMessage(ChatColor.RED + "BAD Captcha !");
                }
            }
            else if(hm2.get(p.getName()) != null) {
                p.closeInventory();
                p.openInventory(myInventory);
            }
        }
        return false;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e)
    {
        if (hm.get(e.getPlayer().getName()) != null || hm2.get(e.getPlayer().getName()) != null)
        {
            e.setCancelled(true);
        }
        else
        {
            e.setCancelled(false);
        }
    }
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e)
    {
        if(hm.get(e.getPlayer().getName()) != null || hm2.get(e.getPlayer().getName()) != null)
            e.setCancelled(true);
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e)
    {
        Player p = e.getPlayer();
        hm.remove(p.getName());
        hm2.remove(p.getName());
    }
    @EventHandler
    public void onPlayerHit(EntityDamageEvent e)
    {
        Entity en = e.getEntity();

        if(en instanceof Player)
        {
            Player player = (Player) en;
            if(hm.get(player.getName()) != null || hm2.get(player.getName()) != null)
                e.setCancelled(true);
        }
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        Inventory inventory = event.getInventory();
        if (inventory.getName().equals(myInventory.getName())) {
            if (clicked.getType() == Material.DIAMOND) {
                player.closeInventory();
                hm2.remove(player.getName());
                player.sendMessage(ChatColor.DARK_GREEN + "The verification process is complete.");
                event.setCancelled(true);
            }
            else
                event.setCancelled(true);
        }
    }
}
