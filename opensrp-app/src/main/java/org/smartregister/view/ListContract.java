package org.smartregister.view;


import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.util.AppExecutors;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author rkodev
 */

public interface ListContract {

    interface Model<T extends ListContract.Identifiable> {

    }

    interface View<T extends ListContract.Identifiable> {

        void bindLayout();

        void renderData(List<T> identifiables);

        void refreshView();

        void setLoadingState(boolean loadingState);

        void onListItemClicked(T t, @IdRes int layoutID);

        /***
         * check fetch execution error and handle gracefully
         *
         * You can force close the view, retry a couple of times or display a new view
         *
         * @param ex
         */
        void onFetchError(Exception ex);

        @NonNull
        ListContract.Adapter<T> adapter();

        @NonNull
        Presenter<T> loadPresenter();

        boolean hasDivider();
    }

    interface Adapter<T extends ListContract.Identifiable>{
        void reloadData(@Nullable List<T> items);

        RecyclerView.Adapter getRecyclerAdapter();

        void notifyDataSetChanged();
    }

    interface Presenter<T extends ListContract.Identifiable> {

        void fetchList(@NonNull Callable<List<T>> callable, @NonNull AppExecutors.Request request);

        void onFetchRequestError(Exception e);

        void onItemsFetched(List<T> identifiables);

        /**
         * binds the view
         *
         * @param view
         */
        <V extends View<T>> Presenter<T> with(V view);

        /**
         * binds interactor
         *
         * @param interactor
         * @return
         */
        <I extends Interactor<T>> Presenter<T> using(I interactor);

        /**
         * binds a views model
         *
         * @param model
         * @return
         */
        <M extends Model<T>> Presenter<T> withModel(M model);

        @Nullable
        View<T> getView();

        <M extends Model<T>> M getModel();

        ListContract.Interactor<T> getInteractor();
    }

    interface Interactor<T extends ListContract.Identifiable> {

        /**
         * @param callable
         * @param presenter
         */
        void runRequest(@NonNull Callable<List<T>> callable, @NonNull AppExecutors.Request request, @NonNull Presenter<T> presenter);
    }

    interface Identifiable {
        @NonNull
        String getID();
    }


    interface AdapterViewHolder<T extends ListContract.Identifiable> {

        /**
         * bind view to object
         *
         * @param t
         */
        void bindView(T t, ListContract.View<T> view);

        /**
         * reset the view details
         */
        void resetView();
    }

}
