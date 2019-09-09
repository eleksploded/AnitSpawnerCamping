package com.eleksploded.antispawnercamping;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class ClientProxy extends ServerProxy {
	public void spawn(LivingSpawnEvent.SpecialSpawn e) {}
	
	public static void Singlespawn(LivingSpawnEvent.SpecialSpawn e) {
		BlockPos pos = getSpawner(e);
        if (pos != null) {
        	TileEntity spawner = e.getWorld().getTileEntity(pos);
            if (spawner instanceof TileEntityMobSpawner && spawner.hasCapability(CapStorage.cap, null)) {
    			CapStorage storage = spawner.getCapability(CapStorage.cap, null);
            	for(EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()){
            		if(ArrayUtils.contains(SpawnerConfig.ignore, player.getName())) return;
            		if(player.getDistance(pos.getX(), pos.getY(), pos.getZ()) <= SpawnerConfig.checkRadius){
            			
            			//if(SpawnerConfig.debug) {
            				//AntiSpawnerCamping.logger.info("1: Current Players are: " + Arrays.toString(storage.currentPlayer().toArray()));
            				AntiSpawnerCamping.logger.info("Deducting spawn from " + pos + " for player " + player.getName() + ". " + storage.time() + " spawns remaining.");
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
                                server.getCommandManager().executeCommand(server, AntiSpawnerCamping.phraseCommand(command, player, pos));
                            }
                        } else {
                        	storage.time(storage.time()-1);
                        	spawner.markDirty();
                        }
            			
            		} else {
            			if(storage.currentPlayer().contains(player.getName())) {
            				//AntiSpawnerCamping.logger.info("2: Current Players are: " + Arrays.toString(storage.currentPlayer().toArray()));
            				List<String> tmp = storage.currentPlayer();
            				tmp.remove(player.getName());
            				storage.currentPlayer(tmp);
            				spawner.markDirty();
            			}
            		}
            		
            	}
            	if(storage.currentPlayer().isEmpty()) {
            		//AntiSpawnerCamping.logger.info("3: Current Players are: " + Arrays.toString(storage.currentPlayer().toArray()));
            		storage.time(SpawnerConfig.max);
            		spawner.markDirty();
            	}
            }  
        }
	}
}
