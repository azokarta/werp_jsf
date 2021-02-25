package general.services;

import java.text.MessageFormat;

import general.MessageProvider;
import general.Validation;
import general.dao.DAOException;
import general.dao.InvoicePaymentTermDao;
import general.tables.InvoicePaymentTerm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("iptService")
public class IPTServiceImpl implements IPTService{

	@Autowired
	InvoicePaymentTermDao dao;
	
	@Override
	public void create(InvoicePaymentTerm ipt) throws DAOException {
		String error = this.validate(ipt);
		if(error.length() > 0){
			throw new DAOException(error);
		}
		dao.create(ipt);
	}
	
	private String validate(InvoicePaymentTerm ipt){
		if(Validation.isEmptyString(ipt.getName_ru())){
			return MessageFormat.format(new MessageProvider().getErrorValue("field_is_required"), "name_ru");
		}
		
		return "";
	}

	@Override
	public void update(InvoicePaymentTerm ipt) throws DAOException {
		String error = this.validate(ipt);
		if(error.length() > 0){
			throw new DAOException(error);
		}
		dao.update(ipt);
	}

}
