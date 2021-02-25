package general.dao;

import java.util.List;

import javax.persistence.Query;

import general.tables.Branch;

public interface BranchDao  extends GenericDao<Branch>{
	public List<Branch> findAll();
	public List<Branch> findByBukrs(String a_bukrs);
	public Branch findServBranchByBranch(Long a_branchId);
	
	List<Branch> findChilds(Long branchId);
	public List<Branch> findUserBranchesDmsc01(String a_bukrs, Long a_userId);
	public List<Branch> findUserBranchesDmsc01Admin(String a_bukrs, Long a_userId);
	

	public List<Branch> findUserBranchesDmsc01Service(String a_bukrs, Long a_userId);
	public List<Branch> findUserBranchesDmsc01ServiceAdmin(String a_bukrs, Long a_userId);
	
	public List<Branch> findUserBranchesOffices(String a_bukrs, Long a_userId);
	public List<Branch> findUserBranchesOfficesAdmin(String a_bukrs, Long a_userId);
}
