package minechem.utils;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

// Written by calclavia
// Taken from UE for legacy code
public class Vector3 implements Cloneable {

	public double x;
	public double y;
	public double z;

	public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3() {
		this(0, 0, 0);
	}

	public Vector3(Vector3 vector) {
		this(vector.x, vector.y, vector.z);
	}

	public Vector3(double amount) {
		this(amount, amount, amount);
	}

	public Vector3(Entity par1) {
		this(par1.posX, par1.posY, par1.posZ);
	}

	public Vector3(TileEntity par1) {
		this(par1.getPos().getX(), par1.getPos().getY(), par1.getPos().getZ());
	}

	public Vector3(Vec3d par1) {
		this(par1.x, par1.y, par1.z);

	}

	public Vector3(Position par1) {
		this(par1.x, par1.y, par1.z);
	}

	public Vector3(ChunkPos par1) {
		this(par1.x, 0, par1.z);
	}

	public Vector3(EnumFacing direction) {
		this(direction.getFrontOffsetX(), direction.getFrontOffsetY(), direction.getFrontOffsetZ());
	}

	/**
	 * Loads a Vector3 from an NBT compound.
	 */
	public Vector3(NBTTagCompound nbt) {
		this(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"));
	}

	/**
	 * Get a Vector3 based on the rotationYaw and rotationPitch.
	 *
	 * @param rotationYaw - Degree
	 * @param rotationPitch- Degree
	 */
	public Vector3(float rotationYaw, float rotationPitch) {
		this(Math.cos(Math.toRadians(rotationYaw + 90)), Math.sin(Math.toRadians(-rotationPitch)), Math.sin(Math.toRadians(rotationYaw + 90)));
	}

	/**
	 * Returns the coordinates as integers, ideal for block placement.
	 */
	public int intX() {
		return (int) Math.floor(x);
	}

	public int intY() {
		return (int) Math.floor(y);
	}

	public int intZ() {
		return (int) Math.floor(z);
	}

	public float floatX() {
		return (float) x;
	}

	public float floatY() {
		return (float) y;
	}

	public float floatZ() {
		return (float) z;
	}

	/**
	 * Makes a new copy of this Vector. Prevents variable referencing problems.
	 */
	@Override
	public Vector3 clone() {
		return new Vector3(this);
	}

	/**
	 * Easy block access functions.
	 *
	 * @param world
	 * @return
	 */
	public Block getBlock(IBlockAccess world) {
		return world.getBlockState(new BlockPos(intX(), intY(), intZ())).getBlock();
	}

	public int getBlockMetadata(IBlockAccess world) {
		return world.getBlockState(new BlockPos(intX(), intY(), intZ())).getBlock().getMetaFromState(world.getBlockState(new BlockPos(intX(), intY(), intZ())));
	}

	public TileEntity getTileEntity(IBlockAccess world) {
		return world.getTileEntity(new BlockPos(intX(), intY(), intZ()));
	}

	public boolean setBlock(World world, Block block, int metadata, int notify) {
		return world.setBlockState(new BlockPos(intX(), intY(), intZ()), block.getStateFromMeta(metadata), notify);
	}

	public boolean setBlock(World world, Block block, int metadata) {
		return this.setBlock(world, block, metadata, 3);
	}

	public boolean setBlock(World world, Block block) {
		return this.setBlock(world, block, 0);
	}

	/**
	 * Converts this vector three into a Minecraft Vec3 object
	 */
	public Vec3d toVec3() {
		return new Vec3d(x, y, z);
	}

	/**
	 * Converts Vector3 into a ForgeDirection.
	 */
	public EnumFacing toEnumFacing() {
		for (EnumFacing direction : EnumFacing.HORIZONTALS) {
			if (x == direction.getFrontOffsetX() && y == direction.getFrontOffsetY() && z == direction.getFrontOffsetZ()) {
				return direction;
			}
		}

		return EnumFacing.values()[-1];
	}

	public double getMagnitude() {
		return Math.sqrt(getMagnitudeSquared());
	}

	public double getMagnitudeSquared() {
		return x * x + y * y + z * z;
	}

	public Vector3 normalize() {
		double d = getMagnitude();

		if (d != 0) {
			this.scale(1 / d);
		}

		return this;
	}

	/**
	 * Gets the distance between two vectors
	 *
	 * @return The distance
	 */
	public static double distance(Vector3 vec1, Vector3 vec2) {
		return vec1.distance(vec2);
	}

	@Deprecated
	public double distanceTo(Vector3 vector3) {
		return this.distance(vector3);
	}

	public double distance(Vector3 compare) {
		Vector3 difference = clone().difference(compare);
		return difference.getMagnitude();
	}

	/**
	 * Multiplies the vector by negative one.
	 */
	public Vector3 invert() {
		this.scale(-1);
		return this;
	}

	public Vector3 translate(EnumFacing par1) {
		x += par1.getFrontOffsetX();
		y += par1.getFrontOffsetY();
		z += par1.getFrontOffsetZ();
		return this;
	}

	public Vector3 translate(double par1) {
		x += par1;
		y += par1;
		z += par1;
		return this;
	}

	public static Vector3 translate(Vector3 translate, Vector3 par1) {
		translate.x += par1.x;
		translate.y += par1.y;
		translate.z += par1.z;
		return translate;
	}

	public static Vector3 translate(Vector3 translate, double par1) {
		translate.x += par1;
		translate.y += par1;
		translate.z += par1;
		return translate;
	}

	@Deprecated
	public Vector3 add(Vector3 amount) {
		return translate(amount);
	}

	@Deprecated
	public Vector3 add(double amount) {
		return translate(amount);
	}

	@Deprecated
	public Vector3 subtract(Vector3 amount) {
		return this.translate(amount.clone().invert());
	}

	@Deprecated
	public Vector3 subtract(double amount) {
		return this.translate(-amount);
	}

	public Vector3 difference(Vector3 amount) {
		return this.translate(amount.clone().invert());
	}

	public Vector3 difference(double amount) {
		return this.translate(-amount);
	}

	public Vector3 scale(double amount) {
		x *= amount;
		y *= amount;
		z *= amount;
		return this;
	}

	public Vector3 scale(Vector3 amount) {
		x *= amount.x;
		y *= amount.y;
		z *= amount.z;
		return this;
	}

	public static Vector3 scale(Vector3 vec, double amount) {
		return vec.scale(amount);
	}

	public static Vector3 scale(Vector3 vec, Vector3 amount) {
		return vec.scale(amount);
	}

	@Deprecated
	public Vector3 multiply(double amount) {
		return this.scale(amount);
	}

	@Deprecated
	public Vector3 multiply(Vector3 amount) {
		return this.scale(amount);
	}

	/**
	 * Static versions of a lot of functions
	 */
	@Deprecated
	public static Vector3 subtract(Vector3 par1, Vector3 par2) {
		return new Vector3(par1.x - par2.x, par1.y - par2.y, par1.z - par2.z);
	}

	@Deprecated
	public static Vector3 add(Vector3 par1, Vector3 par2) {
		return new Vector3(par1.x + par2.x, par1.y + par2.y, par1.z + par2.z);
	}

	@Deprecated
	public static Vector3 add(Vector3 par1, double par2) {
		return new Vector3(par1.x + par2, par1.y + par2, par1.z + par2);
	}

	@Deprecated
	public static Vector3 multiply(Vector3 vec1, Vector3 vec2) {
		return new Vector3(vec1.x * vec2.x, vec1.y * vec2.y, vec1.z * vec2.z);
	}

	@Deprecated
	public static Vector3 multiply(Vector3 vec1, double vec2) {
		return new Vector3(vec1.x * vec2, vec1.y * vec2, vec1.z * vec2);
	}

	public Vector3 round() {
		return new Vector3(Math.round(x), Math.round(y), Math.round(z));
	}

	public Vector3 ceil() {
		return new Vector3(Math.ceil(x), Math.ceil(y), Math.ceil(z));
	}

	public Vector3 floor() {
		return new Vector3(Math.floor(x), Math.floor(y), Math.floor(z));
	}

	public Vector3 toRound() {
		x = Math.round(x);
		y = Math.round(y);
		z = Math.round(z);
		return this;
	}

	public Vector3 toCeil() {
		x = Math.ceil(x);
		y = Math.ceil(y);
		z = Math.ceil(z);
		return this;
	}

	public Vector3 toFloor() {
		x = Math.floor(x);
		y = Math.floor(y);
		z = Math.floor(z);
		return this;
	}

	/**
	 * Gets all entities inside of this position in block space.
	 */
	public List<Entity> getEntitiesWithin(World world, Class<? extends Entity> par1Class) {
		return world.getEntitiesWithinAABB(par1Class, new AxisAlignedBB(intX(), intY(), intZ(), intX() + 1, intY() + 1, intZ() + 1));
	}

	/**
	 * Gets a position relative to a position's side
	 *
	 * @param side - The side. 0-5
	 * @return The position relative to the original position's side
	 */
	public Vector3 modifyPositionFromSide(EnumFacing side, double amount) {
		return this.translate(new Vector3(side).scale(amount));
	}

	public Vector3 modifyPositionFromSide(EnumFacing side) {
		this.modifyPositionFromSide(side, 1);
		return this;
	}

	/**
	 * Cross product functions
	 *
	 * @return The cross product between this vector and another.
	 */
	public Vector3 toCrossProduct(Vector3 compare) {
		double newX = y * compare.z - z * compare.y;
		double newY = z * compare.x - x * compare.z;
		double newZ = x * compare.y - y * compare.x;
		x = newX;
		y = newY;
		z = newZ;
		return this;
	}

	public Vector3 crossProduct(Vector3 compare) {
		return clone().toCrossProduct(compare);
	}

	public Vector3 xCrossProduct() {
		return new Vector3(0.0D, z, -y);
	}

	public Vector3 zCrossProduct() {
		return new Vector3(-y, x, 0.0D);
	}

	public double dotProduct(Vector3 vec2) {
		return x * vec2.x + y * vec2.y + z * vec2.z;
	}

	/**
	 * @return The perpendicular vector.
	 */
	public Vector3 getPerpendicular() {
		if (z == 0.0F) {
			return zCrossProduct();
		}

		return xCrossProduct();
	}

	/**
	 * @return True if this Vector3 is zero.
	 */
	public boolean isZero() {
		return (x == 0) && (y == 0) && (z == 0);
	}

	/**
	 * Rotate by a this vector around an axis.
	 *
	 * @return The new Vector3 rotation.
	 */
	public Vector3 rotate(float angle, Vector3 axis) {
		return translateMatrix(getRotationMatrix(angle, axis), clone());
	}

	public double[] getRotationMatrix(float angle) {
		double[] matrix = new double[16];
		Vector3 axis = clone().normalize();
		double x = axis.x;
		double y = axis.y;
		double z = axis.z;
		angle *= 0.0174532925D;
		float cos = (float) Math.cos(angle);
		float ocos = 1.0F - cos;
		float sin = (float) Math.sin(angle);
		matrix[0] = (x * x * ocos + cos);
		matrix[1] = (y * x * ocos + z * sin);
		matrix[2] = (x * z * ocos - y * sin);
		matrix[4] = (x * y * ocos - z * sin);
		matrix[5] = (y * y * ocos + cos);
		matrix[6] = (y * z * ocos + x * sin);
		matrix[8] = (x * z * ocos + y * sin);
		matrix[9] = (y * z * ocos - x * sin);
		matrix[10] = (z * z * ocos + cos);
		matrix[15] = 1.0F;
		return matrix;
	}

	public static Vector3 translateMatrix(double[] matrix, Vector3 translation) {
		double x = translation.x * matrix[0] + translation.y * matrix[1] + translation.z * matrix[2] + matrix[3];
		double y = translation.x * matrix[4] + translation.y * matrix[5] + translation.z * matrix[6] + matrix[7];
		double z = translation.x * matrix[8] + translation.y * matrix[9] + translation.z * matrix[10] + matrix[11];
		translation.x = x;
		translation.y = y;
		translation.z = z;
		return translation;
	}

	public static double[] getRotationMatrix(float angle, Vector3 axis) {
		return axis.getRotationMatrix(angle);
	}

	/**
	 * Rotates this Vector by a yaw, pitch and roll value.
	 */
	public void rotate(double yaw, double pitch, double roll) {
		double yawRadians = Math.toRadians(yaw);
		double pitchRadians = Math.toRadians(pitch);
		double rollRadians = Math.toRadians(roll);

		double x = this.x;
		double y = this.y;
		double z = this.z;

		this.x = x * Math.cos(yawRadians) * Math.cos(pitchRadians) + z * (Math.cos(yawRadians) * Math.sin(pitchRadians) * Math.sin(rollRadians) - Math.sin(yawRadians) * Math.cos(rollRadians)) + y * (Math.cos(yawRadians) * Math.sin(pitchRadians) * Math.cos(rollRadians) + Math.sin(yawRadians) * Math.sin(rollRadians));
		this.z = x * Math.sin(yawRadians) * Math.cos(pitchRadians) + z * (Math.sin(yawRadians) * Math.sin(pitchRadians) * Math.sin(rollRadians) + Math.cos(yawRadians) * Math.cos(rollRadians)) + y * (Math.sin(yawRadians) * Math.sin(pitchRadians) * Math.cos(rollRadians) - Math.cos(yawRadians) * Math.sin(rollRadians));
		this.y = -x * Math.sin(pitchRadians) + z * Math.cos(pitchRadians) * Math.sin(rollRadians) + y * Math.cos(pitchRadians) * Math.cos(rollRadians);
	}

	/**
	 * Rotates a point by a yaw and pitch around the anchor 0,0 by a specific angle.
	 */
	public void rotate(double yaw, double pitch) {
		this.rotate(yaw, pitch, 0);
	}

	public void rotate(double yaw) {
		double yawRadians = Math.toRadians(yaw);

		double x = this.x;
		double z = this.z;

		if (yaw != 0) {
			this.x = x * Math.cos(yawRadians) - z * Math.sin(yawRadians);
			this.z = x * Math.sin(yawRadians) + z * Math.cos(yawRadians);
		}
	}

	/**
	 * Gets the delta look position based on the rotation yaw and pitch. Minecraft coordinates are messed up. Y and Z are flipped. Yaw is displaced by 90 degrees. Pitch is inversed.
	 *
	 * @param rotationYaw
	 * @param rotationPitch
	 */
	public static Vector3 getDeltaPositionFromRotation(float rotationYaw, float rotationPitch) {
		return new Vector3(rotationYaw, rotationPitch);
	}

	/**
	 * Gets the angle between this vector and another vector.
	 *
	 * @return Angle in degrees
	 */
	public double getAngle(Vector3 vec2) {
		return anglePreNorm(clone().normalize(), vec2.clone().normalize());
	}

	public static double getAngle(Vector3 vec1, Vector3 vec2) {
		return vec1.getAngle(vec2);
	}

	public double anglePreNorm(Vector3 vec2) {
		return Math.acos(dotProduct(vec2));
	}

	public static double anglePreNorm(Vector3 vec1, Vector3 vec2) {
		return Math.acos(vec1.clone().dotProduct(vec2));
	}

	/**
	 * Loads a Vector3 from an NBT compound.
	 */
	@Deprecated
	public static Vector3 readFromNBT(NBTTagCompound nbt) {
		return new Vector3(nbt);
	}

	/**
	 * Saves this Vector3 to disk
	 *
	 * @param nbt - The NBT compound object to save the data in
	 */
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setDouble("x", x);
		nbt.setDouble("y", y);
		nbt.setDouble("z", z);
		return nbt;
	}

