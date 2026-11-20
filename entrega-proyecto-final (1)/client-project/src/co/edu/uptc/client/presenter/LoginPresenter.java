package co.edu.uptc.client.presenter;

import co.edu.uptc.client.Messages;
import co.edu.uptc.client.communication.ServerConnection;
import co.edu.uptc.client.view.LoginView;
import co.edu.uptc.client.view.RegisterView;
import java.util.function.Consumer;
import co.edu.uptc.model.User;
import co.edu.uptc.network.Action;
import co.edu.uptc.network.Response;

public class LoginPresenter extends BasePresenter<LoginView> {

    private final Consumer<User> onLogin;

    public LoginPresenter(LoginView view, ServerConnection connection, Consumer<User> onLogin) {
        super(view, connection);
        this.onLogin = onLogin;
        view.setLoginAction(event -> login());
        view.setRegisterAction(event -> openRegister());
    }

    private void login() {
        if (view.getUsername().isEmpty() || view.getPassword().isEmpty()) {
            view.showError(Messages.error("EMPTY_FIELDS"));
            return;
        }
        sendAsync(Action.LOGIN, this::onLoginSuccess, view.getUsername(), view.getPassword());
    }

    private void onLoginSuccess(Response response) {
        User user = response.getData();
        view.dispose();
        onLogin.accept(user);
    }

    private void openRegister() {
        RegisterView registerView = new RegisterView();
        new RegisterPresenter(registerView, connection);
        registerView.setVisible(true);
    }
}
