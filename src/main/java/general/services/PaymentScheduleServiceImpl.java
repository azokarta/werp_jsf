package general.services; 

import java.util.Calendar;
import java.util.List;
 



import general.dao.DAOException;
import general.dao.MatnrListDao;
import general.dao.PaymentScheduleDao;
import general.tables.Bkpf; 
import general.tables.Bseg; 
import general.tables.MatnrList;
import general.tables.PaymentSchedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("paymentScheduleService")
public class PaymentScheduleServiceImpl implements PaymentScheduleService{
	
	@Autowired
    private PaymentScheduleDao dao;

	@Override
	public void create(List<PaymentSchedule> l) throws DAOException {
		String error = "";
		for(PaymentSchedule p:l){
			error += this.validate(p);
		}
		
		if(error.length() > 0){
			throw new DAOException(error);
		}
		
		for(PaymentSchedule p:l){
			dao.create(p);
		}
	}
	
	private String validate(PaymentSchedule ps){
		String error = "";
		if(ps.getBukrs().isEmpty()){
			error += "Bukrs error" + "\n";
		}
		
		if(ps.getSum() == 0L){
			error += "Sum error" + "\n";
		}
		System.out.println("AW: " + ps.getAwkey());
		if(ps.getAwkey() == 0L){
			error += "Awkey error" + "\n";
		}
		
		if(ps.getPayment_date() == null){
			error += "Payment Date error" + "\n";
		}
		return error;
	}
}