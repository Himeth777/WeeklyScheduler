package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class AddEventDialog extends JDialog {
    private JTextField eventNameField;
    private JTextField locationField;
    private JComboBox<String> colorComboBox;
    private JComboBox<LocalTime> startTimeComboBox;
    private JComboBox<LocalTime> endTimeComboBox;
    private JComboBox<LocalDate> dateComboBox;

    private String eventName;
    private String location;
    private LocalDate eventDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Color selectedColor;
    private LocalDate minDate;
    private LocalDate maxDate;
    private List<Event> existingEvents;

    public AddEventDialog(Frame owner, LocalDate minDate, LocalDate maxDate, List<Event> existingEvents) {
        super(owner, "Create Event", true);
        this.minDate = minDate;
        this.maxDate = maxDate;
        this.existingEvents = existingEvents;

        setLayout(new GridLayout(0, 2, 10, 10));

        LocalDate[] weekDates = new LocalDate[7];
        for (int i = 0; i < 7; i++) {
            weekDates[i] = minDate.plusDays(i);
        }
        dateComboBox = new JComboBox<>(weekDates);

        LocalTime[] times = new LocalTime[25];
        for (int i = 0; i < 25; i++) {
            times[i] = LocalTime.of(8 + (i / 2), (i % 2) * 30);
        }
        startTimeComboBox = new JComboBox<>(times);
        endTimeComboBox = new JComboBox<>(times);

        eventNameField = new JTextField(32);
        locationField = new JTextField(32);

        String[] colors = {"Red", "Green", "Yellow", "Blue", "Orange", "Gray"};
        colorComboBox = new JComboBox<>(colors);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eventName = eventNameField.getText();
                location = locationField.getText();
                eventDate = (LocalDate) dateComboBox.getSelectedItem();
                startTime = (LocalTime) startTimeComboBox.getSelectedItem();
                endTime = (LocalTime) endTimeComboBox.getSelectedItem();
                String color = (String) colorComboBox.getSelectedItem();

                if (eventName.length() > 32 || location.length() > 32) {
                    JOptionPane.showMessageDialog(AddEventDialog.this, "Event name and location must be 32 characters or less.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (endTime.isBefore(startTime)) {
                    JOptionPane.showMessageDialog(AddEventDialog.this, "End time cannot be before start time.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!isOnHalfHour(startTime) || !isOnHalfHour(endTime)) {
                    JOptionPane.showMessageDialog(AddEventDialog.this, "Start and end times must be at the beginning of the hour or half an hour.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                long duration = ChronoUnit.MINUTES.between(startTime, endTime);
                if (duration < 30 || duration > 180) {
                    JOptionPane.showMessageDialog(AddEventDialog.this, "Duration must be between 30 minutes and 3 hours.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (eventDate.isBefore(minDate) || eventDate.isAfter(maxDate)) {
                    JOptionPane.showMessageDialog(AddEventDialog.this, "Event date must be between " + minDate + " and " + maxDate + ".", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (checkOverlap(eventDate, startTime, endTime)) {
                    int confirm = JOptionPane.showConfirmDialog(
                            AddEventDialog.this,
                            "The event overlaps with another event. Do you want to proceed?",
                            "Event Overlap",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (confirm != JOptionPane.YES_OPTION) {
                        return;
                    }
                }

                switch (color) {
                    case "Red":
                        selectedColor = Color.RED;
                        break;
                    case "Green":
                        selectedColor = Color.GREEN;
                        break;
                    case "Yellow":
                        selectedColor = Color.YELLOW;
                        break;
                    case "Blue":
                        selectedColor = Color.BLUE;
                        break;
                    case "Orange":
                        selectedColor = Color.ORANGE;
                        break;
                    case "Gray":
                        selectedColor = Color.GRAY;
                        break;
                }

                dispose();
            }
        });

        add(new JLabel("Date:"));
        add(dateComboBox);
        add(new JLabel("Start Time:"));
        add(startTimeComboBox);
        add(new JLabel("End Time:"));
        add(endTimeComboBox);
        add(new JLabel("Event Name:"));
        add(eventNameField);
        add(new JLabel("Location:"));
        add(locationField);
        add(new JLabel("Color:"));
        add(colorComboBox);
        add(new JLabel());
        add(submitButton);

        pack();
        setLocationRelativeTo(owner);
    }

    private boolean checkOverlap(LocalDate eventDate, LocalTime startTime, LocalTime endTime) {
        for (Event existingEvent : existingEvents) {
            if (existingEvent.getDate().equals(eventDate)) {
                if (existingEvent.getStartTime().isBefore(endTime) && existingEvent.getEndTime().isAfter(startTime)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isOnHalfHour(LocalTime time) {
        return time.getMinute() == 0 || time.getMinute() == 30;
    }

    public String getEventName() {
        return eventName;
    }

    public String getlocation() {
        return location;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public Color getSelectedColor() {
        return selectedColor;
    }
}
