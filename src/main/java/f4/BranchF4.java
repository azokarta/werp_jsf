package f4;

import general.AppContext;
import general.Validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;




import general.dao.BranchDao;
import general.dao.DAOException;
import general.tables.Branch;

@ManagedBean(name = "branchF4Bean")
@ApplicationScoped
public class BranchF4 {

	// ***************************Application Context***************************
	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	// *************************************************************************
	
	List<Branch> branch_list = new ArrayList<Branch>();
	Map<Long,Branch> l_branch_map = new HashMap<Long,Branch>();
	@PostConstruct
	public void init() {
		
		try
		{
			BranchDao branchDao = (BranchDao) appContext.getContext().getBean("branchDao");
			branch_list = branchDao.findAll();
			sortByBranchName(branch_list);
			Long key;
			for (Branch wa_branch:branch_list)
			{
				key = wa_branch.getBranch_id();
				l_branch_map.put(key, wa_branch);
			}
			
		}
	    catch(Exception ex)
		{
	    	System.out.println("Branch F4 not loaded");
	    	throw new DAOException("Branch F4 not loaded");
		}
		 
	}
	
	public void updateF4()
	{
		try
		{
			branch_list = new ArrayList<Branch>();
			l_branch_map = new HashMap<Long,Branch>();
			BranchDao branchDao = (BranchDao) appContext.getContext().getBean("branchDao");
			branch_list = branchDao.findAll();
			Long key;
			for (Branch wa_branch:branch_list)
			{
				key = wa_branch.getBranch_id();
				l_branch_map.put(key, wa_branch);
			}
		}
	    catch(Exception ex)
		{
	    	System.out.println("Branch F4 not loaded");
	    	throw new DAOException("Branch F4 not loaded");
		}
	}
	
	public Map<Long, Branch> getL_branch_map() {
		return l_branch_map;
	}

