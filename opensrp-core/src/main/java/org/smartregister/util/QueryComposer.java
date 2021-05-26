package org.smartregister.util;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.commonregistry.CommonFtsObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ronald
 * Simplify query generation to allow generating complex queries piece by piece
 */
public class QueryComposer {

    private String mainSelect;
    private Columns columns;
    private String mainTable;
    private JoinClause joinClause;
    private WhereClause whereClause;
    private LimitClause limitClause;
    private SortClause sortClause;

    public static String getFtsQuery(String tableName, String searchPhrase) {
        return "select " + CommonFtsObject.idColumn + " from " + tableName + "_search where phrase match '\"" + searchPhrase + "\"*'";
    }

    public String generateQuery() throws InvalidQueryException {
        if (columns == null && mainSelect == null)
            throw new InvalidQueryException("Missing columns statement");

        if (mainTable == null && mainSelect == null)
            throw new InvalidQueryException("Missing main table");

        // generate the query
        StringBuilder builder = new StringBuilder(getMainSelect());

        if (whereClause != null)
            builder.append(" ").append(whereClause.composeQuery());

        if (sortClause != null)
            builder.append(" ").append(sortClause.composeQuery());

        if (limitClause != null)
            builder.append(limitClause.composeQuery());

        return builder.toString();
    }

