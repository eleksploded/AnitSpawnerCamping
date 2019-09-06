package com.eleksploded.antispawnercamping;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = AntiSpawnerCamping.MODID, name = AntiSpawnerCamping.NAME, version = AntiSpawnerCamping.VERSION)
public class AntiSpawnerCamping
{
	public static final String MODID = "anitspawnercamping";
	public static final String NAME = "Anti-Spawner Camping";
	public static final String VERSION = "1.0.0";

	private static Logger logger;

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
	
	//@SideOnly(Side.SERVER)
	@SubscribeEvent
	public void spawn(LivingSpawnEvent.SpecialSpawn e){
		//logger.info("REEE");
		BlockPos pos = getSpawner(e);
        if (pos != null) {
        	TileEntity spawner = e.getWorld().getTileEntity(pos);
            if (spawner instanceof TileEntityMobSpawner && spawner.hasCapability(CapStorage.cap, null)) {
    			CapStorage storage = spawner.getCapability(CapStorage.cap, null);
            	for(EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()){
            		//if(ArrayUtils.contains(SpawnerConfig.ignore, player.getName())) return;
            		if(player.getDistance(pos.getX(), pos.getY(), pos.getZ()) <= SpawnerConfig.checkRadius){
            			
            			//if(SpawnerConfig.debug) {
            				logger.info("1: Current Players are: " + Arrays.toString(storage.currentPlayer().toArray()));
            				logger.info("Deducting spawn from " + pos + " for player " + player.getName() + ". " + storage.time() + " spawns remaining.");
            			//}
            			
            			if(!storage.currentPlayer().contains(player.getName())) {
            				storage.time(SpawnerConfig.max);
            				List<String> tmp = storage.currentPlayer();
            				tmp.add(player.getName());
            				storage.currentPlayer(tmp);
            				spawner.markDirty();
            			} else if (storage.time() <= 0) {
                        	MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
                            for(String command : SpawnerConfig.commands){
                                server.getCommandManager().executeCommand(server, phraseCommand(command, player, pos));
                            }
                        } else {
                        	storage.time(storage.time()-1);
                        	spawner.markDirty();
                        }
            			
            		} else {
            			if(storage.currentPlayer().contains(player.getName())) {
            				logger.info("2: Current Players are: " + Arrays.toString(storage.currentPlayer().toArray()));
            				List<String> tmp = storage.currentPlayer();
            				tmp.remove(player.getName());
            				storage.currentPlayer(tmp);
            				spawner.markDirty();
            			}
            		}
            		
            	}
            	if(storage.currentPlayer().isEmpty()) {
    				logger.info("3: Current Players are: " + Arrays.toString(storage.currentPlayer().toArray()));
            		storage.time(SpawnerConfig.max);
            		spawner.markDirty();
            	}
            }  
        }
	}
	
	public BlockPos getSpawner(LivingSpawnEvent.SpecialSpawn event) {
        if (event.getSpawner() != null) {
            return event.getSpawner().getSpawnerPosition();
        }
        return null;
    }
	
	public String phraseCommand(String in, EntityPlayer player, BlockPos block) {
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