package org.smartregister.cursoradapter;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;

/**
 * Created by keyman on 09/07/18.
 */
public class RecyclerViewPaginatedAdapter<V extends RecyclerView.ViewHolder> extends RecyclerViewCursorAdapter {
    private final RecyclerViewProvider<RecyclerView.ViewHolder> listItemProvider;
    private CommonRepository commonRepository;

    public int totalcount = 0;
    public int currentlimit = 20;
    public int currentoffset = 0;

    public RecyclerViewPaginatedAdapter(Cursor cursor,
                                        RecyclerViewProvider<RecyclerView.ViewHolder>
                                                listItemProvider, CommonRepository
                                                commonRepository) {
        super(cursor);
        this.listItemProvider = listItemProvider;
        this.commonRepository = commonRepository;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == RecyclerViewCursorAdapter.Type.FOOTER.ordinal()) {
            return listItemProvider.createFooterHolder(parent);
        } else {
            return listItemProvider.createViewHolder(parent);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        if (listItemProvider.isFooterViewHolder(viewHolder)) {
            listItemProvider.getFooterView(viewHolder, getCurrentPageCount(), getTotalPageCount(), hasNextPage(), hasPreviousPage());
        } else {
            CommonPersonObject personinlist = commonRepository.readAllcommonforCursorAdapter(cursor);
            CommonPersonObjectClient pClient = new CommonPersonObjectClient(personinlist.getCaseId(),
                    personinlist.getDetails(), personinlist.getDetails().get("FWHOHFNAME"));
            pClient.setColumnmaps(personinlist.getColumnmaps());
            listItemProvider.getView(cursor, pClient, viewHolder);
        }
    }

    // Pagination
    private int getCurrentPageCount() {
        if (currentoffset != 0) {
            if ((currentoffset / currentlimit) != 0) {
                return ((currentoffset / currentlimit) + 1);
            } else {
                return 1;
            }
        } else {
            return 1;
        }
    }

    private int getTotalPageCount() {
        if (totalcount % currentlimit == 0) {
            return (totalcount / currentlimit);
        } else {
            return ((totalcount / currentlimit) + 1);
        }
    }

    public boolean hasNextPage() {

        return ((totalcount > (currentoffset + currentlimit)));
    }

    public boolean hasPreviousPage() {
        return currentoffset != 0;
    }

    public void nextPageOffset() {
        currentoffset = currentoffset + currentlimit;
    }

    public void previousPageOffset() {
        currentoffset = currentoffset - currentlimit;
    }

    public void setTotalcount(int totalcount) {
        this.totalcount = totalcount;
    }

    public int getTotalcount() {
        return totalcount;
    }

    public void setCurrentoffset(int currentoffset) {
        this.currentoffset = currentoffset;
    }

    public int getCurrentoffset() {
        return currentoffset;
    }

    public void setCurrentlimit(int currentlimit) {
        this.currentlimit = currentlimit;
    }

    public int getCurrentlimit() {
        return currentlimit;
    }

}
