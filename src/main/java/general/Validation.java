package general;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {
	public static boolean CheckForWhiteSpacesString(String p_string) {
		Pattern pattern = Pattern.compile("\\s");
		Matcher matcher = pattern.matcher(p_string);
		boolean found = matcher.find();
		return found;
	}

	public static String sqlMatchPatternDynamicWhere(String a_dynamicWhere, String a_andOr, String a_comparator,
			String a_column, String a_value) {
		if (a_dynamicWhere.length() > 0) {
			a_dynamicWhere = a_dynamicWhere + " " + a_andOr + " ";
		}
		a_value.toLowerCase();

		a_dynamicWhere = a_dynamicWhere + a_column + " LIKE '%" + a_value + "%'";
		/*
		 * if (a_value.indexOf("*")>-1){ a_value = a_value.replace(""+"*", "%");
		 * a_dynamicWhere = a_dynamicWhere + a_column + " LIKE '" + a_value
		 * +"'"; } else{ a_dynamicWhere = a_dynamicWhere + a_column + " "
		 * +a_comparator+" '" + a_value +"'"; }
		 */
		return a_dynamicWhere;
	}

	public static String returnFio(String firstName, String lastName, String middleName) {
		String fullName = "";
		if (lastName == null || lastName.length() == 0)
			lastName = "";
		else
			fullName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();
		if (firstName == null || firstName.length() == 0)
			firstName = "";
		else
			fullName = fullName + " " + firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
		if (middleName == null || middleName.length() == 0)
			middleName = "";
		else
			fullName = fullName + " " + middleName.substring(0, 1).toUpperCase()
					+ middleName.substring(1).toLowerCase();
		return fullName;
	}

	public static String returnFioInitials(String firstName, String lastName, String middleName) {
		String fullName = "";
		if (lastName == null || lastName.length() == 0)
			lastName = "";
		else if (lastName.length() > 0)
			fullName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();
		if (firstName == null || firstName.length() == 0)
			firstName = "";
		else if (firstName.length() > 0)
			fullName = fullName + " " + firstName.substring(0, 1).toUpperCase() + ".";
		if (middleName == null || middleName.length() == 0)
			middleName = "";
		else if (middleName.length() > 0)
			fullName = fullName + " " + middleName.substring(0, 1).toUpperCase() + ".";
		return fullName;
	}

	public static boolean isValidEmail(String s) {
		if (s.length() == 0) {
			return false;
		}
		String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
		return s.matches(EMAIL_REGEX);
	}

	public static boolean isValidPassword(String s) {
		if (s.length() == 0 || s == null) {
			return false;
		}
		Pattern pattern;
		Matcher matcher;
		final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,50})";
		pattern = Pattern.compile(PASSWORD_PATTERN);
		matcher = pattern.matcher(s);
		return matcher.matches();
	}

	public static boolean isEmptyString(String s) {
		return s == null || s.isEmpty();
	}

	public static boolean isEmptyLong(Long l) {
		return (l == null || l.longValue() == 0);
	}

	public static boolean isEmptyCollection(Collection col) {
		return col == null || col.isEmpty();
	}

	public static boolean isEmptyInteger(Integer i) {
		return i == 0 || i == null;
	}
}
