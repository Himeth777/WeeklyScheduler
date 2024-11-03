package org.example;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class MainWindow {
    private static LocalDate selectedMondayDate;
    private static List<Event> events = new ArrayList<>();
    private static JPanel panel;

    public static void Window() {
        JFrame frame = new JFrame("Weekly Scheduler");
        panel = new JPanel(new GridLayout(25, 8));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);

        Border cellBorder = BorderFactory.createLineBorder(Color.BLACK);

        JMenuBar menuBar = new JMenuBar();
        JMenuItem addEventItem = new JMenuItem("Add Event");
        JMenuItem saveEventItem = new JMenuItem("Save Schedule");

        addEventItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                addEventItem.setBackground(Color.LIGHT_GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                addEventItem.setBackground(UIManager.getColor("MenuItem.background"));
            }
        });

        addEventItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedMondayDate == null) {
                    JOptionPane.showMessageDialog(frame, "Please select a Monday date first.");
                    selectedMondayDate = promptForMondayDate(frame);
                }

                AddEventDialog eventDialog = new AddEventDialog(frame, selectedMondayDate, selectedMondayDate.plus(6, ChronoUnit.DAYS), events);
                eventDialog.setVisible(true);

                if (eventDialog.getEventName() != null) {
                    String eventName = eventDialog.getEventName();
                    String location = eventDialog.getlocation();
                    LocalTime startTime = eventDialog.getStartTime();
                    LocalTime endTime = eventDialog.getEndTime();
                    Color color = eventDialog.getSelectedColor();
                    LocalDate eventDate = eventDialog.getEventDate();

                    Event event = new Event(eventName, location, eventDate, startTime, endTime, color);
                    events.add(event);

                    updateEventDisplay();
                }
            }
        });

        saveEventItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventStorage.saveEventsToFile("src/main/resources/schedule.dat", events);
            }
        });

        menuBar.add(addEventItem);
        menuBar.add(saveEventItem);
        frame.setJMenuBar(menuBar);

        loadEventsFromFile("schedule.dat");

        updateColumnHeadings(panel);

        LocalTime startTime = LocalTime.of(8, 0);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (int row = 1; row <= 24; row++) {
            JLabel timeLabel = new JLabel(startTime.format(timeFormatter), JLabel.CENTER);
            timeLabel.setBorder(cellBorder);
            panel.add(timeLabel);
            startTime = startTime.plusMinutes(30);

            for (int col = 2; col <= 8; col++) {
                JLabel dataLabel = new JLabel("", JLabel.CENTER);
                dataLabel.setBorder(cellBorder);
                dataLabel.setOpaque(true);
                final Color originalColor = dataLabel.getBackground();

                int finalCol = col;
                int finalRow = row;
                dataLabel.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        if (finalCol == 7 && finalRow >= 10) {
                            JOptionPane.showMessageDialog(frame, "This cell is not clickable after 15:00.");
                        } else {
                            LocalDate eventDate = selectedMondayDate.plusDays(finalCol - 2);
                            LocalTime clickedTime = LocalTime.of(8 + (finalRow - 1) / 2, (finalRow - 1) % 2 * 30);
                            Event event = findEvent(eventDate, clickedTime);

                            if (event == null) {
                                event = findEventAcrossSpan(eventDate, clickedTime);
                            }

                            if (event != null) {
                                ViewEventDialog eventDialog = new ViewEventDialog(frame, event, events, selectedMondayDate);
                                eventDialog.setVisible(true);
                            } else {
                                JOptionPane.showMessageDialog(frame, "No event scheduled for this time slot.");
                            }
                        }
                    }

                    public void mouseEntered(MouseEvent e) {
                        JLabel cell = (JLabel) panel.getComponent(finalRow * 8 + finalCol);

                        if (cell.getText().isEmpty() && cell.getBackground().equals(originalColor)) {
                            cell.setBackground(Color.LIGHT_GRAY);
                            cell.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        }
                    }

                    public void mouseExited(MouseEvent e) {
                        JLabel cell = (JLabel) panel.getComponent(finalRow * 8 + finalCol);

                        if (cell.getBackground().equals(Color.LIGHT_GRAY)) {
                            cell.setBackground(originalColor);
                        }
                        cell.setCursor(Cursor.getDefaultCursor());
                    }
                });

                panel.add(dataLabel);
            }
        }

        frame.add(panel);
        frame.setVisible(true);
    }

    static void updateEventDisplay() {
        if (selectedMondayDate == null) {
            JOptionPane.showMessageDialog(null, "The Monday date is not set. Please restart the application and select a Monday date.");
            return;
        }

        for (Component component : panel.getComponents()) {
            if (component instanceof JLabel) {
                JLabel label = (JLabel) component;
                if (label.getText().isEmpty()) {
                    label.setBackground(Color.WHITE);
                }
            }
        }

        for (Event event : events) {
            LocalDate eventDate = event.getDate();
            LocalTime startTime = event.getStartTime();
            LocalTime endTime = event.getEndTime();
            Color eventColor = event.getColor();

            if (eventDate == null) {
                continue;
            }

            int columnIndex = (int) ChronoUnit.DAYS.between(selectedMondayDate, eventDate) + 1;
            int startRow = (startTime.getHour() - 8) * 2 + (startTime.getMinute() / 30) + 1;
            int endRow = (endTime.getHour() - 8) * 2 + (endTime.getMinute() / 30) + 1;

            for (int row = startRow; row < endRow; row++) {
                JLabel cell = (JLabel) panel.getComponent(row * 8 + columnIndex);
                cell.setBackground(eventColor);
                cell.setText(event.getName());
            }
        }
    }

    private static LocalDate promptForMondayDate(JFrame parentFrame) {
        LocalDate selectedDate = null;

        while (selectedDate == null || selectedDate.getDayOfWeek() != java.time.DayOfWeek.MONDAY) {
            String input = JOptionPane.showInputDialog(parentFrame, "Enter a date (YYYY-MM-DD) for Monday:", "Date Selection", JOptionPane.QUESTION_MESSAGE);

            try {
                selectedDate = LocalDate.parse(input);
                if (selectedDate.getDayOfWeek() != java.time.DayOfWeek.MONDAY) {
                    JOptionPane.showMessageDialog(parentFrame, "The date you selected is not a Monday. Please select a Monday.");
                    selectedDate = null;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(parentFrame, "Invalid date format. Please enter a valid date (YYYY-MM-DD).");
            }
        }

        return selectedDate;
    }

    private static void updateColumnHeadings(JPanel panel) {
        if (selectedMondayDate == null) {
            selectedMondayDate = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        }

        panel.add(new JLabel("Time", JLabel.CENTER));
        panel.add(new JLabel("Monday: " + selectedMondayDate.getDayOfMonth(), JLabel.CENTER));
        panel.add(new JLabel("Tuesday: " + selectedMondayDate.plus(1, ChronoUnit.DAYS).getDayOfMonth(), JLabel.CENTER));
        panel.add(new JLabel("Wednesday: " + selectedMondayDate.plus(2, ChronoUnit.DAYS).getDayOfMonth(), JLabel.CENTER));
        panel.add(new JLabel("Thursday: " + selectedMondayDate.plus(3, ChronoUnit.DAYS).getDayOfMonth(), JLabel.CENTER));
        panel.add(new JLabel("Friday: " + selectedMondayDate.plus(4, ChronoUnit.DAYS).getDayOfMonth(), JLabel.CENTER));
        panel.add(new JLabel("Saturday: " + selectedMondayDate.plus(5, ChronoUnit.DAYS).getDayOfMonth(), JLabel.CENTER));
        panel.add(new JLabel("Sunday: " + selectedMondayDate.plus(6, ChronoUnit.DAYS).getDayOfMonth(), JLabel.CENTER));
    }

    private static Event findEvent(LocalDate date, LocalTime startTime) {
        for (Event event : events) {
            if (event.getDate().equals(date) && event.getStartTime().equals(startTime)) {
                return event;
            }
        }
        return null;
    }

    private static Event findEventAcrossSpan(LocalDate date, LocalTime time) {
        for (Event event : events) {
            if (event.getDate().equals(date) &&
                    (time.isAfter(event.getStartTime()) && time.isBefore(event.getEndTime()))) {
                return event;
            }
        }
        return null;
    }

    private static void loadEventsFromFile(String filename) {
        List<Event> loadedEvents = EventStorage.loadEventsFromFile(filename);
        if (loadedEvents != null) {
            events = loadedEvents;
        }
        if (selectedMondayDate == null) {
            selectedMondayDate = promptForMondayDate(null);
        }
        updateEventDisplay();
    }

    public static void main(String[] args) {
        MainWindow mainWindow = new MainWindow();
        mainWindow.Window();
    }
}