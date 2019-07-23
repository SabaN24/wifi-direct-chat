package scenes.navigationdrawer;

public interface NavigationDrawerContractor {
    interface View {
        void move2History();
        void move2Chat();
    }

    interface Presenter {
        void start();
        void historyTapped();
        void chatTapped();
    }
}
