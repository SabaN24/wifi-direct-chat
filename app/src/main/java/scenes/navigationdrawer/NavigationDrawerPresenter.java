package scenes.navigationdrawer;

public class NavigationDrawerPresenter implements NavigationDrawerContractor.Presenter {

    private NavigationDrawerContractor.View view;

    public NavigationDrawerPresenter(NavigationDrawerContractor.View view) {
        this.view = view;
    }

    @Override
    public void start() {
        view.move2History();
    }

    @Override
    public void historyTapped() {
        view.move2History();
    }

    @Override
    public void chatTapped() {
        view.move2Chat();
    }
}
