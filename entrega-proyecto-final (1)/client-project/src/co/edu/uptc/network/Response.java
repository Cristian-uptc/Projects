package co.edu.uptc.network;

import java.io.Serializable;

public class Response implements Serializable {

    private static final long serialVersionUID = 1L;

    private final boolean success;
    private final String errorCode;
    private final Serializable data;
    private final boolean notification;

    private Response(boolean success, String errorCode, Serializable data, boolean notification) {
        this.success = success;
        this.errorCode = errorCode;
        this.data = data;
        this.notification = notification;
    }

    public static Response ok(Serializable data) {
        return new Response(true, null, data, false);
    }

    public static Response error(String errorCode) {
        return new Response(false, errorCode, null, false);
    }

    public static Response notification(Serializable data) {
        return new Response(true, null, data, true);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public boolean isNotification() {
        return notification;
    }

    @SuppressWarnings("unchecked")
    public <T> T getData() {
        return (T) data;
    }
}
