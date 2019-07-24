package scenes.chat.core;

public class ChatPresenter implements ChatContractor.Presenter {

    private ChatContractor.View view;

    public ChatPresenter(ChatContractor.View view) {
        this.view = view;
    }

    @Override
    public void start() {

    }
}
