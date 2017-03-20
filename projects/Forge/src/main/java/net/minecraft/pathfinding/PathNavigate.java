package net.minecraft.pathfinding;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;

public abstract class PathNavigate
{
    protected EntityLiving theEntity;
    protected World world;
    @Nullable
    protected Path currentPath;
    protected double speed;
    private final IAttributeInstance pathSearchRange;
    private int totalTicks;
    private int ticksAtLastPos;
    private Vec3d lastPosCheck = Vec3d.ZERO;
    private Vec3d timeoutCachedNode = Vec3d.ZERO;
    private long timeoutTimer;
    private long lastTimeoutCheck;
    private double timeoutLimit;
    private float maxDistanceToWaypoint = 0.5F;
    private boolean tryUpdatePath;
    private long lastTimeUpdated;
    protected NodeProcessor nodeProcessor;
    private BlockPos targetPos;
    private final PathFinder pathFinder;

    public PathNavigate(EntityLiving entitylivingIn, World worldIn)
    {
        this.theEntity = entitylivingIn;
        this.world = worldIn;
        this.pathSearchRange = entitylivingIn.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
        this.pathFinder = this.getPathFinder();
    }

    protected abstract PathFinder getPathFinder();

    public void setSpeed(double speedIn)
    {
        this.speed = speedIn;
    }

    public float getPathSearchRange()
    {
        return (float)this.pathSearchRange.getAttributeValue();
    }

    public boolean canUpdatePathOnTimeout()
    {
        return this.tryUpdatePath;
    }

    public void updatePath()
    {
        if (this.world.getTotalWorldTime() - this.lastTimeUpdated > 20L)
        {
            if (this.targetPos != null)
            {
                this.currentPath = null;
                this.currentPath = this.getPathToPos(this.targetPos);
                this.lastTimeUpdated = this.world.getTotalWorldTime();
                this.tryUpdatePath = false;
            }
        }
        else
        {
            this.tryUpdatePath = true;
        }
    }

    @Nullable
    public final Path getPathToXYZ(double x, double y, double z)
    {
        return this.getPathToPos(new BlockPos(x, y, z));
    }

    @Nullable
    public Path getPathToPos(BlockPos pos)
    {
        if (!this.canNavigate())
        {
            return null;
        }
        else if (this.currentPath != null && !this.currentPath.isFinished() && pos.equals(this.targetPos))
        {
            return this.currentPath;
        }
        else
        {
            this.targetPos = pos;
            float f = this.getPathSearchRange();
            this.world.theProfiler.startSection("pathfind");
            BlockPos blockpos = new BlockPos(this.theEntity);
            int i = (int)(f + 8.0F);
            ChunkCache chunkcache = new ChunkCache(this.world, blockpos.add(-i, -i, -i), blockpos.add(i, i, i), 0);
            Path path = this.pathFinder.findPath(chunkcache, this.theEntity, this.targetPos, f);
            this.world.theProfiler.endSection();
            return path;
        }
    }

    @Nullable
    public Path getPathToEntityLiving(Entity entityIn)
    {
        if (!this.canNavigate())
        {
            return null;
        }
        else
        {
            BlockPos blockpos = new BlockPos(entityIn);

            if (this.currentPath != null && !this.currentPath.isFinished() && blockpos.equals(this.targetPos))
            {
                return this.currentPath;
            }
            else
            {
                this.targetPos = blockpos;
                float f = this.getPathSearchRange();
                this.world.theProfiler.startSection("pathfind");
                BlockPos blockpos1 = (new BlockPos(this.theEntity)).up();
                int i = (int)(f + 16.0F);
                ChunkCache chunkcache = new ChunkCache(this.world, blockpos1.add(-i, -i, -i), blockpos1.add(i, i, i), 0);
                Path path = this.pathFinder.findPath(chunkcache, this.theEntity, entityIn, f);
                this.world.theProfiler.endSection();
                return path;
            }
        }
    }

    public boolean tryMoveToXYZ(double x, double y, double z, double speedIn)
    {
        return this.setPath(this.getPathToXYZ(x, y, z), speedIn);
    }

    public boolean tryMoveToEntityLiving(Entity entityIn, double speedIn)
    {
        Path path = this.getPathToEntityLiving(entityIn);
        return path != null && this.setPath(path, speedIn);
    }

    public boolean setPath(@Nullable Path pathentityIn, double speedIn)
    {
        if (pathentityIn == null)
        {
            this.currentPath = null;
            return false;
        }
        else
        {
            if (!pathentityIn.isSamePath(this.currentPath))
            {
                this.currentPath = pathentityIn;
            }

            this.removeSunnyPath();

            if (this.currentPath.getCurrentPathLength() == 0)
            {
                return false;
            }
            else
            {
                this.speed = speedIn;
                Vec3d vec3d = this.getEntityPosition();
                this.ticksAtLastPos = this.totalTicks;
                this.lastPosCheck = vec3d;
                return true;
            }
        }
    }

    @Nullable
    public Path getPath()
    {
        return this.currentPath;
    }

