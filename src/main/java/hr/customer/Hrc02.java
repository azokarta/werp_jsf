package hr.customer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import general.GeneralUtil;
import general.PermissionController;
import general.services.CustomerService;
import general.tables.BankAccount;
import general.dao.DAOException;

@ManagedBean(name = "hrc02Bean")
@ViewScoped
public class Hrc02 extends HrcBase implements Serializable{
	
	private static final long serialVersionUID = 8421532307829352388L;
	private final static Long transactionId = 9L;
	
	public void update(){
		try{
			PermissionController.canWrite(userData, transactionId);
			CustomerService cService = (CustomerService)appContext.getContext().getBean("customerService");
			String error = this.validateBaList(this.getBaList());
			if(error.length() > 0){
				throw new DAOException(error);
			}
			List<BankAccount> tempList = new ArrayList<BankAccount>();
			for(BankAccount ba: this.getBaList()){
				BankAccount bTemp = new BankAccount();
				bTemp.setBank_id(ba.getBank_id());
				bTemp.setCreated_by(ba.getCreated_by());
				bTemp.setCreated_date(ba.getCreated_date());
				bTemp.setCurrency(ba.getCurrency());
				bTemp.setCustomer_id(ba.getCustomer_id());
				bTemp.setIban(ba.getIban());
				tempList.add(bTemp);
			}
			cService.updateCustomer(customer,tempList);
			GeneralUtil.addSuccessMessage("Success!");
		}catch(DAOException e){
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
		return super.getBreadcrumb() + " > Редактирование клиента";
	}
}
