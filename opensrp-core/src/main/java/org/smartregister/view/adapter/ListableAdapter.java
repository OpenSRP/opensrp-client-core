package org.smartregister.view.adapter;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.view.ListContract;
import org.smartregister.view.viewholder.ListableViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rkodev
 */

public abstract class ListableAdapter<T extends ListContract.Identifiable, H extends ListableViewHolder<T>>
        extends RecyclerView.Adapter<H> implements ListContract.Adapter<T> {

    protected List<T> items = new ArrayList<>();
    protected ListContract.View<T> view;

    public ListableAdapter(List<T> items, ListContract.View<T> view) {
        if (items != null)
            this.items.addAll(items);

        this.view = view;
    }

    @Override
    public void reloadData(@Nullable List<T> items) {
        this.items.clear();
        if (items != null)
            this.items.addAll(items);
    }

    @Override
    public RecyclerView.Adapter getRecyclerAdapter() {
        return this;
    }

    @Override
    public void onBindViewHolder(@NonNull H holder, int position) {
        holder.resetView();
        T t = items.get(position);
        if (t != null)
            holder.bindView(t, view);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
