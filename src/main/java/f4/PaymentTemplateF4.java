package f4;

import general.AppContext;
import general.dao.DAOException;
import general.dao.PaymentTemplateDao;
import general.tables.PaymentTemplate;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

@ManagedBean(name = "paymentTemplateF4Bean")
@ApplicationScoped
public class PaymentTemplateF4 {

	// ***************************Application Context***************************
	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	// *************************************************************************

	List<PaymentTemplate> paymentTemplate_list = new ArrayList<PaymentTemplate>();
	
	@PostConstruct
	public void init(){ 
		try
		{
			paymentTemplate_list = new ArrayList<PaymentTemplate>();
			PaymentTemplateDao paymentTemplateDao = (PaymentTemplateDao) appContext.getContext().getBean("paymentTemplateDao");
			paymentTemplate_list = paymentTemplateDao.findAll();		
		}
	    catch(Exception ex)
		{
	    	System.out.println("Payment template F4 not loaded");
	    	throw new DAOException("Payment template F4 not loaded");
		}
	}
	
	public void updateF4()
	{
		try
		{
			paymentTemplate_list = new ArrayList<PaymentTemplate>();
			PaymentTemplateDao paymentTemplateDao = (PaymentTemplateDao) appContext.getContext().getBean("paymentTemplateDao");
			paymentTemplate_list = paymentTemplateDao.findAll();		
		}
	    catch(Exception ex)
		{
	    	System.out.println("Payment template F4 not loaded");
	    	throw new DAOException("Payment template F4 not loaded");
		}
	}

	public List<PaymentTemplate> getPaymentTemplate_list() {
		return paymentTemplate_list;
	}

	public void setPaymentTemplate_list(List<PaymentTemplate> paymentTemplate_list) {
		this.paymentTemplate_list = paymentTemplate_list;
	}
	
	public double getFirstPaymentOf(Long pl_id) {
		
		List<PaymentTemplate> pt_l = this.getPaymentTemplateOf(pl_id);
		
		return pt_l.get(0).getMonthly_payment_sum();
	}
	
	public List<PaymentTemplate> getPaymentTemplateOf(Long pl_id) {
		List<PaymentTemplate> pt_l = new ArrayList<PaymentTemplate>();
		
		for (PaymentTemplate pt: paymentTemplate_list) {
			if (pt.getPrice_list_id().equals(pl_id)) {
				//System.out.println("Pl_id: " + pl_id + "  pt_order: " + pt.getPt_order() +  "  mon: " + pt.getMonth_num() + "  monthly_sum: " + pt.getMonthly_payment_sum());
				pt_l.add(pt);
			}
		}
		
		return pt_l;
	}	
}
