package ui;
import route.Route;
import transport.Transport_type;
import user.User;
import service.Route_service;
import service.Ticket_service;
import service.User_service;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

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

    private JTextField busPriceField;
    private JTextField trainPriceField;
    private JTextField planePriceField;

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
        tabs.addTab("Prices", createPricesPanel());
        tabs.addTab("Users", createUsersPanel());

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createRoutesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        routeListModel = new DefaultListModel<>();
        routeList = new JList<>(routeListModel);
        refreshRoutes();

        panel.add(new JScrollPane(routeList), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(5, 2, 5, 5));
        fromField = new JTextField();
        toField = new JTextField();
        distanceField = new JTextField();
        typeBox = new JComboBox<>(Transport_type.values());

        form.add(new JLabel("From:"));
        form.add(fromField);
        form.add(new JLabel("To:"));
        form.add(toField);
        form.add(new JLabel("Distance (km):"));
        form.add(distanceField);
        form.add(new JLabel("Type:"));
        form.add(typeBox);

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

    private JPanel createPricesPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));

        Map<Transport_type, Double> prices = ticket_service.getPricePerKm();

        busPriceField = new JTextField(String.valueOf(prices.getOrDefault(Transport_type.BUS, 0.2)));
        trainPriceField = new JTextField(String.valueOf(prices.getOrDefault(Transport_type.TRAIN, 0.1)));
        planePriceField = new JTextField(String.valueOf(prices.getOrDefault(Transport_type.PLANE, 0.5)));

        panel.add(new JLabel("Bus price per km:"));
        panel.add(busPriceField);
        panel.add(new JLabel("Train price per km:"));
        panel.add(trainPriceField);
        panel.add(new JLabel("Plane price per km:"));
        panel.add(planePriceField);

        JButton saveBtn = new JButton(new AbstractAction("Save prices") {
            public void actionPerformed(ActionEvent e) {
                savePrices();
            }
        });

        panel.add(new JLabel());
        panel.add(saveBtn);

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
        for (Route r : routes) {
            routeListModel.addElement(r);
        }
    }

    private void refreshUsers() {
        userListModel.clear();
        for (User u : user_service.getAllUsers()) {
            userListModel.addElement(u);
        }
    }

    private void addRoute() {
        try {
            String from = fromField.getText().trim();
            String to = toField.getText().trim();
            double dist = Double.parseDouble(distanceField.getText().trim());
            Transport_type type = (Transport_type) typeBox.getSelectedItem();
            route_service.createRoute(from, to, dist, type);
            refreshRoutes();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Distance must be a number");
        }
    }

    private void updateRoute() {
        Route selected = routeList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select route first");
            return;
        }
        try {
            selected.setFrom(fromField.getText().trim());
            selected.setTo(toField.getText().trim());
            selected.setDistance(Double.parseDouble(distanceField.getText().trim()));
            selected.setTransport_type((Transport_type) typeBox.getSelectedItem());
            route_service.updateRoute(selected);
            refreshRoutes();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Distance must be a number");
        }
    }

    private void deleteRoute() {
        Route selected = routeList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select route first");
            return;
        }
        route_service.deleteRoute(selected.getRoute_id());
        refreshRoutes();
    }

    private void savePrices() {
        try {
            double bus = Double.parseDouble(busPriceField.getText().trim());
            double train = Double.parseDouble(trainPriceField.getText().trim());
            double plane = Double.parseDouble(planePriceField.getText().trim());

            ticket_service.setPricePerKm(Transport_type.BUS, bus);
            ticket_service.setPricePerKm(Transport_type.TRAIN, train);
            ticket_service.setPricePerKm(Transport_type.PLANE, plane);

            JOptionPane.showMessageDialog(this, "Prices updated (not saved to file)");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "All prices must be numbers");
        }
    }

}
