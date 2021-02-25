package general.services;

import java.util.List;

import general.dao.DAOException;
import general.tables.PaymentTemplate;
import general.tables.PriceList;

import org.springframework.transaction.annotation.Transactional;

public interface PriceListService {

	@Transactional
	void createPriceList(List<PriceList> pl) throws DAOException;
	
	@Transactional
	boolean createTovarPriceList(PriceList pl, List<PaymentTemplate> ptL) throws DAOException;
	
	public boolean checkPriceSum(PriceList p, List<PaymentTemplate> ptL);
}
