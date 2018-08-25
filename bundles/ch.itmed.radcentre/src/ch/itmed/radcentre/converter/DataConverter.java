package ch.itmed.radcentre.converter;

public class DataConverter {
	
	public static String patientGender(String gender) {
		if (gender.equals("M")) {
			return "m";
		} else if (gender.equals("F")) {
			return "F";
		}
		return "";
	}
	
	public static String patientBirthday(String bd) {
		return bd.replaceAll("\\-", "");
	}

}
