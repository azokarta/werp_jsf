package general.dao;

import java.util.List;

import general.output.tables.Frep1OutputTable;
import general.output.tables.Frep2OutputTable;
import general.output.tables.Podotchet;
import general.tables.Bseg;

public interface BsegDao extends GenericDao<Bseg> {
	public List<Bseg> getByBukrs(String bukrs);	
	public List<Bseg> getByNotLike(String bukrs);
	public List<Bseg> dynamicFindBseg(String a_dynamicWhere);
	public List<Podotchet> findPodocthetByBukrBranchWaers(String a_bukrs,Long a_branch_id, String a_waers);
	public List<Frep1OutputTable> dynamicFindFrep1(String a_dynamicWhere) throws DAOException;
	public List<Frep2OutputTable> dynamicFindFrep2(String a_dynamicWhere) throws DAOException;
	public List<Object[]> dynamicFindFrep2PaymentDetails(String a_dynamicWhere) throws DAOException;
	public List<Object[]> dynamicFindFrep2OldBaza(String a_dynamicWhere) throws DAOException;
}
