package co.edu.uptc.server;

import co.edu.uptc.model.User;

public class Session {

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
