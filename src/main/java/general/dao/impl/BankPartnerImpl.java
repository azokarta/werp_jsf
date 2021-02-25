package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import general.dao.BankPartnerDao;
import general.tables.BankPartner;

import org.springframework.stereotype.Component;

@Component("bankPartnerDao")
public class BankPartnerImpl extends GenericDaoImpl<BankPartner> implements BankPartnerDao{

	public List<BankPartner> findAll() {
		Query query = this.em
				.createQuery("select c FROM BankPartner c"); 
		List<BankPartner> l_bankPartners = query.getResultList();
		return l_bankPartners;
	}
}
