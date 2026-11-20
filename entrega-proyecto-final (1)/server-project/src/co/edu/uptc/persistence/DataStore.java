package co.edu.uptc.persistence;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import co.edu.uptc.model.ReservationSystem;

public class DataStore {

    private final Path path;

    public DataStore(String filePath) {
        this.path = Paths.get(filePath);
    }

    public ReservationSystem load() {
        if (!Files.exists(path)) {
            return null;
        }
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(path))) {
            return (ReservationSystem) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("LOAD_FAILED", e);
        }
    }

    public void save(ReservationSystem system) {
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(path))) {
                out.writeObject(system);
            }
        } catch (IOException e) {
            throw new IllegalStateException("SAVE_FAILED", e);
        }
    }
}
