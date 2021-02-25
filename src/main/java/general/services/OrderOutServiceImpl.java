package general.services;

import java.util.Calendar;
import java.util.List;

import general.Helper;
import general.Validation;
import general.dao.DAOException;
import general.dao.OrderDao;
import general.dao.OrderOutDao;
import general.dao.OrderOutListDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import general.tables.OrderOut;
import general.tables.OrderOutList;

@Service("orderOutService")
public class OrderOutServiceImpl implements OrderOutService{
	@Autowired
    private OrderOutDao oDao;
	
	@Autowired
    private OrderOutListDao olDao;
	
	@Autowired
	private OrderDao orderInDao;
	
	private String validateOrder(OrderOut o, boolean isNew){
		String error = "";
		if(Validation.isEmptyString(o.getBukrs())){
			error += "Выберите компанию " + "\n";
		}
		
		if(o.getCustomer_id() == null || o.getCustomer_id().longValue() == 0L){
			error += "Выберите поставщика " + "\n";
		}
		
		if(isNew){
			o.setCreated_date(Calendar.getInstance().getTime());
		}
		
		o.setUpdated_date(Calendar.getInstance().getTime());
		
		return error;
	}
		
	@Override
	public void createOrder( OrderOut order, List<OrderOutList> orderList) throws DAOException{
		String error = this.validateOrder(order, true);
		if(orderList.isEmpty()){
			error += "Выберите материалы для заказа";
		}
		if(error.length() > 0){
			throw new DAOException(error);
		}
		
		for(OrderOutList ol:orderList){
			error = this.validateOrderList(ol);
			if(error.length() > 0){
				throw new DAOException(error);
			}
		}
		
		oDao.create(order);
		String[] orderInIds = new String[orderList.size()];
		int i = 0;
		for(OrderOutList ol:orderList){
			ol.setOrder_id_out(order.getId());
			olDao.create(ol);
			if(ol.getOrder_id_in() != null){
				orderInIds[i] = ol.getOrder_id_in().toString();
				i++;
			}
		}
		
		String query = String.format(" SET status_id = 3, processed_date='%s' WHERE id IN(%s) ",Helper.getFormattedDateForDb(Calendar.getInstance().getTime()), "'" + String.join("','", orderInIds) + "'");
		orderInDao.updateByQuery(query);
	}
	
	private String validateOrderList(OrderOutList ol){
		String error = "";
		if(ol.getOrder_id_in() == null){
			ol.setOrder_id_in(0L);
		}
		if(ol.getMatnr_id() == null || ol.getMatnr_id().longValue() == 0L){
			error += "Выберите материал " + "\n";
		}
		
		if(ol.getQuantity_out() == 0){
			error += "Количество должно быть больше нуля" + "\n";
		}
		return error;
	}
	

	@Override
	public void updateOrder(OrderOut order, List<OrderOutList> orderList)
			throws DAOException {
		String error = this.validateOrder(order,false);
		if(error.length() > 0){
			throw new DAOException(error);
		}
		
		oDao.update(order);
		
		for(OrderOutList ol:orderList){
			error = this.validateOrderList(ol);
			if(error.length() > 0){
				throw new DAOException(error);
			}
			ol.setOrder_id_out(order.getId());
			if(ol.getId() == null){
				olDao.create(ol);
			}else{
				olDao.update(ol);
			}
		}
	}
}
