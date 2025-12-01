package ui;
import route.Route;
import transport.Transport;
import transport.Transport_type;
import user.User;
import service.Route_service;
import service.Ticket_service;
import service.User_service;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class Admin_panel extends JPanel {
    private final Route_service route_service;
    private final Ticket_service ticket_service;
    private final User_service user_service;

    private DefaultListModel<Route> routeListModel;
    private JList<Route> routeList;

    private JTextField fromField;
    private JTextField toField;
    private JTextField distanceField;
    private JComboBox<Transport_type> typeBox;
    private JTextField priceField;
    private DefaultListModel<User> userListModel;

    public Admin_panel(Route_service route_service,
                      Ticket_service ticket_service,
                      User_service user_service) {
        this.route_service = route_service;
        this.ticket_service = ticket_service;
        this.user_service = user_service;
        init();
    }

    private void init() {
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Routes", createRoutesPanel());
        tabs.addTab("Users", createUsersPanel());

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createRoutesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        routeListModel = new DefaultListModel<>();
        routeList = new JList<>(routeListModel);
        routeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        routeList.addListSelectionListener(e -> onRouteSelected());
        refreshRoutes();

        panel.add(new JScrollPane(routeList), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(6, 2, 5, 5));
        fromField = new JTextField();
        toField = new JTextField();
        distanceField = new JTextField();
        typeBox = new JComboBox<>(Transport_type.values());
        priceField = new JTextField();

        form.add(new JLabel("From:"));
        form.add(fromField);
        form.add(new JLabel("To:"));
        form.add(toField);
        form.add(new JLabel("Distance (km):"));
        form.add(distanceField);
        form.add(new JLabel("Type:"));
        form.add(typeBox);
        form.add(new JLabel("Price per km:"));
        form.add(priceField);

        JButton addBtn = new JButton(new AbstractAction("Add") {
            public void actionPerformed(ActionEvent e) {
                addRoute();
            }
        });
        JButton updateBtn = new JButton(new AbstractAction("Update selected") {
            public void actionPerformed(ActionEvent e) {
                updateRoute();
            }
        });
        JButton deleteBtn = new JButton(new AbstractAction("Delete selected") {
            public void actionPerformed(ActionEvent e) {
                deleteRoute();
            }
        });

        JPanel buttons = new JPanel();
        buttons.add(addBtn);
        buttons.add(updateBtn);
        buttons.add(deleteBtn);

        JPanel south = new JPanel(new BorderLayout());
        south.add(form, BorderLayout.CENTER);
        south.add(buttons, BorderLayout.SOUTH);

        panel.add(south, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        userListModel = new DefaultListModel<>();
        JList<User> list = new JList<>(userListModel);
        refreshUsers();
        panel.add(new JScrollPane(list), BorderLayout.CENTER);
        return panel;
    }

    private void refreshRoutes() {
        routeListModel.clear();
        List<Route> routes = route_service.getAllRoutes();
        for (Route r : routes) routeListModel.addElement(r);
    }

    private void refreshUsers() {
        userListModel.clear();
        for (User u : user_service.getAllUsers()) userListModel.addElement(u);
    }

    private void onRouteSelected() {
        Route sel = routeList.getSelectedValue();
        if (sel == null) return;
        fromField.setText(sel.getFrom());
        toField.setText(sel.getTo());
        distanceField.setText(String.valueOf(sel.getDistance()));
        typeBox.setSelectedItem(sel.getTransport().getType());
        priceField.setText(String.valueOf(sel.getTransport().getPricePerKm()));
    }

    private void addRoute() {
        try {
            String from = fromField.getText().trim();
            String to = toField.getText().trim();
            double dist = Double.parseDouble(distanceField.getText().trim());
            Transport_type type = (Transport_type) typeBox.getSelectedItem();
            double price = Double.parseDouble(priceField.getText().trim());

            Transport transport = Route_service.createTransportByType(type, price);
            route_service.createRoute(from, to, dist, transport);
            refreshRoutes();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Distance and price must be numbers");
        }
    }

    private void updateRoute() {
        Route sel = routeList.getSelectedValue();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Select route first");
            return;
        }
        try {
            sel.setFrom(fromField.getText().trim());
            sel.setTo(toField.getText().trim());
            sel.setDistance(Double.parseDouble(distanceField.getText().trim()));
            Transport_type type = (Transport_type) typeBox.getSelectedItem();
            double price = Double.parseDouble(priceField.getText().trim());
            Transport t = Route_service.createTransportByType(type, price);
            sel.setTransport(t);
            route_service.updateRoute(sel);
            refreshRoutes();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Distance and price must be numbers");
        }
    }

    private void deleteRoute() {
        Route sel = routeList.getSelectedValue();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Select route first");
            return;
        }
        route_service.deleteRoute(sel.getRoute_id());
        refreshRoutes();
    }
}
