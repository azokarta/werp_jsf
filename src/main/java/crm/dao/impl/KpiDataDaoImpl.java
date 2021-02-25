package crm.dao.impl;

import general.Validation;
import general.dao.impl.GenericDaoImpl;
import general.tables.KpiData;
import general.tables.Position;

import org.springframework.stereotype.Component;

import crm.dao.KpiDataDao;

import javax.persistence.Query;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 29.11.2017.
 */
@Component
public class KpiDataDaoImpl extends GenericDaoImpl<KpiData> implements KpiDataDao {
    @SuppressWarnings("unchecked")
	@Override
    public List<KpiData> findAll(int year, int month) {
        String s = "SELECT k FROM KpiData k WHERE year=:year AND month=:month ";
        Query q = em.createQuery(s);
        q.setParameter("year",year)
                .setParameter("month",month);
        return q.getResultList();
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<KpiData> findAll(int year, int month, Integer indicatorId) {
        String s = "SELECT k FROM KpiData k WHERE year=:year AND month=:month AND indicatorId=:indId";
        Query q = em.createQuery(s);
        q.setParameter("year",year)
            .setParameter("month",month)
            .setParameter("indId",indicatorId);

        return q.getResultList();
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<KpiData> findAllCurrentForManager(Long managerId) {
        String s = " select k from KpiData k where k.staffId = :managerId or k.staffId in(" +
                "select p.staff_id from Pyramid p where p.parent_pyramid_id in" +
                "   (select p1.id from Pyramid p1 where p1.staff_id=:managerId and p1.position_id = :posId)" +
                ")" +
                " and k.month = to_char(sysdate,'fmMM') and k.year = to_char(sysdate,'YYYY') ";
        Query q = em.createQuery(s);
        q.setParameter("managerId",managerId)
                .setParameter("posId", Position.MANAGER_POSITION_ID);

        return q.getResultList();
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<KpiData> findAllByBranchId(int year, int month, Long branchId) {
        String s = "SELECT k FROM KpiData k WHERE year=:year AND month=:month AND branchId=:branchId";
        Query q = em.createQuery(s);
        q.setParameter("year",year)
                .setParameter("month",month)
                .setParameter("branchId",branchId);

        return q.getResultList();
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<KpiData> findAllByBranchByPositionIds(int year, int month, Long branchId, List<Long> positionIds) {
        String s = "SELECT k FROM KpiData k WHERE year=:year AND month=:month AND branchId=:branchId and positionId in :posIds";
        Query q = em.createQuery(s);
        q.setParameter("year",year)
                .setParameter("month",month)
                .setParameter("branchId",branchId)
                .setParameter("posIds",positionIds);

        return q.getResultList();
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<KpiData> findAllByStaffIdsByPositionIds(int year, int month,List<Long> staffIds,List<Long> positionIds) {
        String s = "SELECT k FROM KpiData k WHERE year=:year AND month=:month AND staffId in :staffIds and positionId in :posIds";
        Query q = em.createQuery(s);
        q.setParameter("year",year)
                .setParameter("month",month)
                .setParameter("staffIds",staffIds)
                .setParameter("posIds",positionIds);

        return q.getResultList();
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<KpiData> findAll(int year, int month,Long branchId, Integer indicatorId, List<Long> staffIds) {
        String s = "SELECT k FROM KpiData k WHERE branchId = :brId AND year=:year AND month=:month AND indicatorId=:indId AND staffId in :staffIds";
        Query q = em.createQuery(s);
        q.setParameter("year",year)
                .setParameter("month",month)
                .setParameter("indId",indicatorId)
                .setParameter("staffIds",staffIds)
                .setParameter("brId",branchId);

        return q.getResultList();
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<KpiData> findAll(int year, int month,Long branchId, Integer indicatorId,Long positionId, List<Long> staffIds) {
        String s = "SELECT k FROM KpiData k " +
                " WHERE branchId = :brId AND " +
                " year=:year AND month=:month AND indicatorId=:indId AND " +
                " positionId = :positionId AND staffId in :staffIds";
        Query q = em.createQuery(s);
        q.setParameter("year",year)
                .setParameter("month",month)
                .setParameter("indId",indicatorId)
                .setParameter("staffIds",staffIds)
                .setParameter("brId",branchId)
                .setParameter("positionId",positionId);

        return q.getResultList();
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<KpiData> findAllForKpiRating(List<String> bukrs, List<Long> branchIds, List<Long> positionIds, int year, int month) {
        String s = "SELECT k FROM KpiData k WHERE bukrs in :bukrs and branchId in :branchIds and " +
                " positionId in :positionIds and year=:year AND month=:month ";
        Query q = em.createQuery(s);
        q.setParameter("year",year)
                .setParameter("month",month)
                .setParameter("bukrs",bukrs)
                .setParameter("branchIds",branchIds)
                .setParameter("positionIds",positionIds);

        return q.getResultList();
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<KpiData> findAllByBukrsAndBranches(int year, int month, String bukrs,List<Long> branchIds) {
        String s = "SELECT d FROM KpiData d WHERE d.year=:year AND d.month = :month AND d.bukrs = :bukrs AND d.branchId IN :brIds ";
        Query q = em.createQuery(s);
        q.setParameter("year",year)
            .setParameter("month",month)
            .setParameter("bukrs",bukrs)
            .setParameter("brIds",branchIds);
        return q.getResultList();
    }

    @Override
    public List<Object[]> findSumForKpiReportByBukrs(String bukrs,List<Long> branchIds,List<Long> positionIds,int year,int month) {
        String s = "select sum(value) as value, sum(done_value) as done_value,sum(point) as point,sum(score) as score,branch_id,indicator_id" +
                " from kpi_data where year=:y and month=:m and bukrs=:b and branch_id in :brIds" +
                " and position_id in :posIds " +
                " group by branch_id, indicator_id ";
        Query q = em.createNativeQuery(s);
        q.setParameter("y",year)
                .setParameter("m",month)
                .setParameter("b",bukrs)
                .setParameter("brIds",branchIds)
                .setParameter("posIds",positionIds);

        return q.getResultList();
    }

    @Override
    public List<Object[]> findSumGrouppedByStaff(String bukrs, List<Long> branchIds, List<Long> positionIds, int year, int month) {
        String s = "select sum(value) as value, sum(done_value) as done_value,sum(point) as point,sum(score) as score,staff_id,indicator_id" +
                " from kpi_data where year=:y and month=:m and bukrs=:b and branch_id in :brIds" +
                " and position_id in :posIds " +
                " group by staff_id, indicator_id ";
        Query q = em.createNativeQuery(s);
        q.setParameter("y",year)
                .setParameter("m",month)
                .setParameter("b",bukrs)
                .setParameter("brIds",branchIds)
                .setParameter("posIds",positionIds);

        return q.getResultList();
    }

    @Override
    public List<KpiData> findByStaffIds(String bukrs, List<Long> staffIds, int year, int month) {
        String s = "select d from KpiData d where staffId in :staffIds and bukrs=:bukrs and year=:year and month=:month";
        Query q = em.createQuery(s);
        q.setParameter("bukrs",bukrs)
                .setParameter("staffIds",staffIds)
                .setParameter("year",year)
                .setParameter("month",month);
        return q.getResultList();
    }

    @Override
    public List<Object[]> findSumForKpiRatingReport(List<String> bukrs, List<Long> branchIds, List<Long> positionIds, int year, int month) {
        String s = " select sum(value) as value, sum(done_value) as done_value, staff_id,position_id,branch_id " +
                " from kpi_data where bukrs in :bukrs and branch_id in :branchIds and position_id in :positionIds and year = :year and month = :month " +
                " group by branch_id, staff_id, position_id ";
        Query q = em.createNativeQuery(s);
        q.setParameter("bukrs",bukrs)
                .setParameter("branchIds",branchIds)
                .setParameter("positionIds",positionIds)
                .setParameter("year",year)
                .setParameter("month",month);

        return q.getResultList();
    }
}
