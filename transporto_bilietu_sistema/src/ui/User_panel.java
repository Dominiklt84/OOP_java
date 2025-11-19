package ui;

import users.Auth_service;
import system.Ticket_system;
import ticket.Ticket;
import transport.*;
import javax.swing.*;
import java.awt.*;

public class User_panel extends JPanel {
    private final Auth_service auth;
    private final Ticket_system system;
    private final DefaultListModel<String> listModel = new DefaultListModel<>();

    public User_panel(Auth_service auth, Ticket_system system) {
        this.auth = auth;
        this.system = system;

        setLayout(new BorderLayout());

        JPanel top = new JPanel(new GridLayout(5, 2));
        JTextField startField = new JTextField();
        JTextField finalField = new JTextField();
        JTextField distField = new JTextField();
        JComboBox<String> type = new JComboBox<>(new String[]{"Autobusas", "Traukinys", "Lektuvas"});
        JButton buy = new JButton("Pirkti bilieta");

        top.add(new JLabel("Isvykimo miestas:")); top.add(startField);
        top.add(new JLabel("Atvykimo miestas:")); top.add(finalField);
        top.add(new JLabel("Atstumas (km):"));    top.add(distField);
        top.add(new JLabel("Transportas:"));      top.add(type);
        top.add(new JLabel(""));                  top.add(buy);

        JList<String> bilietuSarasas = new JList<>(listModel);
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(bilietuSarasas), BorderLayout.CENTER);

        buy.addActionListener(e -> {
            try {
                String startCity = startField.getText();
                String finalCity = finalField.getText();
                double distance = Double.parseDouble(distField.getText());
                String t = (String) type.getSelectedItem();

                Transport transport = switch (t) {
                    case "Autobusas" -> new Bus(startCity, finalCity, distance);
                    case "Traukinys" -> new Train(startCity, finalCity, distance);
                    case "Lektuvas" -> new Plane(startCity, finalCity, distance);
                    default -> null;
                };

                if (transport == null) return;

                Ticket b = system.buyTicket(transport);
                listModel.addElement(startCity + " -> " + finalCity + "   " + b.getPrice_ticket() + " EUR");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Netinkamas atstumas", "Klaida", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
