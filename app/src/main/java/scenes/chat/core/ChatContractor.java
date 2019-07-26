package scenes.chat.core;

import java.util.List;

import scenes.chat.model.MessageModel;

public class ChatContractor {
    interface View {
        void draw(List<MessageModel> data);
        void clearInput();
        void sendMessage(String text);
        void showLoader();
        void hideLoader();
        void moveBack();
        void setTitle(String title);
        void setSubtitle(String subtitle);
        void prepareForChat();
    }

    interface Presenter {
        void start();
        void btnSendTapped(String text);
        void messageRecieved(String text);
        void btnCancelTapped();
    }
}
