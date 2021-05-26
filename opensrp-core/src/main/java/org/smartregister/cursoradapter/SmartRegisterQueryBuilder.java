package org.smartregister.cursoradapter;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.smartregister.commonregistry.CommonFtsObject;

import java.util.List;

/**
 * Created by raihan on 3/17/16.
 */
public class SmartRegisterQueryBuilder {
    String Selectquery;

    public SmartRegisterQueryBuilder(String selectquery) {
        Selectquery = selectquery;
    }

    public SmartRegisterQueryBuilder() {
    }

    public String getSelectquery() {
        return Selectquery;
    }

    public void setSelectquery(String selectquery) {
        Selectquery = selectquery;
    }

    /*
            This method takes in @param tablename and columns other than ID. Any special conditions
            for sorting if required can also be added in condition string and if not you can pass
             null.
            Alertname is the name of the alert you would like to sort this by.
             */
    public String queryForRegisterSortBasedOnRegisterAndAlert(String tablename, String[] columns,
                                                              String condition, String AlertName) {
        Selectquery = "Select " + tablename + ".id as _id";

        for (String column : columns) {
            Selectquery = Selectquery + " , " + column;
        }
        Selectquery = Selectquery + " FROM " + tablename;
        Selectquery = Selectquery + " LEFT JOIN alerts ";
        Selectquery = Selectquery + " ON " + tablename + ".id = alerts.caseID";
        if (condition != null) {
            Selectquery = Selectquery + " WHERE " + condition + " AND";
            Selectquery = Selectquery + " alerts.scheduleName = '" + AlertName + "' ";
        } else {
            Selectquery = Selectquery + " WHERE " + "alerts.scheduleName = '" + AlertName + "' ";
        }

        Selectquery = Selectquery + "ORDER BY CASE WHEN alerts.status = 'urgent' THEN '1'\n"
                + "WHEN alerts.status = 'upcoming' THEN '2'\n" + "WHEN alerts.status = 'normal' "
                + "THEN '3'\n" + "WHEN alerts.status = 'expired' THEN '4'\n" + "WHEN alerts.status "
                + "" + "" + "is Null THEN '5'\n" + "Else alerts.status END ASC";
        return Selectquery;
    }

    public String queryForCountOnRegisters(String tablename, String condition) {
        String Selectquery = "SELECT COUNT (*) ";
        Selectquery = Selectquery + " FROM " + tablename;
        if (condition != null) {
            Selectquery = Selectquery + " WHERE " + condition;
        }
        return Selectquery;
    }

    public String addlimitandOffset(String selectquery, int limit, int offset) {
        return selectquery + " LIMIT " + offset + "," + limit;
    }

    public String limitandOffset(int limit, int offset) {
        return Selectquery + " LIMIT " + offset + "," + limit;
    }

    public String Endquery(String selectquery) {
        return selectquery + ";";
    }

    public String selectInitiateMainTable(String tablename, String[] columns) {
        return selectInitiateMainTable(tablename, columns, "id");
    }

    public String selectInitiateMainTable(String tablename, String[] columns, String idColumn) {
        Selectquery = "Select " + tablename + "." + idColumn + " as _id";

        for (String column : columns) {
            Selectquery = Selectquery + " , " + column;
        }
        Selectquery = Selectquery + " FROM " + tablename;
        return Selectquery;
    }

    public String selectInitiateMainTable(String tablenames[], String[] columns) {
        Selectquery = "Select " + tablenames[0] + ".id as _id";
        for (String column : columns) {
            Selectquery = Selectquery + " , " + column;
        }

        StringBuilder sb = new StringBuilder();
        for (String str : tablenames) {
            sb.append(str).append(",");
        }
        //remove trailing ,
        sb.deleteCharAt(sb.length() - 1);

        Selectquery = Selectquery + " From " + sb.toString();
        return Selectquery;
    }

    public String selectInitiateMainTableCounts(String tablename) {
        Selectquery = "SELECT COUNT(*)";
        Selectquery = Selectquery + " FROM " + tablename;
        return Selectquery;
    }

    public String mainCondition(String condition) {
        Selectquery = Selectquery + (!condition.isEmpty() ? " WHERE " + condition + " " : "");
        return Selectquery;
    }

    public String addCondition(String condition) {
        Selectquery = Selectquery + " " + condition;
        return Selectquery;
    }

