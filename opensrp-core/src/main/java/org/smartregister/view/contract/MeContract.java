package org.smartregister.view.contract;

public interface MeContract {
    interface Presenter {

        void updateInitials();

        void updateName();

        String getBuildDate();
    }

    interface View {

        void updateInitialsText(String initials);

        void updateNameText(String name);
    }

    interface Interactor {

    }

    interface Model {
        String getInitials();

        String getName();

        String getBuildDate();
    }
}
