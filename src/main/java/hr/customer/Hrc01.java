package hr.customer;
 

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import general.GeneralUtil;
import general.PermissionController;
import general.services.CustomerService;
import general.tables.Customer;
import general.dao.DAOException;

@ManagedBean(name = "hrc01Bean", eager = true)
@ViewScoped
public class Hrc01 extends HrcBase implements Serializable{

	private static final long serialVersionUID = 977387488798778528L;
	private final static Long transactionId = 8L;
	
	public void save(){
		try{
			PermissionController.canWrite(userData, transactionId);
			this.customer.setCreated_by(userData.getUserid());
			this.customer.setUpdated_by(userData.getUserid());
			CustomerService cService = (CustomerService)appContext.getContext().getBean("customerService");
			String error = this.validateBaList(this.getBaList());
			if(error.length() > 0){
				throw new DAOException(error);
			}
			
			cService.createCustomer(this.customer,this.getBaList());
			this.setCustomer(new Customer());
			this.setEmptyBaList();
			GeneralUtil.addSuccessMessage("Success!");
		}catch(DAOException e){
			this.customer.setId(null);
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	@Override
	Long getTransactionId() {
		return transactionId;
	}	
	
	@Override
	public String getBreadcrumb() {
		// TODO Auto-generated method stub
		return super.getBreadcrumb() + " > Добавление клиента";
	}
}
