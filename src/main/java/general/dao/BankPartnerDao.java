package general.dao;

import java.util.List;

import general.tables.BankPartner;

public interface BankPartnerDao extends GenericDao<BankPartner> {
	public List<BankPartner> findAll();
}
