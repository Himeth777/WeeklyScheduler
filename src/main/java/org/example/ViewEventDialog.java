package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

public class ViewEventDialog extends JDialog {
    private Event event;
    private LocalDate selectedMondayDate;

    public ViewEventDialog(Frame owner, Event event, List<Event> events, LocalDate selectedMondayDate) {
        super(owner, "Event Details", true);
        this.event = event;
        this.selectedMondayDate = selectedMondayDate;
        setSize(300, 200);
        setLocationRelativeTo(owner);

        JPanel panel = new JPanel(new GridLayout(0, 1));

        JLabel nameLabel = new JLabel("Event Name: " + event.getName());
        JLabel locationLabel = new JLabel("Location: " + event.getLocation());
        JLabel dateLabel = new JLabel("Date: " + event.getDate());
        JLabel startTimeLabel = new JLabel("Start Time: " + event.getStartTime());
        JLabel endTimeLabel = new JLabel("End Time: " + event.getEndTime());

        panel.add(nameLabel);
        panel.add(locationLabel);
        panel.add(dateLabel);
        panel.add(startTimeLabel);
        panel.add(endTimeLabel);

        JButton editButton = new JButton("Edit");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EditEventDialog editDialog = new EditEventDialog(event, events, selectedMondayDate);
                editDialog.setVisible(true);
                updateEventDetails(panel);
                MainWindow.updateEventDisplay();
            }
        });

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirmation = JOptionPane.showConfirmDialog(owner,
                        "Are you sure you want to delete this event?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION);

                if (confirmation == JOptionPane.YES_OPTION) {
                    events.remove(event);
                    JOptionPane.showMessageDialog(owner, "Event deleted successfully.");
                    dispose();
                    MainWindow.updateEventDisplay();
                }
            }
        });

        panel.add(editButton);
        panel.add(deleteButton);
        add(panel);
    }

    private void updateEventDetails(JPanel panel) {
        ((JLabel) panel.getComponent(0)).setText("Event Name: " + event.getName());
        ((JLabel) panel.getComponent(1)).setText("Location: " + event.getLocation());
        ((JLabel) panel.getComponent(2)).setText("Date: " + event.getDate());
        ((JLabel) panel.getComponent(3)).setText("Start Time: " + event.getStartTime());
        ((JLabel) panel.getComponent(4)).setText("End Time: " + event.getEndTime());
    }
}