	public List<Branch> getBranch_list() {
		return branch_list;
	}
	public List<Branch> getBranchByBranch(Long a_branch_id)
	{ 
		List<Branch> wa_branch_list = new ArrayList<Branch>();
		if (a_branch_id!=null && a_branch_id>1)
		{
			Branch wa_branch = l_branch_map.get(a_branch_id);
			if (wa_branch!=null)
			{
				wa_branch_list.add(wa_branch);				
			}
		}
		return wa_branch_list;
	}	
	public String getBranchNameByID(String a_branch_id)
	{ 
		String branch_name = "";
		Branch wa_branch = new Branch();
		if (a_branch_id!=null && a_branch_id.length()>0)
		{
			Long br_id = Long.parseLong(a_branch_id);
			wa_branch = l_branch_map.get(br_id);
			if (wa_branch!=null)
			{
				branch_name = wa_branch.getText45();				
			}
		}
		return branch_name;
	}
	public List<Branch> getAllBranchByBukrs(String a_bukrs)
	{ 
		List<Branch> wa_branch_list = new ArrayList<Branch>();
		for(Branch wa_branch:branch_list)
		{
			if (wa_branch.getBukrs().equals(a_bukrs))
			{
				wa_branch_list.add(wa_branch);
			}				
		}
		return wa_branch_list;
	}
	public List<Branch> getByBukrsOrBusinessArea(String a_bukrs,String a_user_bukrs,Long a_business_area_id,Long a_user_business_area_id, Long a_branch_id,Long a_user_branch_id)
	{ 
		//System.out.println(a_bukrs);
		//System.out.println(a_user_bukrs);
		//System.out.println(a_business_area_id);
		//System.out.println(a_user_business_area_id);
		//System.out.println(a_branch_id);
		//System.out.println(a_user_branch_id);
		List<Branch> wa_branch_list = new ArrayList<Branch>();
		/*if (a_branch_id!=null && a_branch_id>1 && a_bukrs!=null)
		{
			for (Branch wa_branch:branch_list)
			{
				if (wa_branch.getBukrs().equals(a_bukrs))
				{
					wa_branch_list.add(wa_branch);
				}
			}
			
			List<Branch> temp_branch_list = new ArrayList<Branch>();
			temp_branch_list = getBranchTree(a_bukrs, a_branch_id,wa_branch_list);
			return temp_branch_list;
			Branch wa_branch = l_branch_map.get(a_branch_id);
			if (wa_branch!=null)
			{
				wa_branch_list.add(wa_branch);
				return wa_branch_list;
			
		}}*/
		if (a_bukrs!=null && a_bukrs.length()>0)
		{
			if (a_business_area_id != null && a_business_area_id>0 && !a_bukrs.equals(a_user_bukrs))
			{	
				
				for(Branch wa_branch:branch_list)
				{
					if (wa_branch.getBukrs().equals(a_bukrs) && wa_branch.getBusiness_area_id() != null && wa_branch.getBusiness_area_id().equals(a_business_area_id) && wa_branch.getType().equals(3L))
					{
						wa_branch_list.add(wa_branch);
					}				
				}
				return wa_branch_list;
			}
			else if (a_business_area_id != null && a_business_area_id>0 && a_bukrs.equals(a_user_bukrs))
			{
				for (Branch wa_branch:branch_list)
				{
					if (wa_branch.getBukrs().equals(a_bukrs))
					{
						wa_branch_list.add(wa_branch);
					}
				}
				if (a_user_branch_id!=null && a_user_branch_id.equals(60L))
				{
					return wa_branch_list;
				}
				else if(a_user_branch_id!=null)
				{
					Branch wa_br = l_branch_map.get(a_user_branch_id);
					if (wa_br!=null && wa_br.getType()!=null && wa_br.getType().equals(3L))
					{
						List<Branch> temp_branch_list = new ArrayList<Branch>();
						temp_branch_list.add(wa_br);
						return temp_branch_list;
					}
				}
				List<Branch> temp_branch_list = new ArrayList<Branch>();
				
				temp_branch_list = getBranchTree(a_bukrs, a_user_branch_id,wa_branch_list);
				for(Branch wa_branch:temp_branch_list)
				{
					if (wa_branch.getBukrs().equals(a_bukrs) && wa_branch.getBusiness_area_id()!=null && wa_branch.getBusiness_area_id().equals(a_business_area_id))
					{
						wa_branch_list.add(wa_branch);
					}				
				}
				
				return temp_branch_list;
			}
			else if (a_bukrs.equals(a_user_bukrs))
			{
				
				for (Branch wa_branch:branch_list)
				{
					if (wa_branch.getBukrs().equals(a_bukrs))
					{
						wa_branch_list.add(wa_branch);
					}
				}
				if (a_user_branch_id!=null && a_user_branch_id.equals(60L))
				{
					return wa_branch_list;
				}
				else if(a_user_branch_id!=null)
				{
					Branch wa_br = l_branch_map.get(a_user_branch_id);
					if (wa_br!=null && wa_br.getType()!=null && wa_br.getType().equals(3L))
					{
						List<Branch> temp_branch_list = new ArrayList<Branch>();
						temp_branch_list.add(wa_br);
						return temp_branch_list;
					}
				}
				
				
				List<Branch> temp_branch_list = new ArrayList<Branch>();
				temp_branch_list = getBranchTree(a_bukrs, a_user_branch_id,wa_branch_list);
				return temp_branch_list;
			}
			else
			{
				for(Branch wa_branch:branch_list)
				{
					if (wa_branch.getBukrs().equals(a_bukrs) && (wa_branch.getType().equals(3L)))
					{
						wa_branch_list.add(wa_branch);
					}				
				}
				return wa_branch_list;
			}
		}
		else if (a_user_bukrs != null && a_user_bukrs.length()>0)
		{
			if (a_user_branch_id != null && a_user_branch_id>1)
			{
				for (Branch wa_branch:branch_list)
				{
					if (wa_branch.getBukrs().equals(a_user_bukrs))
					{
						wa_branch_list.add(wa_branch);
					}
				}
				if (a_user_branch_id!=null && a_user_branch_id.equals(60L))
				{
					return wa_branch_list;
				}
				else if(a_user_branch_id!=null)
				{
					Branch wa_br = l_branch_map.get(a_user_branch_id);
					if (wa_br!=null && wa_br.getType()!=null && wa_br.getType().equals(3L))
					{
						List<Branch> temp_branch_list = new ArrayList<Branch>();
						temp_branch_list.add(wa_br);
						return temp_branch_list;
					}
				}
				
				
				List<Branch> temp_branch_list = new ArrayList<Branch>();
				temp_branch_list = getBranchTree(a_user_bukrs, a_user_branch_id,wa_branch_list);
				return temp_branch_list;
			}
			else if (a_business_area_id != null && a_business_area_id>0)
			{
				for (Branch wa_branch:branch_list)
				{
					if (wa_branch.getBukrs().equals(a_user_bukrs))
					{
						wa_branch_list.add(wa_branch);
					}
				}
				if (a_user_branch_id!=null && a_user_branch_id.equals(60L))
				{
					return wa_branch_list;
				}
				else if(a_user_branch_id!=null)
				{
					Branch wa_br = l_branch_map.get(a_user_branch_id);
					if (wa_br!=null && wa_br.getType()!=null && wa_br.getType().equals(3L))
					{
						List<Branch> temp_branch_list = new ArrayList<Branch>();
						temp_branch_list.add(wa_br);
						return temp_branch_list;
					}
				}
				
				
				List<Branch> temp_branch_list = new ArrayList<Branch>();
				temp_branch_list = getBranchTree(a_user_bukrs, a_user_branch_id,wa_branch_list);
				for(Branch wa_branch:temp_branch_list)
				{
					if (wa_branch.getBukrs().equals(a_user_bukrs) && wa_branch.getBusiness_area_id()!=null && wa_branch.getBusiness_area_id().equals(a_business_area_id))
					{
						wa_branch_list.add(wa_branch);
					}				
				}
			}
			else
			{
				for(Branch wa_branch:branch_list)
				{
					if (wa_branch.getBukrs().equals(a_user_bukrs) && wa_branch.getType().equals(3L))
					{
						wa_branch_list.add(wa_branch);
					}				
				}
			}
						
		}
		return wa_branch_list;
		
	}
	
