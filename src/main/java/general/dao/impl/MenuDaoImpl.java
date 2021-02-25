package general.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import general.dao.MenuDao;
import general.tables.Menu;

import org.springframework.stereotype.Component;

@Component("menuDao")
public class MenuDaoImpl extends GenericDaoImpl<Menu> implements MenuDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<Menu> findAll(String cond) {
		String s = "SELECT m FROM Menu m";
		if (cond.length() > 0) {
			s += " WHERE " + cond;
		}
		Query query = em.createQuery(s);

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<Long, List<Menu>> getMenuMappedByTransactionId() {
		String s = " SELECT id,transaction_id,name_ru,(SELECT name_ru FROM menu m1 WHERE m1.id=m.parent_id) parent_name,(SELECT name_ru FROM menu m1 WHERE m1.id=m.tree_id) tree_name FROM menu m WHERE m.transaction_id > 0 ";
		Query q = em.createNativeQuery(s);
		List<Object[]> result = q.getResultList();
		Map<Long, List<Menu>> out = new HashMap<>();
		for (Object[] o : result) {
			Long id = Long.parseLong(String.valueOf(o[0]));
			Long trId = Long.parseLong(String.valueOf(o[1]));
			String name = String.valueOf(o[2]);
			String parentName = String.valueOf(o[3]);
			String treeName = String.valueOf(o[4]);
			Menu m = new Menu();
			m.setId(id);
			m.setTransaction_id(trId);
			m.setName(name);
			m.setParentName(parentName);
			m.setTreeName(treeName);
			List<Menu> temp = new ArrayList<>();
			if (out.containsKey(trId)) {
				temp = out.get(trId);
			}

			temp.add(m);
			out.put(trId, temp);
		}
		return out;
	}
}
