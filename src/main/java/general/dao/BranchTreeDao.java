package general.dao;

import java.util.List;

import general.tables.BranchTree;

public interface BranchTreeDao  extends GenericDao<BranchTree>{
	public List<BranchTree> findAll();
	public List<BranchTree> findByBukrs(String a_bukrs);
	public BranchTree findServBranchByBranch(Long a_branchId);
}
