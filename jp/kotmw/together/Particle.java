package jp.kotmw.together;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Particle extends BukkitRunnable {

	int tick = 0;
	Player player;
	double radius;
	double radius2;
	Color color;
	Color color2;
	int ii;

	public Particle(Player player, int tick, double radius) {
		this.player = player;
		this.tick = tick;
		this.radius = radius;
		this.color = Color.AQUA;
	}

	@Override
	public void run() {
		if(tick > 0)
		{
			Location l = player.getLocation();
			if(color.equals(DyeColor.GREEN) || color.equals(DyeColor.BLUE))
				ii = -1;
			for(double i = 0 ; i < 360 ; i++) {
				double x = l.getX()+(radius*Math.sin(Math.toRadians(i)));
				double y = l.getY()+(radius*Math.sin(0));
				double z = l.getZ()+(radius*Math.cos(Math.toRadians(i)));
				PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(
						EnumParticle.REDSTONE
						, true
						, (float)x
						, (float)y + 2
						, (float)z
						, (color.getRed() / 255) + ii
						, (color.getGreen() / 255)
						, (color.getBlue() / 255)
						, 10
						, 0
						, 0);
				for(Player online : Bukkit.getOnlinePlayers())
					Main.sendPlayer(online, packet);
			}
			/*for(double i = 0 ; i < 360 ; i++) {
				double x = l.getX()+(radius2*Math.sin(Math.toRadians(i)));
				double y = l.getY()+(radius2*Math.sin(0));
				double z = l.getZ()+(radius2*Math.cos(Math.toRadians(i)));
				PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(
						EnumParticle.REDSTONE
						, true
						, (float)x
						, (float)y + 2
						, (float)z
						, (color2.getRed() / 255) + ii
						, (color2.getGreen() / 255)
						, (color2.getBlue() / 255)
						, 10
						, 0
						, 0);
				for(Player online : Bukkit.getOnlinePlayers())
					Main.sendPlayer(online, packet);
			}*/
			tick--;
		}
		else
		{
			this.cancel();
		}
	}

}
