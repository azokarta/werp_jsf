package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.dao.Fmglflext2Dao;
import general.tables.Fmglflext;
import general.tables.Fmglflext2;
@Component("fmglflext2Dao")
public class Fmglflext2DaoImpl extends GenericDaoImpl<Fmglflext2> implements Fmglflext2Dao{
	public Fmglflext2 findByIds(String a_bukrs,int a_gjahr,String a_hkont,String a_drcrk, Long a_branch_id){
    	Query query = this.em
                .createQuery("select f FROM Fmglflext2 f where f.bukrs= :bukrs"
                		+ " and f.gjahr = :gjahr"
                		+ " and f.branch_id = :branch_id"
                		+ " and f.hkont = :hkont"
                		+ " and f.drcrk = :drcrk");
        query.setParameter("bukrs", a_bukrs);   
        query.setParameter("gjahr", a_gjahr);  
        query.setParameter("hkont", a_hkont);  
        query.setParameter("branch_id", a_branch_id);
        query.setParameter("drcrk", a_drcrk);   
        Fmglflext2 fgl2 = (Fmglflext2) query.getSingleResult();
    	return fgl2;
    }
	public Long countByIds(String a_bukrs,int a_gjahr,String a_hkont,String a_drcrk, Long a_branch_id){
    	Query query = this.em
                .createQuery("select count(f.bukrs) FROM Fmglflext2 f where f.bukrs= :bukrs"
                		+ " and f.gjahr = :gjahr"
                		+ " and f.branch_id = :branch_id"
                		+ " and f.hkont = :hkont"
                		+ " and f.drcrk = :drcrk");
        query.setParameter("bukrs", a_bukrs);   
        query.setParameter("gjahr", a_gjahr);  
        query.setParameter("hkont", a_hkont);  
        query.setParameter("branch_id", a_branch_id);
        query.setParameter("drcrk", a_drcrk);    
        Long count = (Long) query.getSingleResult();        
        return count;
    }
	
	public List<Fmglflext2> findAll(String a_bukrs){
    	Query query = this.em
                .createQuery("select f FROM Fmglflext2 f where f.bukrs= :bukrs order by gjahr");
        query.setParameter("bukrs", a_bukrs);
        List<Fmglflext2> fgl =  query.getResultList();
    	return fgl;
    }
	public List<Fmglflext2> findAll(String a_bukrs,int a_gjahr){
		Query query = this.em
                .createQuery("select f FROM Fmglflext2 f where f.bukrs= :bukrs"
                		+ " and f.gjahr = :gjahr order by gjahr");
        query.setParameter("bukrs", a_bukrs);   
        query.setParameter("gjahr", a_gjahr);   
        List<Fmglflext2> fgl =  query.getResultList();
    	return fgl;
    }
}
