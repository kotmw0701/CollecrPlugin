package jp.kotmw.together;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class LoadMazeData {
	private static Map<String ,List<Location>> bloc = new HashMap<>();
	private static Map<String ,Map<Location, Material>> bdata = new HashMap<>();

	public static boolean loadLoc() {
		for(String stage : getStageList()) {
			File file = StageDirFiles(stage);
			if(!file.exists())
				return false;
			List<Location> bl = new ArrayList<>();
			Map<Location, Material> bd = new HashMap<>();
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String str = br.readLine();
				while(str != null) {
					String[] datas = str.split("/");
					World w = Bukkit.getWorld(datas[0]);
					int x = Integer.valueOf(datas[1]);
					int y = Integer.valueOf(datas[2]);
					int z = Integer.valueOf(datas[3]);
					Material m = Material.valueOf(datas[4]);
					bl.add(new Location(w, x, y, z));
					bd.put(new Location(w, x, y, z), m);
					str = br.readLine();
				}
				bloc.put(stage, bl);
				bdata.put(stage, bd);
				br.close();
			} catch (Exception e) {
				System.out.println("ファイルが存在しません");
				System.out.println(e);
			}
		}
		return true;
	}

	public static File StageDirFiles(String stage) {
		return new File(Main.instance.filepath + "mazes" + File.separator + stage + ".yml");
	}

	public static List<Location> getLocationList(String stage) {
		if(!bloc.containsKey(stage))
			return null;
		return bloc.get(stage);
	}

	public static Material getBlockData(String stage, Location loc) {
		if(!bdata.containsKey(stage))
			return null;
		Map<Location, Material> bd = bdata.get(stage);
		if(!bd.containsKey(loc))
			return null;
		return bd.get(loc);
	}

	public static List<String> getStageList() {
		List<String> names = new ArrayList<>();
		for(File file : Arrays.asList(Main.instance.dir.listFiles()))
		{
			if(file.isDirectory())
				continue;
			names.add(getName(file.getName()));
		}
		return names;
	}

	public static String getName(String name)
	{
		if (name == null)
			return null;
		int point = name.lastIndexOf(".");
		if (point != -1)
			return name.substring(0, point);
		return name;
	}

	public static List<Location> clearLocData(String stage) {
		if(!bloc.containsKey(stage))
			return null;
		return bloc.remove(stage);
	}

	public static Map<Location, Material> clearBlockData(String stage) {
		if(!bdata.containsKey(stage))
			return null;
		return bdata.remove(stage);
	}
}
