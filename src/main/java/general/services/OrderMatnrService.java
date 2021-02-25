package general.services;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import general.dao.DAOException;
import general.tables.OrderMatnr;

public interface OrderMatnrService {

	@Transactional
	void create( List<OrderMatnr> omList) throws DAOException;
}
