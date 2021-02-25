package general.dao;

import java.util.List;

import general.tables.Prebseg;

public interface PrebsegDao extends GenericDao<Prebseg>{
	public int updateDynamicPrebseg(String a_dynamicSet, String a_dynamicWhere);
	public List<Prebseg> dynamicFindPrebseg(String a_dynamicWhere);
}