    public String orderbyCondition(String condition) {
        // No need to order a count query
        if (StringUtils
                .containsIgnoreCase(Selectquery.trim().substring(0, 15), "Select Count(*)" + "")) {
            return Selectquery;
        }

        Selectquery =
                Selectquery + (condition != null && !condition.isEmpty() ? " ORDER BY " + condition
                        + " " : "");
        return Selectquery;
    }

    public String joinwithALerts(String tablename, String alertname) {
        Selectquery = Selectquery + " LEFT JOIN alerts ";
        Selectquery = Selectquery + " ON " + tablename + ".id = alerts.caseID AND  alerts" + ""
                + ".scheduleName = '" + alertname + "'";
        return Selectquery;
    }

    public String joinwithALerts(String tablename) {
        Selectquery = Selectquery + " LEFT JOIN alerts ";
        Selectquery = Selectquery + " ON " + tablename + ".id = alerts.caseID ";
        return Selectquery;
    }

    public String customJoin(String query) {
        Selectquery = Selectquery + " " + query;
        return Selectquery;
    }

    @Override
    public String toString() {
        return Selectquery;
    }

    public String toStringFts(List<String> ids, String idColumn) {
        String res = Selectquery;

        // Remove where clause, Already used when fetching ids
        if (StringUtils.containsIgnoreCase(res, "WHERE")) {
            res = res.substring(0, res.toUpperCase().lastIndexOf("WHERE"));
        }

        if (ids.isEmpty()) {
            res += String.format(" WHERE %s IN () ", idColumn);
        } else {
            String joinedIds = "'" + StringUtils.join(ids, "','") + "'";
            res += String.format(" WHERE %s IN (%s) ", idColumn, joinedIds);
        }

        return res;
    }

    public String toStringFts(List<String> foundIds, String tName, String idCol, String sortBy) {
        String res = Selectquery;
        List<String> ids = foundIds;
        String tableName = tName;
        String idColumn = idCol;
        String sort = sortBy;

        if (StringUtils.containsIgnoreCase(res, "JOIN") && StringUtils.isNotBlank(tableName)) {
            idColumn = tableName + "." + idColumn;
            if (StringUtils.isNotBlank(sort)) {
                sort = tableName + "." + sort;
            }
        }
        // Remove where clause, Already used when fetching ids
        if (StringUtils.containsIgnoreCase(res, "WHERE")) {
            res = res.substring(0, res.toUpperCase().lastIndexOf("WHERE"));
        }

        if (ids.isEmpty()) {
            res += String.format(" WHERE %s IN () ", idColumn);
        } else {
            String joinedIds = "'" + StringUtils.join(ids, "','") + "'";
            res += String.format(" WHERE %s IN (%s) ", idColumn, joinedIds);

            if (StringUtils.isNotBlank(sort)) {
                res += " ORDER BY " + sort;
            }
        }

        return res;
    }

    public String searchQueryFts(String tablename, String searchJoinTable, String mainCondition,
                                 String searchFilter, String sort, int limit, int offset) {
        if (StringUtils.isNotBlank(searchJoinTable) && StringUtils.isNotBlank(searchFilter)) {
            String query = "SELECT " + CommonFtsObject.idColumn + " FROM " + CommonFtsObject
                    .searchTableName(tablename) + phraseClause(tablename, searchJoinTable,
                    mainCondition, searchFilter) + orderByClause(sort) + limitClause(limit, offset);
            return query;
        }
        String query = "SELECT " + CommonFtsObject.idColumn + " FROM " + CommonFtsObject
                .searchTableName(tablename) + phraseClause(mainCondition, searchFilter)
                + orderByClause(sort) + limitClause(limit, offset);
        return query;
    }

    public String countQueryFts(String tablename, String searchJoinTable, String mainCondition,
                                String searchFilter) {
        if (StringUtils.isNotBlank(searchJoinTable) && StringUtils.isNotBlank(searchFilter)) {
            String query = "SELECT COUNT(" + CommonFtsObject.idColumn + ") FROM " + CommonFtsObject
                    .searchTableName(tablename) + phraseClause(searchJoinTable, mainCondition,
                    searchFilter);
            return query;
        }
        String query = "SELECT COUNT(" + CommonFtsObject.idColumn + ") FROM " + CommonFtsObject
                .searchTableName(tablename) + phraseClause(mainCondition, searchFilter);
        return query;
    }

