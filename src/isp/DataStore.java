package isp;

import java.io.*;

public class DataStore {
    private static final String FILE_NAME = "isp-data.ser";

    public AppData load() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            Ui.info("First launch: loading fictional demonstration records.");
            return SampleData.create();
        }
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(file))) {
            Object saved = input.readObject();
            if (!(saved instanceof AppData)) {
                Ui.warn("Saved data format is not recognized. Loading demo records.");
                return SampleData.create();
            }
            return (AppData) saved;
        } catch (IOException | ClassNotFoundException e) {
            Ui.warn("Could not load saved data. Loading demo records. (" + e.getMessage() + ")");
            return SampleData.create();
        }
    }

    public void save(AppData data) {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            output.writeObject(data);
        } catch (IOException e) {
            Ui.warn("Data could not be saved: " + e.getMessage());
        }
    }
}
