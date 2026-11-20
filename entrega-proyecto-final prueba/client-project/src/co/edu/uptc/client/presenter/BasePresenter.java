package co.edu.uptc.client.presenter;

import co.edu.uptc.client.Messages;
import co.edu.uptc.client.communication.ServerConnection;
import co.edu.uptc.client.view.BaseFrame;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.SwingWorker;
import co.edu.uptc.network.Action;
import co.edu.uptc.network.Request;
import co.edu.uptc.network.Response;

public abstract class BasePresenter<V extends BaseFrame> {

    protected final V view;
    protected final ServerConnection connection;

    protected BasePresenter(V view, ServerConnection connection) {
        this.view = view;
        this.connection = connection;
    }

    protected void sendAsync(Action action, Consumer<Response> onSuccess, Serializable... params) {
        new SwingWorker<Response, Void>() {
            @Override
            protected Response doInBackground() {
                return connection.send(new Request(action, params));
            }

            @Override
            protected void done() {
                try {
                    Response response = get();
                    if (response.isSuccess()) {
                        onSuccess.accept(response);
                    } else {
                        view.showError(Messages.error(response.getErrorCode()));
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    view.showError(Messages.error("CONNECTION_ERROR"));
                }
            }
        }.execute();
    }
}
