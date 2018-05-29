package minechem.init;

import org.apache.logging.log4j.Level;

import net.minecraftforge.fml.common.FMLLog;

/**
 * Helper class for logging
 *
 * @author way2muchnoise
 */
public class ModLogger {
	/**
	 * General logging method
	 *
	 * @param level Level of the log
	 * @param obj object to log
	 */
	public static void log(Level level, Object obj) {
		FMLLog.log.log(level, String.valueOf(obj));
	}

	/**
	 * Used for logging when debug is turned on in the config
	 *
	 * @param obj object to log
	 */
	public static void debug(Object obj) {
		if (ModConfig.DebugMode) {
			log(Level.DEBUG, obj);
		}
	}

	/**
	 * Used for logging in any case
	 *
	 * @param obj object to log
	 */
	public static void info(Object obj) {
		log(Level.INFO, obj);
	}
}
