package logistics;

import general.MessageProvider;
import general.tables.Invoice;

public class LogHelper {

	public static String getPreparedPageHeader(Invoice invoice, String mode) {
		MessageProvider mp = new MessageProvider();
		if (invoice != null) {
			if ("create".equals(mode)) {
				return invoice.getTypeName() + " / " + mp.getValue("logistics.creation");
			} else if ("update".equals(mode)) {
				return invoice.getTypeName() + " № " + invoice.getFormattedRegNumber() + " / " + mp.getValue("logistics.editing");
			} else if ("view".equals(mode)) {
				return invoice.getTypeName() + " № " + invoice.getFormattedRegNumber();
			}
		}
		return "";
	}
}
