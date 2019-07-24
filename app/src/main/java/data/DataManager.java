package data;

import java.util.ArrayList;
import java.util.List;

import scenes.chat.model.MessageModel;
import scenes.history.model.ChatModel;

public class DataManager {

    public static List<ChatModel> getAllChats() {
        List<ChatModel> result = new ArrayList<>();
        List<ChatEntity> entities = Database.getInstance().chatDao().getAllChats();
        for (ChatEntity entity : entities) {
            result.add(new ChatModel(entity.getId(), entity.getDeviceName(), DateConverter.toDate(entity.getTime()),
                    Database.getInstance().messageDao().getChatMessagesCount(entity.getId())));
        }
        return result;
    }

    public ChatModel getChatById(int id) {
        ChatEntity entity = Database.getInstance().chatDao().getChatById(id);
        return new ChatModel(entity.getId(), entity.getDeviceName(), DateConverter.toDate(entity.getTime()),
                Database.getInstance().messageDao().getChatMessagesCount(entity.getId()));
    }

    public static void updateChat(ChatModel chat) {
        ChatEntity entity = new ChatEntity(chat.getId(), chat.getDeviceName(), DateConverter.fromDate(chat.getTime()));
        Database.getInstance().chatDao().updateChat(entity);
    }

    public static void deleteChat(int chatId) {
        Database.getInstance().messageDao().deleteChatMessages(chatId);
        Database.getInstance().chatDao().deleteChat(chatId);
    }

    public static void deleteAllChats() {
        Database.getInstance().chatDao().deleteAllChats();
    }

    public static List<MessageModel> getChatMessages(int chatId) {
        List<MessageModel> result = new ArrayList<>();
        List<MessageEntity> entities = Database.getInstance().messageDao().getChatMessages(chatId);
        for (MessageEntity entity : entities) {
            result.add(new MessageModel(entity.getId(), entity.getText(), DateConverter.toDate(entity.getTime()), entity.isResponse(), entity.getChatId()));
        }
        return result;
    }

    public static void updateMessage(MessageModel message) {
        MessageEntity entity = new MessageEntity(message.getId(), message.getText(), DateConverter.fromDate(message.getTime()), message.isResponse(), message.getChatId());
        Database.getInstance().messageDao().updateMessage(entity);
    }

}
