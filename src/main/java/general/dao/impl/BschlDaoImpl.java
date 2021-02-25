package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import general.dao.BschlDao; 
import general.tables.Bschl; 

import org.springframework.stereotype.Component;

@Component("bschlDao")
public class BschlDaoImpl extends GenericDaoImpl<Bschl> implements BschlDao {
	public List<Bschl> findAll() {
		Query query = this.em
				.createQuery("select b FROM Bschl b order by sort");
		List<Bschl> l_bschl =  query.getResultList();
		return l_bschl;
	}
}
