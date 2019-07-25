package scenes.chat.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import data.DataManager;
import scenes.chat.model.MessageModel;
import scenes.history.model.ChatModel;

public class ChatPresenter implements ChatContractor.Presenter {

    private ChatContractor.View view;
    private List<MessageModel> data;
    private int chatId;
    private String deviceName;

    public ChatPresenter(ChatContractor.View view, int chatId, String deviceName) {
        this.view = view;
        this.chatId = chatId;
        this.deviceName = deviceName;
        data = new ArrayList<>();
    }

    @Override
    public void start() {
        if(chatId == -1) {
            ChatModel chatModel = new ChatModel(0, deviceName, new Date(), 0);
            chatModel = DataManager.updateChat(chatModel);
            chatId = chatModel.getId();
        }
    }

    @Override
    public void btnSendTapped(String text) {
        text = text.trim();
        if(text.isEmpty()) { return; }
        MessageModel newMessage = new MessageModel(0, text, new Date(), false, chatId);
        DataManager.updateMessage(newMessage);
        view.sendMessage(text);
        view.clearInput();
        data.add(newMessage);
        view.draw(data);
    }

    @Override
    public void messageRecieved(String text) {
        MessageModel newMessage = new MessageModel(0, text, new Date(), true, chatId);
        DataManager.updateMessage(newMessage);
        data.add(newMessage);
        view.draw(data);
    }
}
