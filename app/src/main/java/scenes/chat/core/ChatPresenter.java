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
    private boolean isHistory;

    ChatPresenter(ChatContractor.View view, int chatId) {
        this.view = view;
        this.chatId = chatId;
        isHistory = chatId != -1;
        data = new ArrayList<>();
    }

    @Override
    public void start() {
        if (isHistory) {
            view.hideSendPanel();
            view.hideLoader();
            ChatModel chatModel = DataManager.getChatById(chatId);
            String deviceName = chatModel.getDeviceName();
            Date time = chatModel.getTime();
            data = DataManager.getChatMessages(chatId);
            view.draw(data);
            view.setTitle(deviceName);
            view.setSubtitle(Utils.SDF.format(time));
        } else {
            view.searchForPeers();
        }
    }

    @Override
    public void createNewChat(String deviceName) {
        Date time = new Date();
        ChatModel chatModel = DataManager.updateChat(new ChatModel(0, deviceName, time, 0));
        view.showChatElements(deviceName, time);
        chatId = chatModel.getId();
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

    @Override
    public void btnDeleteTapped() {
        DataManager.deleteChat(chatId);
        view.moveBack();
    }

    @Override
    public boolean isHistory() {
        return isHistory;
    }

}
