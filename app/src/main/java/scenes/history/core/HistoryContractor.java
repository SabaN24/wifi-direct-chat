package scenes.history.core;

import java.util.List;

import scenes.history.model.ChatModel;

public interface HistoryContractor {
    interface View {
        void drawChats(List<ChatModel> chats);
    }

    interface Presenter {
        void start();
        void onResume();
        void clearHistoryButtonClicked();
        void removeChatButtonClicked(int id);
    }
}
