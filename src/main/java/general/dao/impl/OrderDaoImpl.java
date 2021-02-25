package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import general.Validation;
import general.dao.CustomerDao;
import general.dao.OrderDao;
import general.dao.StaffDao;
import general.dao.UserDao;
import general.tables.Customer;
import general.tables.Order;
import general.tables.Staff;
import general.tables.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Component("orderDao")
public class OrderDaoImpl extends GenericDaoImpl<Order> implements OrderDao{
	
	@Autowired StaffDao stfDao;
	
	@Autowired UserDao userDao;
	
	@Autowired CustomerDao customerDao;
	
	@SuppressWarnings("unchecked")
	public List<Order> findAll(){ 
    	
    	Query query = this.em
                .createQuery("select b FROM Order b order by id"); 
    	List<Order> ord =  query.getResultList();
    	return ord;
    }

	@SuppressWarnings("unchecked")
	public List<Order> findAll(String condition){ 
    	String s = "SELECT o FROM Order o";
    	if(condition.length() > 0){
    		s += " WHERE " + condition;
    	}

    	Query query = this.em.createQuery(s); 
    	List<Order> ord =  query.getResultList();
    	return ord;
    }
	
	
	public void insert() {
		String query_statement="INSERT INTO Order " +
				"(id,bukrs,branch_id,order_number,date_new," +
				"order_type,supplier_branch,customer_id,created_by,"+
				"updated_by,date_old,status_id,info,main_currency) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		/*
		query_statement="INSERT INTO Order " +
				"(id,bukrs,branch_id,order_number,date_new," +
				"order_type,supplier_branch,customer_id,created_by,"+
				"updated_by,date_old,status_id,info,main_currency) " +
				"VALUES (null, '1000',48,24822510,'2015-11-20','EXT',"
				+ " null,2,1,null,null,1,'Text','USD')";
		*/
		
				
		
		Query query = this.em
                .createQuery(query_statement);
    	
	}

	@Override
	public void updateByQuery(String query) {
		String s = " UPDATE Order o " + query;
		Query q = this.em.createQuery(s);
		q.executeUpdate();
	}
	
	@Override
	public Order find(Object id) {
		// TODO Auto-generated method stub
		Order o = super.find(id);
		if(o != null){
			if(!Validation.isEmptyLong(o.getCreated_by())){
				User u = userDao.findWithStaff(o.getCreated_by());
				if(u == null){
					o.setCreator(new Staff());
				}else{
					o.setCreator(u.getStaff());
				}
			}
			
			if(!Validation.isEmptyLong(o.getCustomer_id())){
				Customer c = customerDao.find(o.getCustomer_id());
				if(c == null){
					o.setCustomer(new Customer());
				}else{
					o.setCustomer(c);
				}
			}
		}
		
		return o;
	}
}
