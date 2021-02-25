package crm.validators;

public interface Validator {

	boolean isValid(Object ob);

	String getError();
}