	private List<Branch> getBranchTree(String a_bukrs, Long a_parentId, List<Branch> branch_list) {
		List<Branch> l_branch = new ArrayList<Branch>();
		//Branch temp_branch = null;
		for(Branch wa_branch:branch_list)
		{
			if (wa_branch.getParent_branch_id()!=null && wa_branch.getParent_branch_id().equals(a_parentId) && wa_branch.getBukrs().equals(a_bukrs))
			{
				//temp_branch = wa_branch;
				if (wa_branch.getType()!=null && wa_branch.getType().equals(3L))
				{
					l_branch.add(wa_branch);
				}
				
				l_branch.addAll(getBranchTree(a_bukrs, wa_branch.getBranch_id(), branch_list));
			}
		}
		return l_branch;

	}
	private List<Branch> getBranchTreeCity(String a_bukrs, Long a_parentId, List<Branch> branch_list) {
		List<Branch> l_branch = new ArrayList<Branch>();
		//Branch temp_branch = null;
		for(Branch wa_branch:branch_list)
		{
			if (wa_branch.getParent_branch_id()!=null && wa_branch.getParent_branch_id().equals(a_parentId) && wa_branch.getBukrs().equals(a_bukrs))
			{
				//temp_branch = wa_branch;
				if (wa_branch.getType()!=null && (wa_branch.getType().equals(4L) || wa_branch.getBranch_id().equals(60L) || wa_branch.getBranch_id().equals(102L) || wa_branch.getBranch_id().equals(214L) || wa_branch.getBranch_id().equals(225L)))
				{
					l_branch.add(wa_branch);
				}
				
				l_branch.addAll(getBranchTreeCity(a_bukrs, wa_branch.getBranch_id(), branch_list));
			}
		}
		return l_branch;

	}
	public Branch getMainBranch(String bukrs){
		if(Validation.isEmptyString(bukrs)){
			return null;
		}
		for(Branch b:this.getAllBranchByBukrs(bukrs)){
			if(b.getParent_branch_id() == null || b.getParent_branch_id().equals(0L)){
				return b;
			}
		}
		
		return null;
	}
	
	public String getName(Long branchId){
		if(!Validation.isEmptyLong(branchId)){
			for(Branch br:branch_list){
				if(br.getBranch_id().equals(branchId)){
					return br.getText45();
				}
			}
		}
		
		return null;
	}
	
