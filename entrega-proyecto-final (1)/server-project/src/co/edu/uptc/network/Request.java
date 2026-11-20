package co.edu.uptc.network;

import java.io.Serializable;

public class Request implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Action action;
    private final Serializable[] params;

    public Request(Action action, Serializable... params) {
        this.action = action;
        this.params = params;
    }

    public Action getAction() {
        return action;
    }

    @SuppressWarnings("unchecked")
    public <T> T getParam(int index) {
        return (T) params[index];
    }
}
