package jp.kotmw.together;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.scheduler.BukkitRunnable;

public class RemoveBlock extends BukkitRunnable {

	Location l;
	Material m;
	FallingBlock fb;

	public RemoveBlock(Location l, Material m) {
		this.l = l;
		this.m = m;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		int tick = Main.rt.get(l);
		if (tick == 10) {
			fb = l.getWorld().spawnFallingBlock(l, m, (byte)0);
			l.getBlock().setType(Material.AIR);
		} else if (tick == 0) {
			Main.rb.remove(l);
			fb.remove();
			this.cancel();
		}
		if(fb != null && tick > 20)
			fb.remove();
		tick--;
		Main.rt.put(l, tick);
	}
}
