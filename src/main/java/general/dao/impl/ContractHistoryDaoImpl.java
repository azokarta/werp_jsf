package general.dao.impl;

import java.sql.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import general.dao.ContractHistoryDao;
import general.tables.Bkpf;
import general.tables.BonusArchive;
import general.tables.ContractHistory;

import org.springframework.stereotype.Component;

@Component("contractHistoryDao")
public class ContractHistoryDaoImpl extends GenericDaoImpl<ContractHistory> implements ContractHistoryDao{
	
	@Override
	public List<ContractHistory> findAll() {
		Query query = this.em
				.createQuery("select ch FROM ContractHistory ch"); 
		List<ContractHistory> l_ch = query.getResultList();
		return l_ch;
	}
	
	@Override
	public List<ContractHistory> findAllByContractID(Long a_conId) {
		Query query = this.em
				.createQuery("select ch FROM ContractHistory ch WHERE contract_id = " + a_conId + " order by id"); 
		List<ContractHistory> l_ch = query.getResultList();
		return l_ch;
	}
	

	public ContractHistory findSinglePriceChange(Date a_lastDate, Long a_con_id){ 
    	
    	
    	try{ 
    		Query query = this.em
                    .createQuery("select ch FROM ContractHistory ch where ch.id = some ( select min(ch2.id) FROM ContractHistory ch2 where oper_on=20 and rec_date >:rec_date and contract_id=:contract_id"
                    		+ " group by ch2.contract_id)"); 
        	query.setParameter("rec_date", a_lastDate);
        	query.setParameter("contract_id", a_con_id);
        	ContractHistory ch = (ContractHistory) query.getSingleResult();

        	return ch;
		}	
		catch (NoResultException nre){
			//Ignore this because as per your logic this is ok!
			return null;
			}
    }

}
