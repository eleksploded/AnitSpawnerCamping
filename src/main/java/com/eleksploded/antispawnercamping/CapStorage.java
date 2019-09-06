package com.eleksploded.antispawnercamping;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import scala.actors.threadpool.Arrays;

public class CapStorage implements ICapabilitySerializable<NBTTagCompound> {

	@CapabilityInject(CapStorage.class)
	public static Capability<CapStorage> cap = null;
	
	private int time;
	private List<String> currentPlayer = new ArrayList<String>();
	
	public CapStorage(int time){
		time(time);
	}
	
	public List<String> currentPlayer() {
		return currentPlayer;
	}
	@SuppressWarnings("unchecked")
	public void currentPlayer(String[] in) {
		currentPlayer = Arrays.asList(in);
	}
	public void currentPlayer(List<String> in) {
		currentPlayer = in;
	}
	
	public int time() {
		return time;
	}
	public void time(int in){
		this.time = in;
	}
	
	public static void register() {
        CapabilityManager.INSTANCE.register(CapStorage.class, new Cap(), () -> new CapStorage(SpawnerConfig.max));
    }
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == cap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return capability == cap ? (T) this : null;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("Time", time);
        for(String player : currentPlayer()) {
        	nbt.setString("Player" + currentPlayer.indexOf(player), player);
        }
        return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.time = nbt.getInteger("Time");
		List<String> players = new ArrayList<String>();
		int i = 0;
		while(nbt.hasKey("Player" + i)) {
			players.add(nbt.getString("Player" + i));
			i = i+1;
		}
		this.currentPlayer = players;
	}
	
	private static class Cap implements Capability.IStorage<CapStorage> {

        @Override
        public NBTBase writeNBT(Capability<CapStorage> capability, CapStorage instance, EnumFacing side) {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setInteger("Time", instance.time());
            for(String player : instance.currentPlayer()) {
            	nbt.setString("Player" + instance.currentPlayer().indexOf(player), player);
            }
            return nbt;
        }

        @Override
        public void readNBT(Capability<CapStorage> capability, CapStorage instance, EnumFacing side, NBTBase nbt) {
            NBTTagCompound tags = (NBTTagCompound) nbt;
            instance.time(tags.getInteger("Time"));
            List<String> players = new ArrayList<String>();
    		int i = 0;
    		while(tags.hasKey("Player" + i)) {
    			players.add(tags.getString("Player" + i));
    			i = i+1;
    		}
    		instance.currentPlayer(players);
        }
	}
}