    public void onUpdateNavigation()
    {
        ++this.totalTicks;

        if (this.tryUpdatePath)
        {
            this.updatePath();
        }

        if (!this.noPath())
        {
            if (this.canNavigate())
            {
                this.pathFollow();
            }
            else if (this.currentPath != null && this.currentPath.getCurrentPathIndex() < this.currentPath.getCurrentPathLength())
            {
                Vec3d vec3d = this.getEntityPosition();
                Vec3d vec3d1 = this.currentPath.getVectorFromIndex(this.theEntity, this.currentPath.getCurrentPathIndex());

                if (vec3d.yCoord > vec3d1.yCoord && !this.theEntity.onGround && MathHelper.floor(vec3d.xCoord) == MathHelper.floor(vec3d1.xCoord) && MathHelper.floor(vec3d.zCoord) == MathHelper.floor(vec3d1.zCoord))
                {
                    this.currentPath.setCurrentPathIndex(this.currentPath.getCurrentPathIndex() + 1);
                }
            }

            if (!this.noPath())
            {
                Vec3d vec3d2 = this.currentPath.getPosition(this.theEntity);

                if (vec3d2 != null)
                {
                    BlockPos blockpos = (new BlockPos(vec3d2)).down();
                    AxisAlignedBB axisalignedbb = this.world.getBlockState(blockpos).getBoundingBox(this.world, blockpos);
                    vec3d2 = vec3d2.subtract(0.0D, 1.0D - axisalignedbb.maxY, 0.0D);
                    this.theEntity.getMoveHelper().setMoveTo(vec3d2.xCoord, vec3d2.yCoord, vec3d2.zCoord, this.speed);
                }
            }
        }
    }

    protected void pathFollow()
    {
        Vec3d vec3d = this.getEntityPosition();
        int i = this.currentPath.getCurrentPathLength();

        for (int j = this.currentPath.getCurrentPathIndex(); j < this.currentPath.getCurrentPathLength(); ++j)
        {
            if ((double)this.currentPath.getPathPointFromIndex(j).yCoord != Math.floor(vec3d.yCoord))
            {
                i = j;
                break;
            }
        }

        this.maxDistanceToWaypoint = this.theEntity.width > 0.75F ? this.theEntity.width / 2.0F : 0.75F - this.theEntity.width / 2.0F;
        Vec3d vec3d1 = this.currentPath.getCurrentPos();

        if (MathHelper.abs((float)(this.theEntity.posX - (vec3d1.xCoord + 0.5D))) < this.maxDistanceToWaypoint && MathHelper.abs((float)(this.theEntity.posZ - (vec3d1.zCoord + 0.5D))) < this.maxDistanceToWaypoint && Math.abs(this.theEntity.posY - vec3d1.yCoord) < 1.0D)
        {
            this.currentPath.setCurrentPathIndex(this.currentPath.getCurrentPathIndex() + 1);
        }

        int k = MathHelper.ceil(this.theEntity.width);
        int l = MathHelper.ceil(this.theEntity.height);
        int i1 = k;

        for (int j1 = i - 1; j1 >= this.currentPath.getCurrentPathIndex(); --j1)
        {
            if (this.isDirectPathBetweenPoints(vec3d, this.currentPath.getVectorFromIndex(this.theEntity, j1), k, l, i1))
            {
                this.currentPath.setCurrentPathIndex(j1);
                break;
            }
        }

        this.checkForStuck(vec3d);
    }

    protected void checkForStuck(Vec3d positionVec3)
    {
        if (this.totalTicks - this.ticksAtLastPos > 100)
        {
            if (positionVec3.squareDistanceTo(this.lastPosCheck) < 2.25D)
            {
                this.clearPathEntity();
            }

            this.ticksAtLastPos = this.totalTicks;
            this.lastPosCheck = positionVec3;
        }

        if (this.currentPath != null && !this.currentPath.isFinished())
        {
            Vec3d vec3d = this.currentPath.getCurrentPos();

            if (vec3d.equals(this.timeoutCachedNode))
            {
                this.timeoutTimer += System.currentTimeMillis() - this.lastTimeoutCheck;
            }
            else
            {
                this.timeoutCachedNode = vec3d;
                double d0 = positionVec3.distanceTo(this.timeoutCachedNode);
                this.timeoutLimit = this.theEntity.getAIMoveSpeed() > 0.0F ? d0 / (double)this.theEntity.getAIMoveSpeed() * 1000.0D : 0.0D;
            }

            if (this.timeoutLimit > 0.0D && (double)this.timeoutTimer > this.timeoutLimit * 3.0D)
            {
                this.timeoutCachedNode = Vec3d.ZERO;
                this.timeoutTimer = 0L;
                this.timeoutLimit = 0.0D;
                this.clearPathEntity();
            }

            this.lastTimeoutCheck = System.currentTimeMillis();
        }
    }

    public boolean noPath()
    {
        return this.currentPath == null || this.currentPath.isFinished();
    }

    public void clearPathEntity()
    {
        this.currentPath = null;
    }

    protected abstract Vec3d getEntityPosition();

    protected abstract boolean canNavigate();

    protected boolean isInLiquid()
    {
        return this.theEntity.isInWater() || this.theEntity.isInLava();
    }

    protected void removeSunnyPath()
    {
    }

    protected abstract boolean isDirectPathBetweenPoints(Vec3d posVec31, Vec3d posVec32, int sizeX, int sizeY, int sizeZ);

    public boolean canEntityStandOnPos(BlockPos pos)
    {
        return this.world.getBlockState(pos.down()).isFullBlock();
    }

    public NodeProcessor getNodeProcessor()
    {
        return this.nodeProcessor;
    }
}