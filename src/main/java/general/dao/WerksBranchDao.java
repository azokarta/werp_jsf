package general.dao;

import java.util.List;

import general.tables.Branch;
import general.tables.Werks;
import general.tables.WerksBranch;

public interface WerksBranchDao extends GenericDao<WerksBranch> {
	public List<WerksBranch> findAll(String condition);
	public List<Werks> findAllWerksByBranch(Long branchId);
	
	public List<Branch> findAllBranchesByWerks(Long werks);
	
	public List<Werks> findAllWerksByBranch2(Long branchId);
}