	public List<Branch> getByBukrsOrBusinessAreaOfficesOnly(String a_bukrs,String a_user_bukrs,Long a_business_area_id,Long a_user_business_area_id, Long a_branch_id,Long a_user_branch_id)
	{ 
		List<Branch> wa_branch_list = new ArrayList<Branch>();
		if (a_bukrs.equals("")) { 
            return wa_branch_list; // Skip ajax requests.
        }
		//System.out.println(a_bukrs);
		//System.out.println(a_user_bukrs);
		//System.out.println(a_business_area_id);
		//System.out.println(a_user_business_area_id);
		//System.out.println(a_branch_id);
		//System.out.println(a_user_branch_id);
		//System.out.println(a_bukrs);
				//System.out.println(a_user_bukrs);
				//System.out.println(a_business_area_id);
				//System.out.println(a_user_business_area_id);
				//System.out.println(a_branch_id);
				//System.out.println(a_user_branch_id);
				
				/*if (a_branch_id!=null && a_branch_id>1 && a_bukrs!=null)
				{
					for (Branch wa_branch:branch_list)
					{
						if (wa_branch.getBukrs().equals(a_bukrs))
						{
							wa_branch_list.add(wa_branch);
						}
					}
					
					List<Branch> temp_branch_list = new ArrayList<Branch>();
					temp_branch_list = getBranchTree(a_bukrs, a_branch_id,wa_branch_list);
					return temp_branch_list;
					Branch wa_branch = l_branch_map.get(a_branch_id);
					if (wa_branch!=null)
					{
						wa_branch_list.add(wa_branch);
						return wa_branch_list;
					
				}}*/
				if (a_bukrs!=null && a_bukrs.length()>0)
				{
					//11111
					if (a_business_area_id != null && a_business_area_id>0 && !a_bukrs.equals(a_user_bukrs))
					{	
						
						for(Branch wa_branch:branch_list)
						{
							if (wa_branch.getBukrs().equals(a_bukrs) && wa_branch.getBusiness_area_id() != null && wa_branch.getBusiness_area_id().equals(a_business_area_id) && wa_branch.getType().equals(3L))
							{
								wa_branch_list.add(wa_branch);
							}				
						}
						sortByBranchName(wa_branch_list);
						return wa_branch_list;
					}
					//22222
					else if (a_business_area_id != null && a_business_area_id>0 && a_bukrs.equals(a_user_bukrs))
					{
						List<Branch> temp_branch_list = new ArrayList<Branch>();
						for (Branch wa_branch:branch_list)
						{
							if (wa_branch.getBukrs().equals(a_bukrs))
							{
								wa_branch_list.add(wa_branch);
							}
						}
//						if (a_user_branch_id!=null && a_user_branch_id.equals(60L))
//						{
//							for (Branch wa_branch:wa_branch_list)
//							{
//								if (wa_branch.getBukrs().equals(a_bukrs) && wa_branch.getType().equals(3L))
//								{
//									temp_branch_list.add(wa_branch);
//								}
//							}
//							return temp_branch_list;
//						}
//						else if(a_user_branch_id!=null)
//						{
//							Branch wa_br = l_branch_map.get(a_user_branch_id);
//							if (wa_br!=null && wa_br.getType()!=null && wa_br.getType().equals(3L))
//							{
//								temp_branch_list.add(wa_br);
//								return temp_branch_list;
//							}
//						}
//						
						
						temp_branch_list = getBranchTree(a_bukrs, a_user_branch_id,wa_branch_list);						
						if(a_user_branch_id!=null)
						{
							Branch wa_br = l_branch_map.get(a_user_branch_id);
							temp_branch_list.add(wa_br);
						}
						
						List<Branch> temp_branch_list2 = new ArrayList<Branch>();
						for(Branch wa_branch:temp_branch_list)
						{
							if (wa_branch.getBukrs().equals(a_bukrs) && wa_branch.getBusiness_area_id()!=null && wa_branch.getBusiness_area_id().equals(a_business_area_id) && wa_branch.getType().equals(3L))
							{
								temp_branch_list2.add(wa_branch);
							}				
						}
						sortByBranchName(temp_branch_list2);						
						return temp_branch_list2;
					}
					//333333
					else if (a_bukrs.equals(a_user_bukrs))
					{
						List<Branch> temp_branch_list = new ArrayList<Branch>();
						for (Branch wa_branch:branch_list)
						{
							if (wa_branch.getBukrs().equals(a_bukrs))
							{
								wa_branch_list.add(wa_branch);
							}
						}
//						if (a_user_branch_id!=null && a_user_branch_id.equals(60L) )
//						{
//							for (Branch wa_branch:wa_branch_list)
//							{
//								if (wa_branch.getBukrs().equals(a_bukrs) && wa_branch.getType().equals(3L))
//								{
//									temp_branch_list.add(wa_branch);
//								}
//							}
//							return temp_branch_list;
//						}					
//						else if(a_user_branch_id!=null)
//						{
//							Branch wa_br = l_branch_map.get(a_user_branch_id);
//							if (wa_br!=null && wa_br.getType()!=null && wa_br.getType().equals(3L))
//							{
//								temp_branch_list.add(wa_br);
//								return temp_branch_list;
//							}
//						}
//						
//						temp_branch_list = getBranchTree(a_bukrs, a_user_branch_id,wa_branch_list);
						
						temp_branch_list = getBranchTree(a_bukrs, a_user_branch_id,wa_branch_list);						
						if(a_user_branch_id!=null)
						{
							Branch wa_br = l_branch_map.get(a_user_branch_id);
							temp_branch_list.add(wa_br);
						}
						
						
						List<Branch> temp_branch_list2 = new ArrayList<Branch>();
						for(Branch wa_branch:temp_branch_list)
						{
							if (wa_branch.getType().equals(3L))
							{
								temp_branch_list2.add(wa_branch);
							}				
						}
						sortByBranchName(temp_branch_list2);
						return temp_branch_list2;
					}
					//444444
					else
					{
						for(Branch wa_branch:branch_list)
						{
							if (wa_branch.getBukrs().equals(a_bukrs) && (wa_branch.getType().equals(3L)))
							{
								wa_branch_list.add(wa_branch);
							}				
						}
						sortByBranchName(wa_branch_list);
						return wa_branch_list;
					}
				}
				else if (a_user_bukrs != null && a_user_bukrs.length()>0)
				{
					if (a_user_branch_id != null && a_user_branch_id>1)
					{
						List<Branch> temp_branch_list = new ArrayList<Branch>();
						for (Branch wa_branch:branch_list)
						{
							if (wa_branch.getBukrs().equals(a_user_bukrs))
							{
								wa_branch_list.add(wa_branch);
							}
						}
//						if (a_user_branch_id!=null && a_user_branch_id.equals(60L))
//						{
//							for (Branch wa_branch:wa_branch_list)
//							{
//								if (wa_branch.getBukrs().equals(a_bukrs) && wa_branch.getType().equals(3L))
//								{
//									temp_branch_list.add(wa_branch);
//								}
//							}
//							return temp_branch_list;
//						}
//						else if(a_user_branch_id!=null)
//						{
//							Branch wa_br = l_branch_map.get(a_user_branch_id);
//							if (wa_br!=null && wa_br.getType()!=null && wa_br.getType().equals(3L))
//							{
//								temp_branch_list.add(wa_br);
//								return temp_branch_list;
//							}
//						}
//						
//						temp_branch_list = getBranchTree(a_user_bukrs, a_user_branch_id,wa_branch_list);
						
						temp_branch_list = getBranchTree(a_bukrs, a_user_branch_id,wa_branch_list);						
						if(a_user_branch_id!=null)
						{
							Branch wa_br = l_branch_map.get(a_user_branch_id);
							temp_branch_list.add(wa_br);
						}
						
						List<Branch> temp_branch_list2 = new ArrayList<Branch>();
						for(Branch wa_branch:temp_branch_list)
						{
							if (wa_branch.getType().equals(3L))
							{
								temp_branch_list2.add(wa_branch);
							}				
						}
						sortByBranchName(temp_branch_list2);
						return temp_branch_list2;
					}
					else if (a_business_area_id != null && a_business_area_id>0)
					{
						List<Branch> temp_branch_list = new ArrayList<Branch>();
						for (Branch wa_branch:branch_list)
						{
							if (wa_branch.getBukrs().equals(a_user_bukrs))
							{
								wa_branch_list.add(wa_branch);
							}
						}
//						if (a_user_branch_id!=null && a_user_branch_id.equals(60L))
//						{
//							for (Branch wa_branch:wa_branch_list)
//							{
//								if (wa_branch.getBukrs().equals(a_bukrs) && wa_branch.getType().equals(3L))
//								{
//									temp_branch_list.add(wa_branch);
//								}
//							}
//							return temp_branch_list;
//						}
//						else if(a_user_branch_id!=null)
//						{
//							Branch wa_br = l_branch_map.get(a_user_branch_id);
//							if (wa_br!=null && wa_br.getType()!=null && wa_br.getType().equals(3L))
//							{
//								temp_branch_list.add(wa_br);
//								return temp_branch_list;
//							}
//						}
//						
//						temp_branch_list = getBranchTree(a_user_bukrs, a_user_branch_id,wa_branch_list);
						
						temp_branch_list = getBranchTree(a_bukrs, a_user_branch_id,wa_branch_list);						
						if(a_user_branch_id!=null)
						{
							Branch wa_br = l_branch_map.get(a_user_branch_id);
							temp_branch_list.add(wa_br);
						}
						
						List<Branch> temp_branch_list2 = new ArrayList<Branch>();
						for(Branch wa_branch:temp_branch_list)
						{
							if (wa_branch.getBukrs().equals(a_user_bukrs) && wa_branch.getBusiness_area_id()!=null && wa_branch.getBusiness_area_id().equals(a_business_area_id))
							{
								temp_branch_list2.add(wa_branch);
							}				
						}
						sortByBranchName(temp_branch_list2);
						return temp_branch_list2;
					}
					else
					{
						for(Branch wa_branch:branch_list)
						{
							if (wa_branch.getBukrs().equals(a_user_bukrs) && wa_branch.getType().equals(3L))
							{
								wa_branch_list.add(wa_branch);
							}				
						}
					}
								
				}
				sortByBranchName(wa_branch_list);
				return wa_branch_list;
		
	}
	public List<Branch> getByBukrsOrBusinessAreaCitiesOnly(String a_bukrs,String a_user_bukrs,Long a_business_area_id,Long a_user_business_area_id, Long a_branch_id,Long a_user_branch_id)
	{ 

				List<Branch> wa_branch_list = new ArrayList<Branch>();
				if (a_bukrs!=null && a_bukrs.length()>0)
				{
					if (a_business_area_id != null && a_business_area_id>0 && !a_bukrs.equals(a_user_bukrs))
					{	
						
						for(Branch wa_branch:branch_list)
						{
							if (wa_branch.getBukrs().equals(a_bukrs) && wa_branch.getBusiness_area_id() != null && wa_branch.getBusiness_area_id().equals(a_business_area_id) && wa_branch.getType().equals(4L))
							{
								wa_branch_list.add(wa_branch);
							}				
						}
						return wa_branch_list;
					}
					else if (a_business_area_id != null && a_business_area_id>0 && a_bukrs.equals(a_user_bukrs))
					{
						List<Branch> temp_branch_list = new ArrayList<Branch>();
						for (Branch wa_branch:branch_list)
						{
							if (wa_branch.getBukrs().equals(a_bukrs))
							{
								wa_branch_list.add(wa_branch);
							}
						}
						if (a_user_branch_id!=null)
						{
							for (Branch wa_branch:wa_branch_list)
							{
								if (wa_branch.getBukrs().equals(a_bukrs) && wa_branch.getType().equals(4L) && a_user_branch_id.equals(wa_branch.getBranch_id()))
								{
									temp_branch_list.add(wa_branch);
								}
								if (wa_branch.getBukrs().equals(a_bukrs) && wa_branch.getBranch_id().equals(60L) && a_user_branch_id!=null && a_user_branch_id.equals(60L))
								{
									temp_branch_list.add(wa_branch);
								}
							}
							return temp_branch_list;
						}
						
						temp_branch_list = wa_branch_list;
						//temp_branch_list = getBranchTree(a_bukrs, a_user_branch_id,wa_branch_list);
						List<Branch> temp_branch_list2 = new ArrayList<Branch>();
						for(Branch wa_branch:temp_branch_list)
						{
							if (wa_branch.getBukrs().equals(a_bukrs) && wa_branch.getBusiness_area_id()!=null && wa_branch.getBusiness_area_id().equals(a_business_area_id) && wa_branch.getType().equals(4L))
							{
								temp_branch_list2.add(wa_branch);
							}
							if (wa_branch.getBukrs().equals(a_bukrs) && wa_branch.getBranch_id().equals(60L) && a_user_branch_id!=null && a_user_branch_id.equals(60L))
							{
								temp_branch_list2.add(wa_branch);
							}
						}
						
						return temp_branch_list2;
					}
					else if (a_bukrs.equals(a_user_bukrs))
					{
						List<Branch> temp_branch_list = new ArrayList<Branch>();
						for (Branch wa_branch:branch_list)
						{
							if (wa_branch.getBukrs().equals(a_bukrs))
							{
								wa_branch_list.add(wa_branch);
							}
						}
						if (a_user_branch_id!=null)
						{
							Branch wa_br = l_branch_map.get(a_user_branch_id);
							temp_branch_list=getBranchTreeCity(a_bukrs,a_user_branch_id,wa_branch_list);
							if (wa_br.getBukrs().equals(a_bukrs) && wa_br.getType().equals(4L) && a_user_branch_id.equals(wa_br.getBranch_id()))
							{
								temp_branch_list.add(wa_br);
							}
							if (wa_br.getBukrs().equals(a_bukrs) && wa_br.getBranch_id().equals(60L) && a_user_branch_id!=null && a_user_branch_id.equals(60L))
							{
								temp_branch_list.add(wa_br);
							}
//							for (Branch wa_branch:wa_branch_list)
//							{
//								if (wa_branch.getBukrs().equals(a_bukrs) && wa_branch.getType().equals(4L) && a_user_branch_id.equals(wa_branch.getBranch_id()))
//								{
//									temp_branch_list.add(wa_branch);
//								}
//								if (wa_branch.getBukrs().equals(a_bukrs) && wa_branch.getBranch_id().equals(60L) && a_user_branch_id!=null && a_user_branch_id.equals(60L))
//								{
//									temp_branch_list.add(wa_branch);
//								}
//							}
							return temp_branch_list;
						}						
						temp_branch_list = wa_branch_list;
						//temp_branch_list = getBranchTree(a_bukrs, a_user_branch_id,wa_branch_list);
						List<Branch> temp_branch_list2 = new ArrayList<Branch>();
						for(Branch wa_branch:temp_branch_list)
						{
							if (wa_branch.getType().equals(4L))
							{
								temp_branch_list2.add(wa_branch);
							}		
							if (wa_branch.getBukrs().equals(a_bukrs) && wa_branch.getBranch_id().equals(60L) && a_user_branch_id!=null && a_user_branch_id.equals(60L))
							{
								temp_branch_list2.add(wa_branch);
							}
							
						}
						
						return temp_branch_list2;
					}
					else
					{
						for(Branch wa_branch:branch_list)
						{
							if (wa_branch.getBukrs().equals(a_bukrs) && (wa_branch.getType().equals(4L)))
							{
								wa_branch_list.add(wa_branch);
							}				
						}
						return wa_branch_list;
					}
				}
				
				else if (a_user_bukrs != null && a_user_bukrs.length()>0)
				{
					if (a_user_branch_id != null && a_user_branch_id>1)
					{
						List<Branch> temp_branch_list = new ArrayList<Branch>();
						for (Branch wa_branch:branch_list)
						{
							if (wa_branch.getBukrs().equals(a_user_bukrs))
							{
								wa_branch_list.add(wa_branch);
							}
						}
						if (a_user_branch_id!=null)
						{
							for (Branch wa_branch:wa_branch_list)
							{
								if (wa_branch.getBukrs().equals(a_bukrs) && wa_branch.getType().equals(4L) && a_user_branch_id.equals(wa_branch.getBranch_id()))
								{
									temp_branch_list.add(wa_branch);
								}
								if (wa_branch.getBukrs().equals(a_bukrs) && wa_branch.getBranch_id().equals(60L) && a_user_branch_id!=null && a_user_branch_id.equals(60L))
								{
									temp_branch_list.add(wa_branch);
								}
							}
							return temp_branch_list;
						}
						temp_branch_list = wa_branch_list;
						//temp_branch_list = getBranchTree(a_user_bukrs, a_user_branch_id,wa_branch_list);
						List<Branch> temp_branch_list2 = new ArrayList<Branch>();
						for(Branch wa_branch:temp_branch_list)
						{
							if (wa_branch.getType().equals(4L))
							{
								temp_branch_list2.add(wa_branch);
							}				
							if (wa_branch.getBukrs().equals(a_bukrs) && wa_branch.getBranch_id().equals(60L) && a_user_branch_id!=null && a_user_branch_id.equals(60L))
							{
								temp_branch_list2.add(wa_branch);
							}
						}
						
						return temp_branch_list2;
					}
					else if (a_business_area_id != null && a_business_area_id>0)
					{
						List<Branch> temp_branch_list = new ArrayList<Branch>();
						for (Branch wa_branch:branch_list)
						{
							if (wa_branch.getBukrs().equals(a_user_bukrs))
							{
								wa_branch_list.add(wa_branch);
							}
						}
						if (a_user_branch_id!=null)
						{
							for (Branch wa_branch:wa_branch_list)
							{
								if (wa_branch.getBukrs().equals(a_bukrs) && wa_branch.getType().equals(4L) && a_user_branch_id.equals(wa_branch.getBranch_id()))
								{
									temp_branch_list.add(wa_branch);
								}
								if (wa_branch.getBukrs().equals(a_bukrs) && wa_branch.getBranch_id().equals(60L) && a_user_branch_id!=null && a_user_branch_id.equals(60L))
								{
									temp_branch_list.add(wa_branch);
								}
							}
							return temp_branch_list;
						}
						temp_branch_list = wa_branch_list;
						//temp_branch_list = getBranchTree(a_user_bukrs, a_user_branch_id,wa_branch_list);
						List<Branch> temp_branch_list2 = new ArrayList<Branch>();
						for(Branch wa_branch:temp_branch_list)
						{
							if (wa_branch.getBukrs().equals(a_user_bukrs) && wa_branch.getBusiness_area_id()!=null && wa_branch.getBusiness_area_id().equals(a_business_area_id))
							{
								temp_branch_list2.add(wa_branch);
							}				
						}
						return temp_branch_list2;
					}
					else
					{
						for(Branch wa_branch:branch_list)
						{
							if (wa_branch.getBukrs().equals(a_user_bukrs) && wa_branch.getType().equals(4L))
							{
								wa_branch_list.add(wa_branch);
							}				
						}
					}
								
				}
				return wa_branch_list;
		
	}
	public String getAllEnabled(String a_bukrs,String a_branch_id)
    {
		if (a_bukrs!=null && a_bukrs.equals("1000") && a_branch_id!=null && (a_branch_id.equals("2") || a_branch_id.equals("60")  || a_branch_id.equals("213")))
		{
			return "false";
		}
		else if (a_bukrs!=null && a_bukrs.equals("2000") && a_branch_id!=null && (a_branch_id.equals("60") || a_branch_id.equals("102") || a_branch_id.equals("207")  || a_branch_id.equals("213")))
		{
			return "false";
		}
		else
		{
			return "true";
		}
    }
	
	public Branch getParentBranch(Long a_branch_id)
	{
		Branch wa_branch_child = new Branch();
		Branch wa_branch_parent = new Branch();
		if (a_branch_id!=null && a_branch_id>1)
		{
			wa_branch_child = l_branch_map.get(a_branch_id);
			wa_branch_parent = l_branch_map.get(wa_branch_child.getParent_branch_id());
		}
		return wa_branch_parent;
	}
	
	public void sortByBranchName(List<Branch> temp_branch_list) {

		   Collections.sort(temp_branch_list, new Comparator<Branch>() {

			@Override
			public int compare(Branch o1, Branch o2) {

				return o1.getText45().compareTo(o2.getText45());

			}
		   });
		}
}
