package org.smartregister.cursoradapter.mock;

import android.database.Cursor;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.cursoradapter.RecyclerViewCursorAdapter;

/**
 * Created by samuelgithengi on 10/27/20.
 */
public class RecyclerViewCursorAdapterMock extends RecyclerViewCursorAdapter {
    public RecyclerViewCursorAdapterMock(Cursor cursor) {
        super(cursor);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {//mock method
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }
}
