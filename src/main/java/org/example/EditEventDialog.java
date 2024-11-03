package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;

public class EditEventDialog extends JDialog {
    private JTextField nameField;
    private JTextField locationField;
    private JComboBox<LocalDate> dateField;
    private JComboBox<LocalTime> startTimeField;
    private JComboBox<LocalTime> endTimeField;
    private Event eventToEdit;
    private java.util.List<Event> events;

    public EditEventDialog(Event event, java.util.List<Event> events, LocalDate minDate) {
        super((Frame) null, "Edit Event", true);
        this.eventToEdit = event;
        this.events = events;
        setSize(300, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(0, 1));

        nameField = new JTextField(event.getName());
        locationField = new JTextField(event.getLocation());

        LocalTime[] times = new LocalTime[25];
        for (int i = 0; i < 25; i++) {
            times[i] = LocalTime.of(8 + (i / 2), (i % 2) * 30);
        }

        startTimeField = new JComboBox<>(times);
        endTimeField = new JComboBox<>(times);
        startTimeField.setSelectedItem(event.getStartTime());
        endTimeField.setSelectedItem(event.getEndTime());

        LocalDate[] weekDates = new LocalDate[7];
        for (int i = 0; i < 7; i++) {
            weekDates[i] = minDate.plusDays(i);
        }

        dateField = new JComboBox<>(weekDates);
        dateField.setSelectedItem(event.getDate());

        panel.add(new JLabel("Event Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Location:"));
        panel.add(locationField);
        panel.add(new JLabel("Date:"));
        panel.add(dateField);
        panel.add(new JLabel("Start Time:"));
        panel.add(startTimeField);
        panel.add(new JLabel("End Time:"));
        panel.add(endTimeField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String eventName = nameField.getText();
                String location = locationField.getText();
                LocalDate selectedDate = (LocalDate) dateField.getSelectedItem();
                LocalTime startTime = (LocalTime) startTimeField.getSelectedItem();
                LocalTime endTime = (LocalTime) endTimeField.getSelectedItem();

                if (startTime.isAfter(endTime)) {
                    JOptionPane.showMessageDialog(EditEventDialog.this, "End time must be after start time.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean overlapFound = false;
                for (Event existingEvent : events) {
                    if (!existingEvent.equals(eventToEdit) && existingEvent.getDate().equals(selectedDate) &&
                            ((startTime.isAfter(existingEvent.getStartTime()) && startTime.isBefore(existingEvent.getEndTime())) ||
                                    (endTime.isAfter(existingEvent.getStartTime()) && endTime.isBefore(existingEvent.getEndTime())) ||
                                    (startTime.equals(existingEvent.getStartTime()) || endTime.equals(existingEvent.getEndTime())))) {
                        overlapFound = true;
                        break;
                    }
                }

                if (overlapFound) {
                    int confirm = JOptionPane.showConfirmDialog(
                            EditEventDialog.this,
                            "The event overlaps with another event. Do you want to overwrite it?",
                            "Event Overlap",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (confirm != JOptionPane.YES_OPTION) {
                        return;
                    }
                }

                eventToEdit.setName(eventName);
                eventToEdit.setLocation(location);
                eventToEdit.setDate(selectedDate);
                eventToEdit.setStartTime(startTime);
                eventToEdit.setEndTime(endTime);
                dispose();
            }
        });

        panel.add(saveButton);
        add(panel);
    }
}