    private String getMainSelect() {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isBlank(mainSelect)) {
            builder.append("SELECT ")
                    .append(columns.composeQuery())
                    .append(" FROM ")
                    .append(mainTable);

            if (joinClause != null)
                builder.append(" ").append(joinClause.composeQuery());

            return builder.toString();
        } else {
            return mainSelect;
        }
    }

    private Columns getColumns() {
        if (columns == null)
            columns = new Columns();

        return columns;
    }

    private JoinClause getJoinClause() {
        if (joinClause == null)
            joinClause = new JoinClause();

        return joinClause;
    }

    private LimitClause getLimitClause() {
        if (limitClause == null)
            limitClause = new LimitClause();

        return limitClause;
    }

    private SortClause getSortClause() {
        if (sortClause == null)
            sortClause = new SortClause();

        return sortClause;
    }

    public QueryComposer withMainTable(String mainTable) {
        this.mainTable = mainTable;
        return this;
    }

    public QueryComposer withMainSelect(String mainSelect) {
        this.mainSelect = mainSelect;
        return this;
    }

    public QueryComposer withColumns(List<String> values) {
        getColumns().addColumns(values);
        return this;
    }

    public QueryComposer withColumn(String value) {
        getColumns().addColumn(value);
        return this;
    }

    public QueryComposer withJoinClause(List<String> values) {
        getJoinClause().addJoinClause(values);
        return this;
    }

    public QueryComposer withJoinClause(String value) {
        getJoinClause().addJoinClause(value);
        return this;
    }

    private WhereClause getWhereClause() {
        if (whereClause == null)
            whereClause = new WhereClause();

        return whereClause;
    }

    public QueryComposer withWhereClause(List<String> values) {
        getWhereClause().addWhereClause(values);
        return this;
    }

    public QueryComposer withWhereClause(String value) {
        getWhereClause().addWhereClause(value);
        return this;
    }

    public QueryComposer withWhereClause(Disjoint disjoint) {
        getWhereClause().addWhereDisjoint(disjoint);
        return this;
    }

    public QueryComposer withLimitClause(int start, int end) {
        getLimitClause().addLimitClause(start, end);
        return this;
    }

    public QueryComposer withSortColumn(List<String> values) {
        getSortClause().addSortColumn(values);
        return this;
    }

    public QueryComposer withSortColumn(String value) {
        getSortClause().addSortColumn(value);
        return this;
    }

    public interface QueryValue {

        String composeQuery();

    }

    public static class Columns implements QueryValue {
        private List<String> strings;

        private Columns() {
            strings = new ArrayList<>();
        }

        private void addColumns(List<String> values) {
            if (strings == null || strings.size() == 0) return;
            for (String s : values) {
                addColumn(s);
            }
        }

        private void addColumn(String value) {
            if (StringUtils.isNotBlank(value))
                strings.add(value);
        }

        @Override
        public String composeQuery() {
            if (strings == null || strings.size() == 0)
                return "";

            StringBuilder builder = new StringBuilder();
            for (String s : strings) {
                if (builder.length() > 0) builder.append(" ,");

                builder.append(" ").append(s);
            }
            return builder.toString().trim();
        }
    }

    public static class JoinClause implements QueryValue {
        private List<String> strings;

        private JoinClause() {
            strings = new ArrayList<>();
        }

        private void addJoinClause(List<String> values) {
            if (strings == null || strings.size() == 0) return;
            for (String s : values) {
                addJoinClause(s);
            }
        }

        private void addJoinClause(String value) {
            if (StringUtils.isNotBlank(value))
                strings.add(value);
        }

        @Override
        public String composeQuery() {
            if (strings == null)
                return "";

            StringBuilder builder = new StringBuilder();
            for (String s : strings) {
                builder.append(" ").append(s);
            }
            return builder.toString().trim();
        }
    }

    public static class WhereClause implements QueryValue {
        private List<QueryValue> values;

        private WhereClause() {
            values = new ArrayList<>();
        }

        private void addWhereClause(List<String> values) {
            if (values == null || values.size() == 0) return;
            for (String s : values) {
                addWhereClause(s);
            }
        }

        private void addWhereClause(String value) {
            if (StringUtils.isNotBlank(value))
                values.add(new Phrase(value));
        }

        private void addWhereDisjoint(Disjoint disjoint) {
            if (disjoint != null)
                values.add(disjoint);
        }

        @Override
        public String composeQuery() {
            if (values == null || values.size() == 0)
                return "";

            StringBuilder builder = new StringBuilder(" WHERE ");
            int x = 0;
            for (QueryValue queryValue : values) {
                if (x > 0) builder.append(" ");

                if (x > 0) builder.append("AND ");
                builder.append("( ").append(queryValue.composeQuery()).append(" )");

                if (x == 0) x = 1;
            }
            return builder.toString().trim();
        }
    }

    private static class Phrase implements QueryValue {
        private String phrase;

        public Phrase(String phrase) {
            this.phrase = phrase;
        }

        @Override
        public String composeQuery() {
            return phrase;
        }
    }

    public static class Disjoint implements QueryValue {
        private List<String> strings;

        public Disjoint() {
            strings = new ArrayList<>();
        }

        public Disjoint addDisjointClause(String value) {
            if (StringUtils.isNotBlank(value))
                strings.add(value);

            return this;
        }

        @Override
        public String composeQuery() {
            if (strings == null || strings.size() == 0)
                return "";

            StringBuilder builder = new StringBuilder();
            int x = 0;
            for (String s : strings) {
                if (x > 0) builder.append(" ");

                if (x > 0) builder.append("OR ");
                builder.append("( ").append(s).append(" )");

                if (x == 0) x = 1;
            }
            return builder.toString().trim();
        }
    }

    public static class LimitClause implements QueryValue {
        private int start;
        private int end;

        private void addLimitClause(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public String composeQuery() {
            return " LIMIT " + start + " , " + end;
        }
    }

    public static class SortClause implements QueryValue {
        private List<String> strings;

        private SortClause() {
            strings = new ArrayList<>();
        }

        private void addSortColumn(List<String> values) {
            if (values == null) return;
            for (String s : values) {
                addSortColumn(s);
            }
        }

        private void addSortColumn(String value) {
            if (StringUtils.isNotBlank(value))
                strings.add(value);
        }

        @Override
        public String composeQuery() {
            if (strings == null || strings.size() == 0)
                return "";

            StringBuilder builder = new StringBuilder("ORDER BY");
            int x = 0;
            for (String s : strings) {
                if (x > 0) builder.append(",");

                builder.append(" ").append(s);

                if (x == 0) x = 1;
            }
            return builder.toString().trim();
        }
    }

    public static class InvalidQueryException extends Exception {

        public InvalidQueryException() {
        }

        public InvalidQueryException(String message) {
            super(message);
        }
    }
}