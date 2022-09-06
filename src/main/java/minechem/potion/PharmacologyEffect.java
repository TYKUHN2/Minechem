package minechem.potion;

import java.util.List;

import com.google.common.collect.Lists;

import minechem.init.ModGlobals;
import minechem.utils.MinechemUtil;
import minechem.utils.PotionHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;

public abstract class PharmacologyEffect {

	private final String colour;

	PharmacologyEffect(TextFormatting colour) {
		this.colour = colour.toString();
	}

	public abstract void applyEffect(EntityLivingBase entityLivingBase);

	public String getColour() {
		return colour;
	}

	public static class Food extends PharmacologyEffect {

		private final int level;
		private final float saturation;

		public Food(int level, float saturation) {
			super(TextFormatting.DARK_GREEN);
			this.level = level;
			this.saturation = saturation;
		}

		@Override
		public void applyEffect(EntityLivingBase entityLivingBase) {
			if (entityLivingBase instanceof EntityPlayer) {
				((EntityPlayer) entityLivingBase).getFoodStats().addStats(level, saturation);
			}
		}

		@Override
		public String toString() {
			return "Food Effect: " + level + ", Saturation: " + saturation;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Food) {
				Food other = (Food) obj;
				return other.level == level && other.saturation == saturation;
			}
			return false;
		}
	}

	public static class Burn extends PharmacologyEffect {

		private final int duration;

		public Burn(int duration) {
			super(TextFormatting.RED);
			this.duration = duration;
		}

		@Override
		public void applyEffect(EntityLivingBase entityLivingBase) {
			entityLivingBase.setFire(duration);
		}

		@Override
		public String toString() {
			return "Burn Effect: " + duration + " s";
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Burn) {
				Burn other = (Burn) obj;
				return other.duration == duration;
			}
			return false;
		}
	}

	public static class Cure extends PharmacologyEffect {
		private final Potion potion;

		public Cure() {
			this((Potion) null);
		}

		public Cure(Potion potion) {
			super(TextFormatting.AQUA);
			this.potion = potion;
		}

		public Cure(String potionName) {
			this(PotionHelper.getPotionByName(potionName));
		}

		@Override
		public void applyEffect(EntityLivingBase entityLivingBase) {
			if (potion == null) {
				List<PotionEffect> effectsToRemove = Lists.newArrayList();
				for (PotionEffect potionEffect : entityLivingBase.getActivePotionEffects()) {
					//if (potionEffect.getCurativeItems().size() > 0) {
					if (potionEffect.getPotion().isBadEffect()) {
						effectsToRemove.add(potionEffect);
					}
				}
				for (PotionEffect effect : effectsToRemove) {
					entityLivingBase.removePotionEffect(effect.getPotion());
				}
			}
			else {
				entityLivingBase.removePotionEffect(potion);
			}
		}

		@Override
		public String toString() {
			return "Cure Effect: " + (potion == null ? "all" : MinechemUtil.getLocalString(potion.getName()));
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Cure) {
				Cure other = (Cure) obj;
				return other.potion == potion;
			}
			return false;
		}
	}

	public static class Damage extends PharmacologyEffect {

		private final float damage;

		public Damage(float damage) {
			super(TextFormatting.GOLD);
			this.damage = damage;
		}

		@Override
		public void applyEffect(EntityLivingBase entityLivingBase) {
			entityLivingBase.attackEntityFrom(DamageSource.GENERIC, damage);
		}

		@Override
		public String toString() {
			float print = damage / 2;
			return "Damage Effect: " + print + " heart" + (print == 1 ? "" : "s");
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Damage) {
				Damage other = (Damage) obj;
				return other.damage == damage;
			}
			return false;
		}
	}

	public static class Magic extends PharmacologyEffect {

		private int duration;
		private int power;
		private final Potion potion;

		public Magic(String potionName, int power, int duration) {
			this(PotionHelper.getPotionByName(potionName), power, duration);
		}

		public Magic(String potionName, int duration) {
			this(potionName, 0, duration);
		}

		public Magic(Potion potion, int duration) {
			this(potion, 0, duration);
		}

		public Magic(Potion potion, int power, int duration) {
			super(TextFormatting.DARK_PURPLE);
			this.duration = duration;
			this.potion = potion;
			this.power = power;
		}

		@Override
		public void applyEffect(EntityLivingBase entityLivingBase) {
			entityLivingBase.addPotionEffect(new PotionEffect(potion, duration * ModGlobals.TICKS_PER_SECOND, power));
		}

		@Override
		public String toString() {
			if (potion != null) {
				return "Potion Effect: " + MinechemUtil.getLocalString(potion.getName()) + ", Duration: " + duration + ", Power: " + power;
			}
			return "";
		}

		public Potion getPotion() {
			return potion;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Magic) {
				Magic other = (Magic) obj;
				return other.duration == duration && other.potion == potion && other.power == power;
			}
			return false;
		}
	}

}
