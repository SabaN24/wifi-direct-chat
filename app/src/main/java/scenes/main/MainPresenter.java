package scenes.main;

public class MainPresenter implements MainContractor.Presenter {

    private MainContractor.View view;

    public MainPresenter(MainContractor.View view) {
        this.view = view;
    }

    @Override
    public void start() {

    }
}
