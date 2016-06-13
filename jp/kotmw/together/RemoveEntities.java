package jp.kotmw.together;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

public class RemoveEntities extends BukkitRunnable{

	private List<Entity> entitieslist = new ArrayList<>();
	private Entity entities;

	public RemoveEntities(List<Entity> entities)
	{
		this.entitieslist = entities;
	}

	public RemoveEntities(Entity entity)
	{
		this.entities = entity;
	}

	public void run()
	{
		if(entities != null)
		{
			entities.remove();
			return;
		}
		for(Entity entities : entitieslist)
			entities.remove();
	}

}
