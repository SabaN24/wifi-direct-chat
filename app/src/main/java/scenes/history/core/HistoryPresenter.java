package scenes.history.core;

import java.util.ArrayList;

import data.DataManager;
import scenes.history.model.ChatModel;

public class HistoryPresenter implements HistoryContractor.Presenter {

    private HistoryContractor.View view;

    public HistoryPresenter(HistoryContractor.View view) {
        this.view = view;
    }

    @Override
    public void start() {
        view.drawChats(DataManager.getAllChats());
    }

    @Override
    public void onResume() {
        view.drawChats(DataManager.getAllChats());
    }

    @Override
    public void clearHistoryButtonClicked() {
        DataManager.deleteAllChats();
        view.drawChats(new ArrayList<ChatModel>());
    }

    @Override
    public void removeChatButtonClicked(int id) {
        DataManager.deleteChat(id);
        view.drawChats(DataManager.getAllChats());
    }

}
