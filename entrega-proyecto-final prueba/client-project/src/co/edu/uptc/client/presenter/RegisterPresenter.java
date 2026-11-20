package co.edu.uptc.client.presenter;

import co.edu.uptc.client.Messages;
import co.edu.uptc.client.communication.ServerConnection;
import co.edu.uptc.client.view.RegisterView;
import co.edu.uptc.network.Action;
import co.edu.uptc.network.Response;

public class RegisterPresenter extends BasePresenter<RegisterView> {

    public RegisterPresenter(RegisterView view, ServerConnection connection) {
        super(view, connection);
        view.setRegisterAction(event -> register());
        view.setCancelAction(event -> view.dispose());
    }

    private void register() {
        if (view.getFullName().isEmpty() || view.getDocument().isEmpty()
                || view.getPhone().isEmpty() || view.getUsername().isEmpty()
                || view.getPassword().isEmpty()) {
            view.showError(Messages.error("EMPTY_FIELDS"));
            return;
        }
        sendAsync(Action.REGISTER, this::onRegistered, view.getFullName(), view.getDocument(),
                view.getPhone(), view.getUsername(), view.getPassword());
    }

    private void onRegistered(Response response) {
        view.showInfo("Registro exitoso. Ya puedes iniciar sesión.");
        view.dispose();
    }
}
