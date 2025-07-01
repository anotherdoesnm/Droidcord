package com.atipls.chat;

public class StopTypingThread extends Thread {
    com.atipls.chat.State s;
    Long userID;

    public StopTypingThread(com.atipls.chat.State s, long userID) {
        this.s = s;
        this.userID = userID;
    }

    public void run() {
        try {
            Thread.sleep(10000);
        } catch (Exception e) {
        }

        for (int i = 0; i < s.typingUsers.size(); i++) {
            if (s.typingUserIDs.get(i) == userID) {
                s.typingUsers.remove(i);
                s.typingUserIDs.remove(i);
                
                /*if (s.oldUI) {
                    s.oldChannelView.update();
                } else {
                    s.channelView.repaint();
                }*/
                return;
            }
        }
    }
}