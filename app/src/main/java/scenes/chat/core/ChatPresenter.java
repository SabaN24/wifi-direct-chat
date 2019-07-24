package scenes.chat.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import scenes.chat.model.MessageModel;

public class ChatPresenter implements ChatContractor.Presenter {

    private ChatContractor.View view;
    private List<MessageModel> data;

    public ChatPresenter(ChatContractor.View view) {
        this.view = view;
        data = new ArrayList<>();
    }

    @Override
    public void start() {

    }

    @Override
    public void btnSendTapped(String text) {
        text = text.trim();
        if(text.isEmpty()) { return; }
        view.clearInput();
        MessageModel newMessage = new MessageModel(0, text, new Date(), false, 0);
        data.add(newMessage);
        view.draw(data);
    }
}
