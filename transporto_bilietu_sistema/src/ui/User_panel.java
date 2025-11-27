package ui;
import route.Route;
import ticket.Ticket;
import user.User;
import service.Route_service;
import service.Ticket_service;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class User_panel extends JPanel {
    private final User user;
    private final Route_service route_service;
    private final Ticket_service ticket_service;

    private DefaultListModel<Route> routeListModel;
    private DefaultListModel<Ticket> ticketListModel;
    private JList<Route> routeList;
    private JList<Ticket> ticketList;

    public User_panel(User user, Route_service route_service, Ticket_service ticket_service) {
        this.user = user;
        this.route_service = route_service;
        this.ticket_service = ticket_service;
        init();
    }

    private void init() {
        setLayout(new BorderLayout());

        routeListModel = new DefaultListModel<>();
        routeList = new JList<>(routeListModel);

        ticketListModel = new DefaultListModel<>();
        ticketList = new JList<>(ticketListModel);

        refreshRoutes();
        refreshTickets();

        JPanel center = new JPanel(new GridLayout(1, 2));
        center.add(new JScrollPane(routeList));
        center.add(new JScrollPane(ticketList));

        JPanel buttons = new JPanel();

        JButton buyBtn = new JButton(new AbstractAction("Buy selected route") {
            public void actionPerformed(ActionEvent e) {
                buyTicket();
            }
        });

        JButton returnBtn = new JButton(new AbstractAction("Return selected ticket") {
            public void actionPerformed(ActionEvent e) {
                returnTicket();
            }
        });

        JButton refreshBtn = new JButton(new AbstractAction("Refresh") {
            public void actionPerformed(ActionEvent e) {
                refreshRoutes();
                refreshTickets();
            }
        });

        buttons.add(buyBtn);
        buttons.add(returnBtn);
        buttons.add(refreshBtn);

        add(new JLabel("Logged in as: " + user.getUsername(), SwingConstants.CENTER),
                BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }

    private void refreshRoutes() {
        routeListModel.clear();
        List<Route> routes = route_service.getAllRoutes();
        for (Route r : routes) {
            routeListModel.addElement(r);
        }
    }

    private void refreshTickets() {
        ticketListModel.clear();
        List<Ticket> tickets = ticket_service.getUserTickets(user.getUserID());
        for (Ticket t : tickets) {
            ticketListModel.addElement(t);
        }
    }

    private void buyTicket() {
        Route selected = routeList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select route first");
            return;
        }
        ticket_service.buyTicket(user.getUserID(), selected.getRoute_id());
        refreshTickets();
    }

    private void returnTicket() {
        Ticket selected = ticketList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select ticket");
            return;
        }
        ticket_service.returnTicket(selected.getTicket_id(), user.getUserID());
        refreshTickets();
    }
}
