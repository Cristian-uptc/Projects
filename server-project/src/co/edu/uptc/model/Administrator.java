package co.edu.uptc.model;

public class Administrator extends User {

    private static final long serialVersionUID = 1L;

    public Administrator(String username, String password) {
        super(username, password);
    }

    @Override
    public Role getRole() {
        return Role.ADMINISTRATOR;
    }
}
