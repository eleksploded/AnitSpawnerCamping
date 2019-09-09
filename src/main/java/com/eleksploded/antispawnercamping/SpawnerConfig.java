package com.eleksploded.antispawnercamping;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = AntiSpawnerCamping.MODID)
public class SpawnerConfig {
	
	@Comment("Max number of spawns before running commands on the player")
	public static int max = 10;
	
	@Comment("Radius from the spawner to check for players")
	public static int checkRadius = 10;
	
	@Comment("Enable/Disable Debug Output")
	public static boolean debug = false;
	
	@Comment({"List of Commands to run every spawn above the max spawns. (Ran as server) Codes (Case Sensitive):",
		"Player = Player's Username, PlayerX = Player's X Position, PlayerY = Player's Y Position, PlayerZ = Player's Z Position,",
		"SpawnerX = Spawner's X Position, SpawnerY = Spawner's Y Position, SpawnerX = Spawner's Y Position"})
	public static String[] commands = {"msg Player PlayerX PlayerY PlayerZ Spawner is SpawnerX SpawnerY SpawnerZ", "summon minecraft:tnt PlayerX PlayerY PlayerZ"};

	@Comment("Player Usernames to ignore for spawner camping")
	public static String[] ignore = {};
	
	@Mod.EventBusSubscriber(modid = AntiSpawnerCamping.MODID)
    private static class ConfigChange {
        @SubscribeEvent
        public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(AntiSpawnerCamping.MODID)) {
                ConfigManager.sync(AntiSpawnerCamping.MODID, Config.Type.INSTANCE);
            }
        }
    }
}
