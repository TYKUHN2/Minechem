package minechem.utils;

import minechem.init.ModGlobals;

public class TimeHelper {

	public static String getTimeFromTicks(long ticks) {
		if (ticks < 0) {
			return MinechemUtil.getLocalString("minechem.unstable", true);
		}
		String timeLeft = "";
		String hourabbr = MinechemUtil.getLocalString("minechem.hour.abbr", true);
		String minabbr = MinechemUtil.getLocalString("minechem.min.abbr", true);
		String secabbr = MinechemUtil.getLocalString("minechem.sec.abbr", true);

		if (ticks < ModGlobals.TICKS_PER_DAY) {
			int hrs = (int) (ticks / ModGlobals.TICKS_PER_HOUR);
			ticks = ticks - (ModGlobals.TICKS_PER_HOUR * hrs);
			if (hrs > 0) {
				if (hourabbr.isEmpty() || hourabbr.equals("minechem.hour.abbr")) {
					timeLeft = hrs + "hr ";
				}
				else {
					timeLeft = hrs + hourabbr + " ";
				}
			}
		}
		if (ticks < ModGlobals.TICKS_PER_HOUR) {
			int mins = (int) (ticks / ModGlobals.TICKS_PER_MINUTE);
			ticks = ticks - (ModGlobals.TICKS_PER_MINUTE * mins);
			if (mins > 0) {
				if (minabbr.isEmpty() || minabbr.equals("minechem.min.abbr")) {
					timeLeft = timeLeft + mins + "m";
				}
				else {
					timeLeft = timeLeft + mins + minabbr;
				}
			}
		}
		if (ticks < ModGlobals.TICKS_PER_MINUTE) {
			int secs = (int) (ticks / ModGlobals.TICKS_PER_SECOND);
			ticks = ticks - (ModGlobals.TICKS_PER_SECOND * secs);
			if (secs > 0) {
				if (!timeLeft.equals("")) {
					timeLeft += " ";
				}
				if (secabbr.isEmpty() || secabbr.equals("minechem.sec.abbr")) {
					timeLeft = timeLeft + secs + "s";
				}
				else {
					timeLeft = timeLeft + secs + secabbr;
				}
			}
		}
		return timeLeft;
	}

	public static int getSecondsFromTicks(long ticks) {
		return (int) (ticks / ModGlobals.TICKS_PER_SECOND);
	}

	public static String getTimeFormattedFromSeconds(int totalSeconds) {
		String hourabbr = MinechemUtil.getLocalString("minechem.hour.abbr", true);
		String minabbr = MinechemUtil.getLocalString("minechem.min.abbr", true);
		String secabbr = MinechemUtil.getLocalString("minechem.sec.abbr", true);
		if (hourabbr.isEmpty() || hourabbr.equals("minechem.hour.abbr")) {
			hourabbr = "H";
		}
		if (minabbr.isEmpty() || minabbr.equals("minechem.min.abbr")) {
			minabbr = "M";
		}
		if (secabbr.isEmpty() || secabbr.equals("minechem.sec.abbr")) {
			secabbr = "S";
		}
		int hours = (totalSeconds % (24 * 3600)) / 3600;
		int minutes = totalSeconds % (24 * 3600);
		minutes %= 3600;
		minutes = minutes / 60;
		int seconds = totalSeconds % (24 * 3600);
		seconds %= 3600;
		seconds %= 60;
		String timeString = "";
		if (hours > 0) {
			timeString = hours + "" + hourabbr;
		}
		if (minutes > 0) {
			timeString += " " + minutes + "" + minabbr;
		}
		if (seconds > 0) {
			timeString += " " + seconds + "" + secabbr;
		}
		return timeString;
	}

}
