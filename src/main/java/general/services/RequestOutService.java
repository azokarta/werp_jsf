package general.services;


import java.util.List;

import general.dao.DAOException;
import general.tables.Request;
import general.tables.RequestOut;
import general.tables.RequestOutMatnr;

import org.springframework.transaction.annotation.Transactional;

import user.User;

public interface RequestOutService {
	
	@Transactional
	void create(RequestOut r,List<RequestOutMatnr> reqMatnrs,List<Request> parentDocs, User user) throws DAOException;
	
	@Transactional
	void update(RequestOut r,List<RequestOutMatnr> reqMatnrs,List<Request> parentDocs,User user) throws DAOException;
}