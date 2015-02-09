package com.igo.slapiclient;

import android.os.Bundle;

//** Converts data sent by the server to string */
public final class Data2String {
	public static String errorCode(int code) {
		switch (code) {
		case 0x0000:
			return "No error (0x0000)";
		case 0x0001:
			return "General message syntax error (0x0001)";
		case 0x0002:
			return "Unsupported message (0x0002)";
		case 0x0003:
			return "Unsupported parameter value (0x0003)";
		case 0x00FF:
			return "Unspecified error (0x00FF)";
		case 0x0100:
			return "General Route Planning Error (0x0100)";
		case 0x0101:
			return "Maps are isolated (0x0101)";
		case 0x0102:
			return "Prohibited/Isolated (0x0102)";
		case 0x0103:
			return "Out of memory (0x0103)";
		case 0x0104:
			return "Line graph corrupt (0x0104)";
		case 0x0105:
			return "Source is isolated (0x0105)";
		case 0x0106:
			return "Target is isolated (0x0106)";
		case 0x0107:
			return "Route is too long (0x0107)";
		case 0x0108:
			return "Create waypoint - no road found (0x0108)";
		case 0x0109:
			return "Internal error - destination unreachable (0x0109)";
		case 0x0210:
			return "Waypoint is unreachable (0x0210)";
		case 0x0211:
			return "Waypoint is unreachable with preferences (0x0211)";
		case 0x0212:
			return "Target is unreachable (0x0212)";
		case 0x0213:
			return "Target is unreachable - pedestrian (0x0213)";
		case 0x0214:
			return "Target is unreachable with preferences (0x0214)";
		case 0x0215:
			return "Target is unreachable with preferences - unpaved (0x0215)";
		case 0x0216:
			return "Target is unreachable with preferences - permit needed (0x0216)";
		case 0x0217:
			return "Truck on minor road (0x0217)";
		case 0x0300:
			return "General I/O error (0x0300)";
		case 0x0301:
			return "Serial I/O error (0x0301)";
		case 0x0302:
			return "File I/O error: file not found (0x0302)";
		case 0x0303:
			return "File I/O error: cannot read (0x0303)";
		case 0x0304:
			return "File I/O error: cannot write (0x0304)";
		case 0x0400:
			return "General Geocoding error (0x0400)";
		case 0x0401:
			return "Cannot find position for address (see Error Message for details) (0x0401)";
		case 0x0402:
			return "Cannot find address for requested/current position (0x0402)";
		case 0x0500:
			return "General GPS error (0x0500)";
		case 0x0501:
			return "GPS Position is not valid (0x0501)";
		case 0x0600:
			return "General Language error (0x0600)";
		case 0x0601:
			return "Language not found (0x0601)";
		case 0x0602:
			return "Language already selected (0x0602)";
		default:
			return "Unknown error ("+Integer.toString(code,16)+")";
		}
	}

	public static String planMode(int code) {
		switch (code) {
		case 0:
			return "Current (0)";
		case 1:
			return "Fastest (1)";
		case 2:
			return "Shortest (2)";
		case 3:
			return "Easy (3)";
		case 4:
			return "Economic (4)";
		default:
			return "Unknown plan mode ("+code+")";
		}
	}

	public static String vehicleType(int code) {
		switch (code) {
		case 0:
			return "Current (0)";
		case 1:
			return "Car (1)";
		case 2:
			return "Pedestrian (2)";
		case 3:
			return "Bicycle (3)";
		case 4:
			return "Emergency (4)";
		case 5:
			return "Bus (5)";
		case 6:
			return "Taxi (6)";
		case 7:
			return "Truck Profile 1 (7)";
		case 8:
			return "Truck Profile 2 (8)";
		case 9:
			return "Truck Profile 3 (9)";
		default:
			return "Unknown vehicle mode ("+code+")";
		}
	}

