package general.dao;

import java.util.List;

import general.tables.Statement;

public interface StatementDao  extends GenericDao<Statement> {

	public List<Statement> findAll(String condition);
	public List<Statement> findAll();
}
