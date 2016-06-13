package jp.kotmw.together;

import org.bukkit.entity.FallingBlock;
import org.bukkit.scheduler.BukkitRunnable;

public class RemoveFallingBlock extends BukkitRunnable {

	FallingBlock fb;

	public RemoveFallingBlock(FallingBlock fb) {
		this.fb = fb;
	}

	@Override
	public void run() {
		fb.remove();
	}

}
