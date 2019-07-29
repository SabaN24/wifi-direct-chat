# wifi-direct-chat
Android Development (Spring 2019) Final Project

Application which allows two devices connected to the same Wi-Fi network to communicate through chat.
Application keeps history of old chats. Chats can be deleted.

Wi-Fi direct P2P communication was implemented based on Android Developers documentation
(https://developer.android.com/training/connect-devices-wirelessly/wifi-direct)
and tutorial videos given at link (https://www.youtube.com/watch?v=nw627o-8Fok&list=PLFh8wpMiEi88SIJ-PnJjDxktry4lgBtN3).
After two devices are connected using Wi-Fi P2P, two threads are launched for each device,
and the communication goes through Pipe, which itself is a thread.