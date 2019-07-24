package scenes.chat.core;

import java.util.List;

import scenes.chat.model.MessageModel;

public class ChatContractor {
    interface View {
        void draw(List<MessageModel> data);
        void clearInput();
    }

    interface Presenter {
        void start();
        void btnSendTapped(String text);
    }
}
