package general.validators;

import general.tables.HrDocTransfer;
import user.User;

public interface HrDocTransferValidator {
	public boolean isValid(HrDocTransfer hrDocTransfer);
	
	public String getError();
}
