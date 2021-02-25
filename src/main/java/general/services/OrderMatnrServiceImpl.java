package general.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import general.Validation;
import general.dao.DAOException;
import general.dao.OrderDao;
import general.dao.OrderMatnrDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import general.tables.OrderMatnr;

@Service("orderMatnrService")
public class OrderMatnrServiceImpl implements OrderMatnrService {

	@Autowired
	private OrderDao oDao;

	@Autowired
	private OrderMatnrDao omDao;

	@Override
	public void create(List<OrderMatnr> omList) throws DAOException {
		String error = validate(omList, true);
		if (omList == null || omList.size() == 0) {
			error += "Список материалов пуст";
		}

		if (error.length() > 0) {
			throw new DAOException(error);
		}
		insertOrderMatnrs(omList);
	}

	private void insertOrderMatnrs(List<OrderMatnr> omList) {
		Long orderId = omList.get(0).getOrder_id();
		List<OrderMatnr> existsList = omDao.findAll(String.format(
				" order_id = %d ", orderId));

		Map<Long, Integer> tempMap = new HashMap<Long, Integer>();
		for (OrderMatnr om1 : omList) {
			boolean isExisted = false;
			for (OrderMatnr om2 : existsList) {
				if (om2.getMatnr().equals(om1.getMatnr())) {
					om1.setId(om2.getId());
					omDao.update(om1);
					tempMap.put(om2.getId(), 1);
					isExisted = true;
					break;
				}
			}

			if (!isExisted) {
				om1.setId(null);
				omDao.create(om1);
			}
		}

		for (OrderMatnr om : existsList) {
			if (!tempMap.containsKey(om.getId())) {
				omDao.delete(om.getId());
			}
		}
	}

	private String validate(List<OrderMatnr> omList, boolean isNew) {
		String error = "";
		for (OrderMatnr om : omList) {
			if (Validation.isEmptyLong(om.getOrder_id())) {
				error += "Выберите заказ" + "\n";
			}

			if (om.getAmount() == null) {
				om.setAmount(0D);
			}

			if (om.getQuantity() == null || om.getQuantity() == 0D) {
				error += "Количество должно быть больше нуля" + "\n";
			} else {
				if (om.getUnit_price() != null) {
					om.setAmount(om.getQuantity() * om.getUnit_price());
				}
			}

			if (Validation.isEmptyLong(om.getMatnr())) {
				error += "Выберите материал" + "\n";
			}

			if (om.getUnit_price() == null || om.getUnit_price() == 0) {
				om.setUnit_price(0D);
			} else {

			}

			if (error.length() > 0) {
				break;
			}
		}
		return error;
	}
}
