package general.dao.impl;

import general.dao.WriteoffRepairItemDao;
import general.tables.WriteoffRepairItem;

import org.springframework.stereotype.Component;

@Component("writeoffRepairItemDao")
public class WriteoffRepairItemDaoImpl extends GenericDaoImpl<WriteoffRepairItem>implements WriteoffRepairItemDao {
}