    private String phraseClause(String mainCondition, String phrase) {
        if (StringUtils.isNotBlank(phrase)) {
            String phraseClause =
                    " WHERE " + mainConditionClause(mainCondition) + CommonFtsObject.phraseColumn
                            + matchPhrase(phrase);
            return phraseClause;
        } else if (StringUtils.isNotBlank(mainCondition)) {
            return " WHERE " + mainCondition;
        }
        return "";
    }

    private String phraseClause(String joinTable, String mainCondition, String phrase) {
        String phraseClause =
                " WHERE " + mainConditionClause(mainCondition) + CommonFtsObject.phraseColumn
                        + matchPhrase(phrase) + " UNION SELECT "
                        + CommonFtsObject.relationalIdColumn + " FROM " + CommonFtsObject
                        .searchTableName(joinTable) + " " + "WHERE " + CommonFtsObject.phraseColumn
                        + matchPhrase(phrase);
        return phraseClause;
    }

    private String phraseClause(String tableName, String joinTable, String mainCondition, String
            phrase) {
        String phraseClause =
                " WHERE " + CommonFtsObject.idColumn + " IN ( SELECT " + CommonFtsObject.idColumn
                        + " FROM " + CommonFtsObject.searchTableName(tableName) + " WHERE "
                        + mainConditionClause(mainCondition) + CommonFtsObject.phraseColumn
                        + matchPhrase(phrase) + " UNION SELECT "
                        + CommonFtsObject.relationalIdColumn + " " + "FROM " + CommonFtsObject
                        .searchTableName(joinTable) + " WHERE " + CommonFtsObject.phraseColumn
                        + matchPhrase(phrase) + " )";
        return phraseClause;
    }

    private String phraseClause(String tableName, String joinTable[], String mainCondition, String
            phrase) {
        String join_queries[] = new String[joinTable.length];
        String join_query = "";
        for (int i = 0; i < joinTable.length; i++) {
            join_queries[i] = " SELECT " + CommonFtsObject.relationalIdColumn + " " + "FROM " + CommonFtsObject
                    .searchTableName(joinTable[i]) + " WHERE " + CommonFtsObject.phraseColumn
                    + matchPhrase(phrase) + " UNION ";
        }
        join_queries[join_queries.length - 1] = join_queries[join_queries.length - 1].replace(" UNION ", "");
        for (int i = 0; i < join_queries.length; i++) {
            join_query += join_queries[i] + " ";
        }
        String phraseClause =
                " WHERE " + CommonFtsObject.idColumn + " IN ( SELECT " + CommonFtsObject.idColumn
                        + " FROM " + CommonFtsObject.searchTableName(tableName) + " WHERE "
                        + mainConditionClause(mainCondition) + CommonFtsObject.phraseColumn
                        + matchPhrase(phrase) + " UNION " +
                        join_query
                        + " )";
        return phraseClause;
    }

    public String searchQueryFts(String tablename, String searchJoinTable[], String mainCondition,
                                 String searchFilter, String sort, int limit, int offset) {
        if (ArrayUtils.isNotEmpty(searchJoinTable) && StringUtils.isNotBlank(searchFilter)) {
            String query = "SELECT " + CommonFtsObject.idColumn + " FROM " + CommonFtsObject
                    .searchTableName(tablename) + phraseClause(tablename, searchJoinTable,
                    mainCondition, searchFilter) + orderByClause(sort) + limitClause(limit, offset);
            return query;
        }
        String query = "SELECT " + CommonFtsObject.idColumn + " FROM " + CommonFtsObject
                .searchTableName(tablename) + phraseClause(mainCondition, searchFilter)
                + orderByClause(sort) + limitClause(limit, offset);
        return query;
    }

    private String matchPhrase(String phrase) {
        if (phrase == null) {
            phrase = "";
        }

        // Underscore does not work well in fts search
        if (phrase.contains("_")) {
            phrase = phrase.replace("_", "");
        }
        return " MATCH '" + phrase + "*' ";

    }

    private String orderByClause(String sort) {
        if (StringUtils.isNotBlank(sort)) {
            return " ORDER BY " + sort;
        }
        return "";
    }

    private String limitClause(int limit, int offset) {
        return " LIMIT " + offset + "," + limit;
    }

    private String mainConditionClause(String mainCondition) {
        if (StringUtils.isNotBlank(mainCondition)) {
            return mainCondition += " AND ";
        } else {
            return mainCondition = "";
        }
    }
}
