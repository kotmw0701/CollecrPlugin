package jp.kotmw.together;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class Main extends JavaPlugin implements Listener {

	public static Main instance;
	String Collectmovemeta = "CollectPluginMeta";
	Map<String, Location> checkloc = new HashMap<>();
	static Map<Location, Integer> rt = new HashMap<>();
	static Map<String, List<Location>> rh = new HashMap<>();
	static List<Location> rb = new ArrayList<>();
	public String filepath = getDataFolder() + File.separator;
	public File dir = new File(filepath + "mazes");
	public File config = new File(filepath + "Config.yml");

	@Override
	public void onEnable() {
		instance = this;
		getServer().getPluginManager().registerEvents(this, this);
		if(!config.exists()) {
			this.getConfig().addDefault("Test", "test");
			this.getConfig().options().copyDefaults(true);
			this.saveConfig();
			this.reloadConfig();
		}
		if(!dir.exists())
			dir.mkdir();
		LoadMazeData.loadLoc();
	}

	@Override
	public void onDisable() {

	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender s, Command cmd, String lav, String[] args) {
		if(args.length >= 1) {
			if(s instanceof Player) {
				Player p = (Player)s;
				if((args.length == 1) && ("place".equalsIgnoreCase(args[0]))) {
					p.setMetadata(Collectmovemeta, new FixedMetadataValue(this, p.getName()));
				} else if((args.length == 1) && ("stop".equalsIgnoreCase(args[0]))) {
					if(p.hasMetadata(Collectmovemeta))
						p.removeMetadata(Collectmovemeta, this);
				} else if((args.length == 2) && ("particle".equalsIgnoreCase(args[0]))){
					new Particle(p, 100, Double.valueOf(args[1])).runTaskTimer(this, 0, 2);
				} else if((args.length == 2) && ("circle".equalsIgnoreCase(args[0]))){
					int radius = Integer.valueOf(args[1]);
					Location l = p.getLocation();
					for(int i = 0 ; i < 360 ; i++) {
						int x = (int) (l.getBlockX()+(radius*Math.sin(i)));
						int z = (int) (l.getBlockZ()+(radius*Math.cos(i)));
						l.getWorld().getBlockAt(x, l.getBlockY()+2, z).setType(Material.SMOOTH_BRICK);
					}
				} else if((args.length == 3) && ("invisiblemaze".equalsIgnoreCase(args[0]))) {
					String sn = args[2];
					if(("setup".equalsIgnoreCase(args[1]))) {
						WorldEditPlugin worldEdit = (WorldEditPlugin)Bukkit.getPluginManager().getPlugin("WorldEdit");
						Selection selection = worldEdit.getSelection(p);
						if(selection != null) {
							World w = selection.getWorld();
							String worldName = w.getName();
							int x1 = selection.getMinimumPoint().getBlockX();
							int y1 = selection.getMinimumPoint().getBlockY();
							int z1 = selection.getMinimumPoint().getBlockZ();
							int x2 = selection.getMaximumPoint().getBlockX();
							int y2 = selection.getMaximumPoint().getBlockY();
							int z2 = selection.getMaximumPoint().getBlockZ();

							File file = new File(getDataFolder() + File.separator + "mazes" + File.separator + sn + ".yml");
							String sp = "\r\n";
							try {
								file.createNewFile();
							} catch (IOException e) {
								System.out.println("ファイルが生成できません！");
								System.out.println(e);
							}
							try {
								FileWriter writer = new FileWriter(file);
								for(int x = x1 ; x <= x2 ; x++) {
									for(int y = y1 ; y <= y2 ; y++) {
										for(int z = z1 ; z <= z2 ; z++) {
											Material type = w.getBlockAt(x, y, z).getType();
											if(!type.equals(Material.AIR))
												writer.write(worldName+"/"+x+"/"+y+"/"+z+"/"+type.toString()+ sp);
										}
									}
								}
								writer.close();
							} catch (IOException e) {
								System.out.println("セーブファイルの作成ができませんでした");
								System.out.println(e);
							}
							LoadMazeData.loadLoc();
							for(Location l : LoadMazeData.getLocationList(sn)) {
								FallingBlock fb = l.getWorld().spawnFallingBlock(l, LoadMazeData.getBlockData(sn, l), (byte)0);
								fb.setDropItem(false);
								l.getBlock().setType(Material.AIR);
								new RemoveFallingBlock(fb).runTaskLater(this, 10);
							}
							return true;
						}
					} else if("Realization".equalsIgnoreCase(args[1])) {
						List<Location> ll = LoadMazeData.getLocationList(sn);
						if(ll == null) {
							s.sendMessage("そのステージは存在しません");
							return false;
						}
						for(Location l : ll) {
							l.getBlock().setType(LoadMazeData.getBlockData(sn, l));
						}
						s.sendMessage("ブロックを可視化しました");
					} else if("Abstraction".equalsIgnoreCase(args[1])) {
						List<Location> ll = LoadMazeData.getLocationList(sn);
						if(ll == null) {
							s.sendMessage("そのステージは存在しません");
							return false;
						}
						for(Location l : ll) {
							FallingBlock fb = l.getWorld().spawnFallingBlock(l, LoadMazeData.getBlockData(sn, l), (byte)0);
							fb.setDropItem(false);
							l.getBlock().setType(Material.AIR);
							new RemoveFallingBlock(fb).runTaskLater(this, 10);
						}
						s.sendMessage("ブロックを不可視化しました");
					} else if("delete".equalsIgnoreCase(args[1])) {
						List<Location> ll = LoadMazeData.clearLocData(sn);
						Map<Location, Material> lb = LoadMazeData.clearBlockData(sn);
						if(ll == null) {
							s.sendMessage("そのステージは存在しません");
							return false;
						}
						for(Location l : ll) {
							l.getBlock().setType(lb.get(l));
						}
						File file = new File(getDataFolder() + File.separator + "mazes" + File.separator + sn +".yml");
						if(!file.exists()) {
							s.sendMessage("ファイルが存在しません");
							return false;
						}
						file.delete();
					}
				}
			}
		}
		return false;
	}


	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if(p.hasMetadata(Collectmovemeta)) {
			Location l = p.getLocation().clone();
			int x = l.getBlockX() ,y = l.getBlockY() ,z = l.getBlockZ();
			Material m = Material.SMOOTH_BRICK;
			int x_ = x;
			int z_ = z;
			if(checkloc.containsKey(p.getName())) {
				x_ = checkloc.get(p.getName()).getBlockX();
				z_ = checkloc.get(p.getName()).getBlockZ();
			}
			if(x_ == x && z_ == z) {
				checkloc.put(p.getName(), LocConversion(l));
				return;
			}
			for(int xf = x-1 ; xf <= x+1 ; xf++) {
				for(int yf = y-1 ; yf <= y+1 ; yf++) {
					for(int zf = z-1 ; zf <= z+1 ; zf++) {
						if(yf == y-1 || (xf == x-1 || xf == x+1)) {
							l.getWorld().getBlockAt(xf, yf, zf).setType(m);
							Location sl = new Location(l.getWorld(), xf, yf, zf);
							rt.put(sl, 20*10+5);
							if(!rb.contains(sl)) {
								rb.add(sl);
								new RemoveBlock(sl, m).runTaskTimer(this, 0, 1);
							}
						}
					}
				}
			}
			checkloc.put(p.getName(), LocConversion(l));
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onMoveinArea(PlayerMoveEvent e) {
		String n = e.getPlayer().getName();
		Location l = e.getPlayer().getLocation().clone();
		List<Location> ll = new ArrayList<>();
		for(int x = l.getBlockX()-1 ; x <= l.getBlockX()+1 ; x++) {
			for(int y = l.getBlockY()-1 ; y <= l.getBlockY()+1 ; y++) {
				for(int z = l.getBlockZ()-1 ; z <= l.getBlockZ()+1 ; z++) {
					Location sl = new Location(l.getWorld(), x, y, z);
					for(String stage : LoadMazeData.getStageList()) {
						if(LoadMazeData.getLocationList(stage).contains(sl)) {
							sl.getBlock().setType(LoadMazeData.getBlockData(stage, sl));
							ll.add(sl);

							/*rt.put(sl, 20*2+10);
							if(!rb.contains(sl)) {
								rb.add(sl);
								new RemoveBlock(sl, Material.SMOOTH_BRICK).runTaskTimer(this, 0, 1);
							}*/
						}
					}
				}
			}
		}
		if(rh.containsKey(n)) {
			for(Location bl : rh.get(n)) {
				if(ll.contains(bl))
					continue;
				FallingBlock fb = bl.getWorld().spawnFallingBlock(bl, bl.getBlock().getType(), (byte)0);
				bl.getBlock().setType(Material.AIR);
				fb.setDropItem(false);
				new RemoveFallingBlock(fb).runTaskLater(this, 10);
			}
		}
		rh.put(n, ll);
	}

	@EventHandler
	public void onBlake(BlockBreakEvent e) {
		Location l = e.getBlock().getLocation();
		if(rb.contains(l)) {
			e.setCancelled(true);
		}
		for(String stage : LoadMazeData.getStageList()) {
			if(LoadMazeData.getLocationList(stage).contains(l)) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(e.getClickedBlock() == null)
			return;
		Location l = e.getClickedBlock().getLocation().clone();
		for(String stage : LoadMazeData.getStageList()) {
			if(LoadMazeData.getLocationList(stage).contains(l)) {
				e.setCancelled(true);
			}
		}
	}

	public Location LocConversion(Location l) {
		return new Location(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}

	/**
	 * パケットを送信
	 *
	 * @param player 対象
	 * @param packet パケット
	 */
	@SuppressWarnings("rawtypes")
	public static void sendPlayer(Player player, net.minecraft.server.v1_8_R3.Packet packet)
	{
		((org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
	}

	/**
	 * ファイルの保存
	 *
	 * @param fileconfiguration ファイルコンフィグを指定
	 * @param file ファイル指定
	 * @param save 上書きをするかリセットするか
	 */
	public void SettingFiles(FileConfiguration fileconfiguration, File file, boolean save)
	{
		if(!file.exists() || save)
		{
			try {
				fileconfiguration.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
