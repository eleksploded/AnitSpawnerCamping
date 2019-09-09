package com.eleksploded.antispawnercamping;

import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = AntiSpawnerCamping.MODID, name = AntiSpawnerCamping.NAME, version = AntiSpawnerCamping.VERSION, acceptableRemoteVersions = "*")
public class AntiSpawnerCamping
{
	public static final String MODID = "antispawnercamping";
	public static final String NAME = "Anti-Spawner Camping";
	public static final String VERSION = "1.0";
	public static Logger logger;
	
	@SidedProxy(modId=AntiSpawnerCamping.MODID,clientSide="com.eleksploded.antispawnercamping.ClientProxy", serverSide="com.eleksploded.antispawnercamping.ServerProxy")
	public static ServerProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(this);
		CapStorage.register();
		logger = event.getModLog();
	}
	
	@SubscribeEvent
    public void onAttachCapability(AttachCapabilitiesEvent<TileEntity> event) {
        if (event.getObject() instanceof TileEntityMobSpawner) {
            event.addCapability(new ResourceLocation(MODID, "antispawnerstorage"), new CapStorage(SpawnerConfig.max));
        }
    }
	
	@SubscribeEvent
	public void spawn(LivingSpawnEvent.SpecialSpawn e){
		try {
			if(Minecraft.getMinecraft().isSingleplayer()) {
				ClientProxy.Singlespawn(e);
			}
		} catch(NoClassDefFoundError ex) {
			//if(SpawnerConfig.debug) logger.info("Running on server");
		}
		proxy.spawn(e);
	}
	
	public static String phraseCommand(String in, EntityPlayer player, BlockPos block) {
		StringBuilder builder = new StringBuilder();
		String[] input = in.split(" ");
		
		for(String string : input){
			if(string.equals("PlayerX")){
				builder.append((int)player.posX);
			} else if(string.equals("PlayerY")) {
				builder.append((int)player.posY);
			} else if(string.equals("PlayerZ")) {
				builder.append((int)player.posZ);
			} else if(string.equals("SpawnerX")) {
				builder.append(block.getX());
			} else if(string.equals("SpawnerY")) {
				builder.append(block.getY());
			} else if(string.equals("SpawnerZ")) {
				builder.append(block.getZ());
			} else if(string.equals("Player")) {
				builder.append(player.getName());
			} else {
				builder.append(string);
			}
			builder.append(" ");
		}
		
		return builder.toString();
	}
	
	
}