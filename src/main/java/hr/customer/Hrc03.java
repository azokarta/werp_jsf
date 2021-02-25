package hr.customer;
 

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "hrc03Bean", eager = true)
@ViewScoped
public class Hrc03 extends HrcBase implements Serializable{

	private static final long serialVersionUID = -2647874691562180599L;
	private final static Long transactionId = 10L;
	@Override
	Long getTransactionId() {
		return transactionId;
	}
	
	@Override
	public String getBreadcrumb() {
		// TODO Auto-generated method stub
		return super.getBreadcrumb() + " > Просмотр клиента";
	}
}
