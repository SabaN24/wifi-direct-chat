package scenes.chat.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import common.Utils;
import data.DataManager;
import scenes.chat.model.MessageModel;
import scenes.history.model.ChatModel;

public class ChatPresenter implements ChatContractor.Presenter {

    private ChatContractor.View view;
    private List<MessageModel> data;
    private int chatId;
    private String deviceName;
    private boolean isHistory;

    ChatPresenter(ChatContractor.View view, int chatId) {
        this.view = view;
        this.chatId = chatId;
        isHistory = chatId != -1;
        data = new ArrayList<>();
    }

    @Override
    public void start() {
        view.hideLoader();
        Date time;
        if (isHistory) {
            view.hideLoader();
            ChatModel chatModel = DataManager.getChatById(chatId);
            deviceName = chatModel.getDeviceName();
            time = chatModel.getTime();
            data = DataManager.getChatMessages(chatId);
            view.draw(data);
        } else {
            time = new Date();
            ChatModel chatModel = DataManager.updateChat(new ChatModel(0, deviceName, time, 0));
            chatId = chatModel.getId();
        }
        view.setTitle(deviceName);
        view.setSubtitle(Utils.SDF.format(time));
    }

    @Override
    public void btnSendTapped(String text) {
        text = text.trim();
        if (text.isEmpty()) {
            return;
        }
        MessageModel newMessage = new MessageModel(0, text, new Date(), false, chatId);
        DataManager.updateMessage(newMessage);
        view.sendMessage(text);
        view.clearInput();
        data.add(newMessage);
        view.draw(data);
    }

    @Override
    public void messageReceived(String text) {
        MessageModel newMessage = new MessageModel(0, text, new Date(), true, chatId);
        DataManager.updateMessage(newMessage);
        data.add(newMessage);
        view.draw(data);
    }

    @Override
    public void btnCancelTapped() {
        view.moveBack();
    }
}
