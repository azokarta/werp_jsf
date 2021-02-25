package general.dao; 

import java.util.List;

import general.tables.MatnrType;

public interface MatnrTypeDao extends GenericDao<MatnrType> {
    public List<MatnrType> findAll(String condition);
}
