package flaxbeard.steamcraft.item.tool.steam;

import java.util.HashMap;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;

import org.apache.commons.lang3.tuple.MutablePair;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import flaxbeard.steamcraft.api.ISteamChargable;

public class ItemSteamDrill extends ItemPickaxe implements ISteamChargable {
	public IIcon[] icon = new IIcon[2];
	public static HashMap<Integer,MutablePair<Integer,Integer>> stuff = new HashMap<Integer,MutablePair<Integer,Integer>>();
	
	public ItemSteamDrill() {
		super(EnumHelper.addToolMaterial("DRILL", 2, 1600, 1.0F, -1.0F, 0));
	}

	@Override
    public boolean hitEntity(ItemStack par1ItemStack, EntityLivingBase par2EntityLivingBase, EntityLivingBase par3EntityLivingBase)
    {
        return true;
    }
	
	

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase entity)
    {
    	MutablePair info = stuff.get(entity.getEntityId());
    	int speed = (Integer) info.right;
    	speed -= 10;
    	stuff.put(entity.getEntityId(), MutablePair.of((Integer)info.left, speed));
    	System.out.println(speed);
    	System.out.println("Decreasing on account of you broke the block");
    	return true;
    }
	
	public static void checkNBT(EntityPlayer player) {
		if (!stuff.containsKey(player.getEntityId())) {
			stuff.put(player.getEntityId(), MutablePair.of(0,0));
		}
	}

	
    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining)
    {
    	this.checkNBT(player);

    	MutablePair info = stuff.get(player.getEntityId());
    	int ticks = (Integer) info.left;
    	return this.icon[ticks > 125 ? 0 : 1];
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister ir)
	{
		this.icon[0] = this.itemIcon = ir.registerIcon("steamcraft:drill0");
		this.icon[1] = ir.registerIcon("steamcraft:drill1");
	}
	
    public void onUpdate(ItemStack stack, World par2World, Entity player, int par4, boolean par5) {
    	if (player instanceof EntityPlayer) {
	    	this.checkNBT((EntityPlayer) player);
	    	MutablePair info = stuff.get(player.getEntityId());
	    	int ticks = (Integer) info.left;
	    	int speed = (Integer) info.right;
	    	if (speed % 2 == 0){
	    		ticks += speed ;
	    	}
	    	
	    	//System.out.println("speed: "+speed + "; ticks: "+ticks);
	    	if (speed > 0) {
	    		
	    		speed--;
	    	} else if (ticks <= 0){
	    		ticks = 0;
	    	} else {
	    		ticks--;
	    	}
	    	
	    	
	    	ticks = ticks%201;
			stuff.put(player.getEntityId(), MutablePair.of(ticks, speed));
    	}
    }
    
   
    public ItemStack onItemRightClick(ItemStack stack, World par2World, EntityPlayer player)
    {
    	this.checkNBT(player);
		if (stack.getItemDamage() < stack.getMaxDamage()-1) {
	    	MutablePair info = stuff.get(player.getEntityId());
	    	int ticks = (Integer) info.left;
	    	int speed = (Integer) info.right;
	    	if (speed <= 1000) {
	    		speed+=Math.min(90,1000-speed);
	    		stack.damageItem(1, player);
	    	}
			stuff.put(player.getEntityId(), MutablePair.of(ticks, speed));
			System.out.println(speed);
		}
    	return stack;
    	
    }
    
    @Override
    public float getDigSpeed(ItemStack stack, Block block, int meta)
    {
    	return 1.0F;
    }

	@Override
	public int steamPerDurability() {
		return 20;
	}

	@Override
	public boolean canCharge(ItemStack me) {
		return true;
	}

}
