package org.smartregister.view.dialog;

public interface FilterClause<T> {
    boolean filter(T object);
}