	public static Vector3 UP() {
		return new Vector3(0, 1, 0);
	}

	public static Vector3 DOWN() {
		return new Vector3(0, -1, 0);
	}

	public static Vector3 NORTH() {
		return new Vector3(0, 0, -1);
	}

	public static Vector3 SOUTH() {
		return new Vector3(0, 0, 1);
	}

	public static Vector3 WEST() {
		return new Vector3(-1, 0, 0);
	}

	public static Vector3 EAST() {
		return new Vector3(1, 0, 0);
	}

	/**
	 * RayTrace Code, retrieved from MachineMuse.
	 *
	 * @author MachineMuse
	 */
	public RayTraceResult rayTrace(World world, float rotationYaw, float rotationPitch, boolean collisionFlag, double reachDistance) {
		// Somehow this destroys the playerPosition vector -.-
		RayTraceResult pickedBlock = rayTraceBlocks(world, rotationYaw, rotationPitch, collisionFlag, reachDistance);
		RayTraceResult pickedEntity = this.rayTraceEntities(world, rotationYaw, rotationPitch, reachDistance);

		if (pickedBlock == null) {
			return pickedEntity;
		}
		else if (pickedEntity == null) {
			return pickedBlock;
		}
		else {
			double dBlock = this.distance(new Vector3(pickedBlock.hitVec));
			double dEntity = this.distance(new Vector3(pickedEntity.hitVec));

			if (dEntity < dBlock) {
				return pickedEntity;
			}
			else {
				return pickedBlock;
			}
		}
	}

