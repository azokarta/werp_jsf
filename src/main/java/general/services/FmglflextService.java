package general.services;

import java.util.List;

import general.dao.DAOException;
import general.tables.Branch;
import general.tables.Fmglflext;

import org.springframework.transaction.annotation.Transactional;

public interface FmglflextService {
	@Transactional
	public List<Fmglflext> getAccountsBalance(String a_bukrs, int a_gjahr, int a_monat, List<String> sl_hkont) throws DAOException;
	
	@Transactional
	public List<Object[]> getAccountsBalanceFrep6(String a_bukrs,int a_gjahr, List<String> al_branch_id, String a_hkontCashBank) throws DAOException;
	
	@Transactional
	public void closeYear(String a_bukrs, int a_year);
}
