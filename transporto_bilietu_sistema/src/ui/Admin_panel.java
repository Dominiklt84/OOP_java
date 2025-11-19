package ui;

import users.Auth_service;
import system.Ticket_system;
import ticket.Ticket;
import javax.swing.*;
import java.awt.*;

public class Admin_panel extends JPanel {
    private final Ticket_system system;
    private final DefaultListModel<String> listModel = new DefaultListModel<>();

    public Admin_panel(Auth_service auth, Ticket_system system) {
        this.system= system;
        setLayout(new BorderLayout());

        JButton refresh = new JButton("Atnaujinti bilietu sarasa");
        JList<String> list = new JList<>(listModel);

        add(refresh, BorderLayout.NORTH);
        add(new JScrollPane(list), BorderLayout.CENTER);

        refresh.addActionListener(e -> {
            listModel.clear();
            for (Ticket b : system.getTickets()) {
                listModel.addElement(
                        b.getTransport().getClass().getSimpleName() + " - " + b.getPrice_ticket() + " EUR"
                );
            }
        });
    }
}
