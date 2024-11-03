package org.example;

import javax.swing.*;
import java.io.*;
import java.util.List;

public class EventStorage {

    public static void saveEventsToFile(String filename, List<Event> events) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(events);
            JOptionPane.showMessageDialog(null, "Events saved successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving events: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static List<Event> loadEventsFromFile(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                return (List<Event>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(null, "Error loading events: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }
}