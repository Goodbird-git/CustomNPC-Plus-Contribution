package noppes.npcs.items;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.scripted.NpcAPI;
import noppes.npcs.scripted.event.ItemEvent;
import noppes.npcs.scripted.interfaces.IItemStack;
import noppes.npcs.scripted.interfaces.IPlayer;
import noppes.npcs.scripted.item.ScriptCustomItem;
import org.lwjgl.opengl.GL11;

public class ItemScripted extends Item implements ItemRenderInterface {
    public ItemScripted() {
        maxStackSize = 1;
        setCreativeTab(CustomItems.tab);
        CustomNpcs.proxy.registerItem(this);
        setHasSubtypes(true);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity player, int p_77663_4_, boolean p_77663_5_) {
        IItemStack istack = NpcAPI.Instance().getIItemStack(stack);
        EventHooks.onScriptItemUpdate((ScriptCustomItem) istack, (EntityPlayer) player);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister){
        this.itemIcon = Items.iron_pickaxe.getIconFromDamage(0);
    }

    @Override
    public int getColorFromItemStack(ItemStack itemStack, int par2){
        return 0x8B4513;
    }

    @Override
    public boolean requiresMultipleRenderPasses(){
        return true;
    }

    @Override
    public Item setUnlocalizedName(String name){
        GameRegistry.registerItem(this, name);
        return super.setUnlocalizedName(name);
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer entityPlayer)
    {
        if(entityPlayer.isSneaking()) {
            CustomNpcs.proxy.openGui(0, 0, 0, EnumGuiType.ScriptItem, entityPlayer);
        }
        entityPlayer.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        return stack;
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entityLivingBase, ItemStack stack){
        if(entityLivingBase instanceof EntityClientPlayerMP)
            return false;

        IItemStack istack = NpcAPI.Instance().getIItemStack(stack);
        ItemEvent.AttackEvent eve = new ItemEvent.AttackEvent( (ScriptCustomItem) istack, ScriptController.Instance.getScriptForEntity(entityLivingBase), 2, null);
        return EventHooks.onScriptItemAttack((ScriptCustomItem) istack, eve);
    }

    public int getMaxItemUseDuration(ItemStack stack){
        return (new ScriptCustomItem(stack)).getMaxItemUseDuration();
    }

    public static ScriptCustomItem GetWrapper(ItemStack stack) {
        return new ScriptCustomItem(stack);
    }

    public boolean showDurabilityBar(ItemStack stack) {
        IItemStack istack = NpcAPI.Instance().getIItemStack(stack);
        return istack instanceof ScriptCustomItem && (new ScriptCustomItem(stack)).durabilityShow;
    }

    public double getDurabilityForDisplay(ItemStack stack) {
        IItemStack istack = NpcAPI.Instance().getIItemStack(stack);
        return istack instanceof ScriptCustomItem ? 1.0D - (new ScriptCustomItem(stack)).durabilityValue : 1.0D;
    }

    public int getItemStackLimit(ItemStack stack) {
        IItemStack istack = NpcAPI.Instance().getIItemStack(stack);
        return istack instanceof ScriptCustomItem ? (new ScriptCustomItem(stack)).getMaxStackSize() : super.getItemStackLimit(stack);
    }

    public boolean isItemTool(ItemStack stack)
    {
        IItemStack istack = NpcAPI.Instance().getIItemStack(stack);
        return istack instanceof ScriptCustomItem ? (new ScriptCustomItem(stack)).isTool() : super.isItemTool(stack);
    }

    public float getDigSpeed(ItemStack stack, Block block, int metadata)
    {
        IItemStack istack = NpcAPI.Instance().getIItemStack(stack);
        return istack instanceof ScriptCustomItem ? (new ScriptCustomItem(stack)).getDigSpeed() : super.getDigSpeed(stack, block, metadata);
    }

    public boolean isValidArmor(ItemStack stack, int armorType, Entity entity)
    {
        IItemStack istack = NpcAPI.Instance().getIItemStack(stack);

        if((new ScriptCustomItem(stack)).getArmorType() == -1)
            return true;

        return istack instanceof ScriptCustomItem ? armorType == (new ScriptCustomItem(stack)).getArmorType() : super.isValidArmor(stack, armorType, entity);
    }

    public int getItemEnchantability(ItemStack stack)
    {
        IItemStack istack = NpcAPI.Instance().getIItemStack(stack);
        return istack instanceof ScriptCustomItem ? (new ScriptCustomItem(stack)).getEnchantability() : super.getItemEnchantability(stack);
    }

    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        IItemStack istack = NpcAPI.Instance().getIItemStack(stack);
        ItemEvent.AttackEvent eve = new ItemEvent.AttackEvent( (ScriptCustomItem) istack, ScriptController.Instance.getScriptForEntity(attacker), 1, ScriptController.Instance.getScriptForEntity(target));
        return EventHooks.onScriptItemAttack((ScriptCustomItem) istack, eve);
    }

    public EnumAction getItemUseAction(ItemStack stack)
    {
        IItemStack istack = NpcAPI.Instance().getIItemStack(stack);
        if (istack instanceof ScriptCustomItem) {
            switch (((ScriptCustomItem) istack).getItemUseAction()) {
                case 0:
                    return EnumAction.none;
                case 1:
                    return EnumAction.block;
                case 2:
                    return EnumAction.bow;
                case 3:
                    return EnumAction.eat;
                case 4:
                    return EnumAction.drink;
            }
        }
        return super.getItemUseAction(stack);
    }

    @Override
    public void renderSpecial() {
        GL11.glTranslatef(0.135F, 0.2F, 0.07F);
    }
}
