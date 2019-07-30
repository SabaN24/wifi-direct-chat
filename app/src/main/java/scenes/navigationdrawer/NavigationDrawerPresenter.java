package scenes.navigationdrawer;

public class NavigationDrawerPresenter implements NavigationDrawerContractor.Presenter {

    private NavigationDrawerContractor.View view;

    public NavigationDrawerPresenter(NavigationDrawerContractor.View view) {
        this.view = view;
    }

    @Override
    public void start() {
        view.showHistoryScene();
    }

    @Override
    public void historyTapped() { view.closeDrawer(); }

    @Override
    public void chatTapped() {
        view.closeDrawer();
        view.move2Chat();
    }
}
