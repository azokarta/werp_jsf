package general.validators;

import general.tables.HrDoc;
import general.tables.HrDocTransfer;
import user.User;

public interface HrDocValidator {
	public boolean isValid(HrDoc doc);
	
	public String getError();
}
