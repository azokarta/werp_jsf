package logistics.invoice;

import general.tables.InvoiceItem;
import general.tables.Matnr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogUtil {

	public static void prepareInvoiceItems(List<InvoiceItem> items,List<InvoiceItem> newItems,
			Map<Long, Matnr> matnrMap, Map<Long, List<String>> barcodesMap) {
		for (InvoiceItem ii : items) {
			Matnr m = matnrMap.get(ii.getMatnr());
			if (m != null) {
				ii.setMatnrObject(m);
				ii.setId(null);
				if (m.getType() == 1) {
					List<String> bcodes = new ArrayList<String>();
					if (barcodesMap.containsKey(m.getMatnr())) {
						bcodes = barcodesMap.get(m.getMatnr());
					}
					bcodes.add(ii.getBarcode());
					barcodesMap.put(m.getMatnr(), bcodes);
				}
			}
		}

		Map<Long, Integer> usedItems = new HashMap<Long, Integer>();
		
		for (InvoiceItem ii : items) {
			if (barcodesMap.containsKey(ii.getMatnr())) {
				if (!usedItems.containsKey(ii.getMatnr())) {
					List<String> temp = barcodesMap.get(ii.getMatnr());
					ii.setQuantity(new Double(temp.size()));
					ii.setBarcodesList(barcodesMap.get(ii.getMatnr()));
					newItems.add(ii);
					usedItems.put(ii.getMatnr(), 1);
				}
			} else {
				newItems.add(ii);
			}
		}
	}
}
