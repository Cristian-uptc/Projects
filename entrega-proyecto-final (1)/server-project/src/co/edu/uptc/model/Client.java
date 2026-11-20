package co.edu.uptc.model;

public class Client extends User {

    private static final long serialVersionUID = 1L;

    private final String name;
    private final String documentId;
    private final String phone;

    public Client(String name, String documentId, String phone, String username, String password) {
        super(username, password);
        this.name = name;
        this.documentId = documentId;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public Role getRole() {
        return Role.CLIENT;
    }
}
