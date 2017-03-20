package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelEvoker extends ModelIllager
{
    public ModelEvoker(float p_i47228_1_)
    {
        super(p_i47228_1_, 0.0F, 64, 64);
    }

    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        EntityEvoker entityevoker = (EntityEvoker)entityIn;

        if (entityevoker.isCastingSpell())
        {
            this.rightArm.render(scale);
            this.leftArm.render(scale);
        }
        else
        {
            this.arms.render(scale);
        }
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        this.rightArm.rotationPointZ = 0.0F;
        this.rightArm.rotationPointX = -5.0F;
        this.leftArm.rotationPointZ = 0.0F;
        this.leftArm.rotationPointX = 5.0F;
        this.rightArm.rotateAngleX = MathHelper.cos(ageInTicks * 0.6662F) * 0.25F;
        this.leftArm.rotateAngleX = MathHelper.cos(ageInTicks * 0.6662F) * 0.25F;
        this.rightArm.rotateAngleZ = 2.3561945F;
        this.leftArm.rotateAngleZ = -2.3561945F;
        this.rightArm.rotateAngleY = 0.0F;
        this.leftArm.rotateAngleY = 0.0F;
    }
}