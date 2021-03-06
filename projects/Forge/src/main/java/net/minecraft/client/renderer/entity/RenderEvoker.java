package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelEvoker;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderEvoker extends RenderLiving<EntityMob>
{
    private static final ResourceLocation EVOKER_ILLAGER = new ResourceLocation("textures/entity/illager/evoker.png");

    public RenderEvoker(RenderManager p_i47207_1_)
    {
        super(p_i47207_1_, new ModelEvoker(0.0F), 0.5F);
    }

    protected ResourceLocation getEntityTexture(EntityMob entity)
    {
        return EVOKER_ILLAGER;
    }

    protected void preRenderCallback(EntityMob entitylivingbaseIn, float partialTickTime)
    {
        float f = 0.9375F;
        GlStateManager.scale(0.9375F, 0.9375F, 0.9375F);
    }
}