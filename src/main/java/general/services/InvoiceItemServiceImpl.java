package general.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import general.Validation;
import general.dao.DAOException;
import general.dao.InvoiceItemDao;
import general.tables.InvoiceItem;
import general.tables.Matnr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("invoiceItemService")
public class InvoiceItemServiceImpl implements InvoiceItemService {

	@Autowired InvoiceItemDao iiDao;
	
	@Override
	public void create(List<InvoiceItem> items) throws DAOException {
		String error = validate(items);
		if(error.length() > 0){
			throw new DAOException(error);
		}
		
		Long invoiceId = items.get(0).getInvoice_id();
		iiDao.deleteByCondition("invoice_id = " + invoiceId);
		//List<InvoiceItem> existsList = iiDao.findAll(" invoice_id = " + invoiceId);
		//Map<Long, Integer> tempMap = new HashMap<Long, Integer>();
		
		for(InvoiceItem ii1:items){
			//boolean isExisted = false;
//			for(InvoiceItem ii2:existsList){
//				if(ii1.getMatnr().equals(ii2.getMatnr())){
//					ii1.setId(ii2.getId());
//					iiDao.update(ii1);
//					isExisted = true;
//					tempMap.put(ii1.getId(), 1);
//				}
//			}
			
			//if(!isExisted){
				ii1.setId(null);
				iiDao.create(ii1);
			//}
		}
		
//		for(InvoiceItem ii:existsList){
//			if(!tempMap.containsKey(ii.getId())){
//				iiDao.delete(ii.getId());
//			}
//		}
	}
	
	private String validate(List<InvoiceItem> items){
		String error = "";
		if(items == null || items.size() == 0){
			return "Список материалов пуст";
		}
		
		for(InvoiceItem ii:items){
			if(Validation.isEmptyLong(ii.getInvoice_id())){
				error += "Не выбран накладной" + " \n";
			}
			
			if(Validation.isEmptyLong(ii.getMatnr())){
				error += "Не выбран материал" + " \n";
			}
			
			if(ii.getQuantity() == null || ii.getQuantity() == 0){
				error += "Количество должно быть больше нуля" + "\n";
			}
			
			if(ii.getDone_quantity() == null){
				ii.setDone_quantity(0D);
			}
			
			Matnr m = ii.getMatnrObject();
			if(m == null){
				error += "Matnr Object Is Null" + "\n";
			}else{
				if(m.getType() == 1){
					if(Validation.isEmptyString(ii.getBarcode())){
						error += "Баркод обязательно для материала " + m.getText45() + "\n";
					}else{
						String[] b = ii.getParsedBarcodes();
						if(ii.getQuantity().intValue() != b.length){
							error += "Количество баркода не совпадает с количеством материала " + m.getText45() + "\n";
						}
					}
				}
			}
			
			if(error.length() > 0){
				return error;
			}
		}
		
		return error;
	}

}
