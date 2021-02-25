package general.dao;

import java.util.List;

import general.tables.Branch;
import general.tables.Promotion;
import general.tables.Role;

public interface PromotionDao  extends GenericDao<Promotion> {

	public Promotion findByNumber(Long a_number);
	public List<Promotion> findAll();
	public List<Promotion> dynamicFindAll(String wcl);
	public List<Promotion> findContractPromotions(Long a_contractId);

}
