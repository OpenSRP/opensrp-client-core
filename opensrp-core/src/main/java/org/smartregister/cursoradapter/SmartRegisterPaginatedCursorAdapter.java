package org.smartregister.cursoradapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;

/**
 * Created by raihan on 3/9/16.
 */
public class SmartRegisterPaginatedCursorAdapter extends CursorAdapter {
    private final SmartRegisterCLientsProviderForCursorAdapter listItemProvider;
    Context context;
    CommonRepository commonRepository;

    public SmartRegisterPaginatedCursorAdapter(Context context, Cursor c,
                                               SmartRegisterCLientsProviderForCursorAdapter
                                                       listItemProvider, CommonRepository
                                                       commonRepository) {
        super(context, c);
        this.listItemProvider = listItemProvider;
        this.context = context;
        this.commonRepository = commonRepository;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return listItemProvider.inflatelayoutForCursorAdapter();
//        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        CommonPersonObject personinlist = commonRepository.readAllcommonforCursorAdapter(cursor);
        CommonPersonObjectClient pClient = new CommonPersonObjectClient(personinlist.getCaseId(),
                personinlist.getDetails(), personinlist.getDetails().get("FWHOHFNAME"));
        pClient.setColumnmaps(personinlist.getColumnmaps());
        listItemProvider.getView(cursor, pClient, view);

    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        return super.swapCursor(newCursor);
    }
}
