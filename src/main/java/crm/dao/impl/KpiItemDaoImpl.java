package crm.dao.impl;

import general.Validation;
import general.dao.impl.GenericDaoImpl;
import general.tables.KpiItem;
import org.springframework.stereotype.Component;

import crm.dao.KpiItemDao;

import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by admin on 22.11.2017.
 */
@Component
public class KpiItemDaoImpl extends GenericDaoImpl<KpiItem> implements KpiItemDao {
    @Override
    public List<KpiItem> findAllCurrent(Long branchId, Long positionId) {
        String s = "SELECT t1 FROM KpiItem t1 JOIN fetch t1.kpiSetting t2 " +
                " WHERE t2.branchId = :brId AND t2.positionId = :psId AND t2.fromDate < :now AND (t2.toDate IS NULL OR t2.toDate > :now )";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Query q = em.createQuery(s);
        q.setParameter("psId",positionId)
                .setParameter("brId",branchId)
                .setParameter("now",new Date());
        return q.getResultList();
    }

    @Override
    public List<KpiItem> findAllCurrent(List<Long> branchIds, List<Long> positionIds,Date fromDate, Date toDate) {
        String s = "SELECT t1 FROM KpiItem t1 JOIN fetch t1.kpiSetting t2 " +
                "  WHERE t2.fromDate <= :fromDate AND (t2.toDate IS NULL OR t2.toDate > :now ) ";
        if(!Validation.isEmptyCollection(branchIds)){
            s += " AND t2.branchId IN :brIds ";
        }

        if(!Validation.isEmptyCollection(positionIds)){
            s += " AND t2.positionId IN :posIds ";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Query q = em.createQuery(s);
        q.setParameter("fromDate",fromDate);
        if(!Validation.isEmptyCollection(branchIds)){
            q.setParameter("brIds",branchIds);
        }

        if(!Validation.isEmptyCollection(positionIds)){
            q.setParameter("posIds",positionIds);
        }

        return q.getResultList();
    }

    @Override
    public List<KpiItem> findAllByYearAndMonth(int year, int month) {
        String s = "SELECT t1 FROM KpiItem t1 JOIN fetch t1.kpiSetting t2 " +
                "  WHERE t2.year = :year AND t2.month = :month ";

        Query q = em.createQuery(s);
        q.setParameter("year",year)
            .setParameter("month",month);

        return q.getResultList();
    }

    @Override
    public List<KpiItem> findAll(String bukrs,Long branchId, Long positionId, int year, int month) {
        String s = "SELECT t1 FROM KpiItem t1 JOIN fetch t1.kpiSetting t2 " +
                "  WHERE t2.bukrs=:bukrs AND t2.year = :year AND t2.month = :month and t2.branchId = :brId and t2.positionId = :posId ";

        Query q = em.createQuery(s);
        q.setParameter("year",year)
                .setParameter("month",month)
                .setParameter("brId",branchId)
                .setParameter("posId",positionId)
                .setParameter("bukrs",bukrs);

        return q.getResultList();
    }

    @Override
    public List<KpiItem> findAllForAllBranches(String bukrs, Long positionId, int year, int month) {
        String s = "SELECT t1 FROM KpiItem t1 JOIN fetch t1.kpiSetting t2 " +
                "  WHERE t2.bukrs=:bukrs AND t2.year = :year AND t2.month = :month and (t2.branchId IS NULL OR t2.branchId = 0) and t2.positionId = :posId ";

        Query q = em.createQuery(s);
        q.setParameter("year",year)
                .setParameter("month",month)
                .setParameter("posId",positionId)
                .setParameter("bukrs",bukrs);

        return q.getResultList();
    }

    @Override
    public List<KpiItem> findAllForDataCalculatingForAllBranchesByPosition(List<Integer> indicators,Long positionId,int year,int month) {
        String s = "SELECT t1 FROM KpiItem t1 JOIN fetch t1.kpiSetting t2 " +
                "  WHERE t2 IS NOT NULL AND t1.indicatorId IN :inds AND t2.year = :year AND t2.month = :month" +
                " AND (t2.branchId = 0 OR t2.branchId IS NULL ) AND t2.positionId = :posId";

        Query q = em.createQuery(s);
        q.setParameter("inds",indicators)
                .setParameter("year",year)
                .setParameter("month",month)
                .setParameter("posId",positionId);
        return q.getResultList();
    }

    @Override
    public List<KpiItem> findAllByBranchCurrentForDataCalculating(List<Integer> indicators) {
        String s = " SELECT t1 FROM KpiItem t1 JOIN fetch t1.kpiSetting t2 " +
                "  WHERE t2 IS NOT NULL AND t1.indicatorId IN :inds AND t2.year = :year AND t2.month = :month " +
                " AND t2.branchId IS NOT NULL AND t2.branchId > 0 ";

        Calendar calendar = Calendar.getInstance();

        Query q = em.createQuery(s);
        q.setParameter("inds",indicators)
                .setParameter("year",calendar.get(Calendar.YEAR))
                .setParameter("month",calendar.get(Calendar.MONTH)+1);
        return q.getResultList();
    }

    @Override
    public List<KpiItem> findAllForReport(List<Long> branchIds, List<Long> positionIds, Integer year, Integer month) {
        String s = "SELECT t1 FROM KpiItem t1 JOIN fetch t1.kpiSetting t2 " +
                "  WHERE t2.year=:y AND t2.month = :m ";

        if(!Validation.isEmptyCollection(branchIds)){
            s += " AND t2.branchId IN :brIds ";
        }

        if(!Validation.isEmptyCollection(positionIds)){
            s += " AND t2.positionId IN :posIds ";
        }

        Query q = em.createQuery(s);
        q.setParameter("y",year)
            .setParameter("m",month);

        if(!Validation.isEmptyCollection(branchIds)){
            q.setParameter("brIds",branchIds);
        }

        if(!Validation.isEmptyCollection(positionIds)){
            q.setParameter("posIds",positionIds);
        }

        return q.getResultList();
    }
}