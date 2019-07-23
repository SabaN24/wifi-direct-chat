package scenes.history;

public class HistoryPresenter implements HistoryContractor.Presenter {

    private HistoryContractor.View view;

    public HistoryPresenter(HistoryContractor.View view) {
        this.view = view;
    }

    @Override
    public void start() {

    }
}
