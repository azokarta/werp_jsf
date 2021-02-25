package general.dao;

import java.util.List;
import general.tables.ServZFBranchMT;

public interface ServZFBranchMTDao extends GenericDao<ServZFBranchMT> {
	List<ServZFBranchMT> findAll();
	List<ServZFBranchMT> findAll(String cond);
	ServZFBranchMT findLastMTByBranch(Long a_branchId);
}

