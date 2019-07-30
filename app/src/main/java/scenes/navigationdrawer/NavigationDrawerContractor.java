package scenes.navigationdrawer;

public interface NavigationDrawerContractor {
    interface View {
        void showHistoryScene();
        void move2Chat();
        void closeDrawer();
    }

    interface Presenter {
        void start();
        void historyTapped();
        void chatTapped();
    }
}
