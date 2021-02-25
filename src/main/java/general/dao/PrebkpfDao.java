package general.dao;


import java.util.List;

import general.tables.Prebkpf;


public interface PrebkpfDao  extends GenericDao<Prebkpf>{
	public int updateDynamicPreBkpf(String a_dynamicSet, String a_dynamicWhere);
	public List<Prebkpf> dynamicFindPrebkpf(String a_dynamicWhere);
}