	public static String freightCategory(int code) {
		switch (code) {
		case 0:
			return "Explosives (0)";
		case 1:
			return "Flammable Gas (1)";
		case 2:
			return "Nonflammable Gas (2)";
		case 3:
			return "Poisonous Gas (3)";
		case 4:
			return "Flammable Liquid (4)";
		case 5:
			return "Flammable Solids (5)";
		case 6:
			return "Spontaneously Combustible (6)";
		case 7:
			return "Dangerous when Wet (7)";
		case 8:
			return "Oxidizing Agents (8)";
		case 9:
			return "Organic Peroxides (9)";
		case 10:
			return "Poison (10)";
		case 11:
			return "Biohazard (11)";
		case 12:
			return "Radioactive Substances (12)";
		case 13:
			return "Corrosive Substances (13)";
		case 14:
			return "Miscellaneous Substances (14)";
		default:
			return "Unknown freight code ("+code+")";
		}
	}

	public static String gpsFixValue(int code) {
		switch (code) {
		case 0:
			return "No Fix (0)";
		case 1:
			return "2D Fix (1)";
		case 2:
			return "3D Fix (2)";
		case 3:
			return "Dead Reckoning (3)";
		default:
			return "Unknown GPS fix code ("+code+")";
		}
	}

	public static String localeID(int code) {
		switch (code) {
			case 0x3801:
				return"Arabic - U.A.E. (0x3801)";
			case 0x0402:
				return"Bulgarian (0x0402)";
			case 0x0403:
				return"Catalan (0x0403)";
			case 0x0004:
				return"Chinese - Simplified (0x0004)";
			case 0x7c04:
				return"Chinese - Traditional (0x7c04)";
			case 0x041a:
				return"Croatian (0x041a)";
			case 0x0405:
				return"Czech (0x0405)";
			case 0x0406:
				return"Danish (0x0406)";
			case 0x0413:
				return"Dutch - Netherlands (0x0413)";
			case 0x0813:
				return"Dutch - Belgium (0x0813)";
			case 0x0409:
				return"English - United States (0x0409)";
			case 0x0809:
				return"English - United Kingdom (0x0809)";
			case 0x0c09:
				return"English - Australia (0x0c09)";
			case 0x0425:
				return"Estonian (0x0425)";
			case 0x040b:
				return"Finnish (0x040b)";
			case 0x040c:
				return"French - France (0x040c)";
			case 0x0c0c:
				return"French - Canada (0x0c0c)";
			case 0x0407:
				return"German - Germany (0x0407)";
			case 0x0408:
				return"Greek (0x0408)";
			case 0x040d:
				return"Hebrew (0x040d)";
			case 0x040e:
				return"Hungarian (0x040e)";
			case 0x0421:
				return"Indonesian (0x0421)";
			case 0x0410:
				return"Italian - Italy (0x0410)";
			case 0x0426:
				return"Latvian (0x0426)";
			case 0x0427:
				return"Lithuanian (0x0427)";
			case 0x043e:
				return"Malay - Malaysia (0x043e)";
			case 0x0414:
				return"Norwegian (Bokmål) (0x0414)";
			case 0x0415:
				return"Polish (0x0415)";
			case 0x0416:
				return"Portuguese - Brazil (0x0416)";
			case 0x0816:
				return"Portuguese - Portugal (0x0816)";
			case 0x0418:
				return"Romanian (0x0418)";
			case 0x0419:
				return"Russian (0x0419)";
			case 0x081a:
				return"Serbian (Latin) (0x081a)";
			case 0x041b:
				return"Slovak (0x041b)";
			case 0x0424:
				return"Slovenian (0x0424)";
			case 0x040a:
				return"Spanish - Spain (0x040a)";
			case 0x2c0a:
				return"Spanish - Argentina (0x2c0a)";
			case 0x080a:
				return"Spanish - Mexico (0x080a)";
			case 0x041d:
				return"Swedish (0x041d)";
			case 0x041e:
				return"Thai (0x041e)";
			case 0x041f:
				return"Turkish (0x041f)";
			case 0x0422:
				return"Ukrainian (0x0422)";
		default:
			return "Unknown locale ID ("+Integer.toString(code,16)+")";
		}
	}

	public static String utcTime(Bundle d) {
	return ""+d.getInt("Year")+"-"+d.getInt("Month")+"-"+d.getInt("Day")+
	  " ["+d.getInt("DayOfWeek")+"] "+
	  d.getInt("Hour")+":"+d.getInt("Minute")+":"+d.getInt("Second")+"."+d.getInt("Milliseconds");
	}
}