	public RayTraceResult rayTraceBlocks(World world, float rotationYaw, float rotationPitch, boolean collisionFlag, double reachDistance) {
		Vector3 lookVector = Vector3.getDeltaPositionFromRotation(rotationYaw, rotationPitch);
		Vector3 reachPoint = clone().translate(lookVector.clone().scale(reachDistance));
		return world.rayTraceBlocks(toVec3(), reachPoint.toVec3(), collisionFlag, !collisionFlag, false);
	}

	private Vector3 translate(Vector3 scale) {
		return scale.clone();
	}

	@Deprecated
	public RayTraceResult rayTraceEntities(World world, float rotationYaw, float rotationPitch, boolean collisionFlag, double reachDistance) {
		return this.rayTraceEntities(world, rotationYaw, rotationPitch, reachDistance);
	}

	public RayTraceResult rayTraceEntities(World world, float rotationYaw, float rotationPitch, double reachDistance) {
		return this.rayTraceEntities(world, getDeltaPositionFromRotation(rotationYaw, rotationPitch).scale(reachDistance));
	}

	/**
	 * Does an entity raytrace.
	 *
	 * @param world - The world object.
	 * @param target - The rotation in terms of Vector3. Convert using getDeltaPositionFromRotation()
	 * @return The target hit.
	 */
	public RayTraceResult rayTraceEntities(World world, Vector3 target) {
		RayTraceResult pickedEntity = null;
		Vec3d startingPosition = toVec3();
		Vec3d look = target.toVec3();
		double reachDistance = this.distance(target);
		Vec3d reachPoint = new Vec3d(startingPosition.x + look.x * reachDistance, startingPosition.y + look.y * reachDistance, startingPosition.z + look.z * reachDistance);

		double checkBorder = 1.1 * reachDistance;
		AxisAlignedBB boxToScan = new AxisAlignedBB(-checkBorder, -checkBorder, -checkBorder, checkBorder, checkBorder, checkBorder).offset(x, y, z);

		List<Entity> entitiesHit = world.getEntitiesWithinAABBExcludingEntity(null, boxToScan);
		double closestEntity = reachDistance;

		if (entitiesHit == null || entitiesHit.isEmpty()) {
			return null;
		}
		for (Entity entityHit : entitiesHit) {
			if (entityHit != null && entityHit.canBeCollidedWith() && entityHit.getCollisionBoundingBox() != null) {
				float border = entityHit.getCollisionBorderSize();
				AxisAlignedBB aabb = entityHit.getCollisionBoundingBox().expand(border, border, border);
				RayTraceResult hitMOP = aabb.calculateIntercept(startingPosition, reachPoint);

				if (hitMOP != null) {
					if (aabb.contains(startingPosition)) {
						if (0.0D < closestEntity || closestEntity == 0.0D) {
							pickedEntity = new RayTraceResult(entityHit);
							pickedEntity.hitVec = hitMOP.hitVec;
							closestEntity = 0.0D;
						}
					}
					else {
						double distance = startingPosition.distanceTo(hitMOP.hitVec);

						if (distance < closestEntity || closestEntity == 0.0D) {
							pickedEntity = new RayTraceResult(entityHit);
							pickedEntity.hitVec = hitMOP.hitVec;
							closestEntity = distance;
						}
					}
				}
			}
		}
		return pickedEntity;
	}

	@Override
	public int hashCode() {
		return ("X:" + x + "Y:" + y + "Z:" + z).hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Vector3) {
			Vector3 vector3 = (Vector3) o;
			return x == vector3.x && y == vector3.y && z == vector3.z;
		}

		return false;
	}

	@Override
	public String toString() {
		return "Vector3 [" + x + "," + y + "," + z + "]";
	}

}
