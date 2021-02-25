package general.services;

import java.util.List;

import general.Helper;
import general.Validation;
import general.dao.DAOException;
import general.dao.PaymentTemplateDao;
import general.dao.PriceListDao;
import general.tables.PaymentTemplate;
import general.tables.PriceList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("priceListService")
public class PriceListServiceImpl implements PriceListService {

	@Autowired
	PriceListDao plDao;

	@Autowired
	PaymentTemplateDao ptDao;

	@Override
	public void createPriceList(List<PriceList> pList) throws DAOException {
		String error = "";
		if (pList.size() == 0) {
			error += "Выберите материал" + "\n";
		}

		for (PriceList pl : pList) {
			if (pl.getMatnr() == null || pl.getMatnr().longValue() == 0L) {
				error += "Материал не выбран" + "\n";
				break;
			}

			if (Validation.isEmptyString(pl.getBukrs())) {
				error += "Выберите компанию" + "\n";
				break;
			}

			if (pl.getCountry_id() == null
					|| pl.getCountry_id().longValue() == 0L) {
				error += "Выберите страну" + "\n";
				break;
			}

			if (Validation.isEmptyString(pl.getWaers())) {
				error += "Выберите Валюту" + "\n";
				break;
			}

			if (pl.getFrom_date() == null) {
				error += "Выберите Дату" + "\n";
				break;
			}

			if (pl.getPrice() < 0) {
				error += "Цена не может быть отрицательным числом!" + "\n";
				break;
			}
			
			if (pl.getMonth() > 0 && pl.getMonth_type() ==0) {
				error += "Выберите вид рассрочки." + "\n";
				break;
			} else if (pl.getMonth() == 0 && pl.getMonth_type() > 0) {
				error += "Вид рассрочки не соответствует." + "\n";
				break;
			}
		}
		
		if (error.length() == 0) {
			PriceList temp = pList.get(0);

			List<PriceList> samePrices = plDao
					.findAll(String
							.format("matnr = %d AND bukrs = '%s' AND waers = '%s' AND country_id = %d AND from_date >= '%s'",
									temp.getMatnr(), temp.getBukrs(),
									temp.getWaers(), temp.getCountry_id(),
									Helper.getFormattedDateForDb(temp.getFrom_date())));
			if(samePrices.size() > 0){
				error += "Материал с такой датой уже имеется. ";
			}
		}

		if (error.length() > 0) {
			throw new DAOException(error);
		}

		for (PriceList pl : pList) {
			pl.setActive(1L);
			plDao.create(pl);
		}
	}
	
	
	@Override
	public boolean createTovarPriceList(PriceList pl, List<PaymentTemplate> ptL) throws DAOException {
		String error = "";
		if (ptL.size() == 0) {
			error += "График платежей не определен!" + "\n";
		}

			if (pl.getMatnr() == null || pl.getMatnr().longValue() == 0L) {
				error += "Материал не выбран" + "\n";
			}

			if (Validation.isEmptyString(pl.getBukrs())) {
				error += "Выберите компанию" + "\n";
			}

			if (pl.getCountry_id() == null
					|| pl.getCountry_id().longValue() == 0L) {
				error += "Выберите страну" + "\n";
			}

			if (Validation.isEmptyString(pl.getWaers())) {
				error += "Выберите Валюту" + "\n";
			}

			if (pl.getFrom_date() == null) {
				error += "Выберите Дату" + "\n";
			}

			if (pl.getPrice() == 0) {
				error += "Напишите цену" + "\n";
			}
			
			if (ptL.size() == 0) {
				error += "Введите график платежей!" + "\n";
			}
			
			if (!checkPriceSum(pl, ptL)) {
				error += "Ошибка в суммах графика платежей!" + "\n";
			}			
			
			if (pl.getMonth() > 0 && pl.getMonth_type() ==0) {
				error += "Выберите вид рассрочки." + "\n";
			} 
//			else if (pl.getMonth() == 0 && pl.getMonth_type() > 0) {
//				error += "Вид рассрочки не соответствует." + "\n";
//			}
	
		if (error.length() == 0) {
			PriceList temp = pl;
			List<PriceList> samePrices = plDao
					.findAll(String
							.format("matnr = %d AND bukrs = '%s' AND waers = '%s' "
									+ "AND country_id = %d AND from_date >= '%s'"
									+ "AND branch_id = %d AND month_type = %d",
									temp.getMatnr(), temp.getBukrs(),
									temp.getWaers(), temp.getCountry_id(),
									Helper.getFormattedDateForDb(temp.getFrom_date()),
									temp.getBranch_id(),
									temp.getMonth_type()));
			if(samePrices.size() > 0){
				error += "Прайс с такой датой уже имеется. ";
			}
		}

		if (error.length() > 0) {
			throw new DAOException(error);
		}

		pl.setActive(1L);
		plDao.create(pl);
		
		for (PaymentTemplate pt:ptL) {
			pt.setPrice_list_id(pl.getPrice_list_id());
			ptDao.create(pt);
		}
		
		return true;
	}
	
	public boolean checkPriceSum(PriceList p, List<PaymentTemplate> ptL) {
		double sum = 0;
		int mon = 0;
		for (PaymentTemplate pt:ptL) {
			if (pt.getMonth_num() <= 0 || pt.getMonthly_payment_sum() <= 0) {
				ptL.remove(pt);
			} else { 
				mon += pt.getMonth_num();
				sum += pt.getMonth_num() * pt.getMonthly_payment_sum();
			}
		}
		return ((sum == p.getPrice()) && (mon-1 == p.getMonth()));
	}
}
