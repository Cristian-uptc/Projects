package co.edu.uptc.network;

public enum Action {
    LOGIN,
    REGISTER(true),
    LOGOUT,
    GET_COURTS,
    GET_ACTIVE_COURTS,
    ADD_COURT(true),
    EDIT_COURT(true),
    DEACTIVATE_COURT(true),
    GET_OCCUPIED_SLOTS,
    CREATE_RESERVATION(true),
    CANCEL_RESERVATION(true),
    GET_MY_RESERVATIONS,
    JOIN_WAITLIST(true),
    GET_MY_WAITLIST;

    private final boolean mutating;

    Action() {
        this(false);
    }

    Action(boolean mutating) {
        this.mutating = mutating;
    }

    public boolean isMutating() {
        return mutating;
    }
}
