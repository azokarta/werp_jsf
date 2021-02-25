package general.services;

import java.text.MessageFormat;

import general.MessageProvider;
import general.Validation;
import general.dao.DAOException;
import general.dao.InvoiceDeliveryTermDao;
import general.tables.InvoiceDeliveryTerm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("idtService")
public class IDTServiceImpl implements IDTService{

	@Autowired
	InvoiceDeliveryTermDao dao;
	
	@Override
	public void create(InvoiceDeliveryTerm idt) throws DAOException {
		String error = this.validate(idt);
		if(error.length() > 0){
			throw new DAOException(error);
		}
		dao.create(idt);
	}
	
	private String validate(InvoiceDeliveryTerm idt){
		if(Validation.isEmptyString(idt.getName_ru())){
			return MessageFormat.format(new MessageProvider().getErrorValue("field_is_required"), "name_ru");
		}
		
		return "";
	}

	@Override
	public void update(InvoiceDeliveryTerm idt) throws DAOException {
		String error = this.validate(idt);
		if(error.length() > 0){
			throw new DAOException(error);
		}
		dao.update(idt);
	}

}
