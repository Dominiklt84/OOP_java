package ui;
import user.Role;
import user.User;
import service.*;
import javax.swing.*;
import java.awt.*;

public class Main_frame extends JFrame {
    private final Auth_service auth_service;
    private final User_service user_service;
    private final Ticket_service ticket_service;
    private final Route_service route_service;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public Main_frame(Auth_service auth_service,
                     Route_service route_service,
                     Ticket_service ticket_service,
                     User_service user_service) {
        super("Transport Ticket System");
        this.auth_service = auth_service;
        this.route_service = route_service;
        this.ticket_service = ticket_service;
        this.user_service = user_service;
        init();
    }

    private void init() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        createLoginAndRegisterPanels();

        setContentPane(cardPanel);
    }

    private void createLoginAndRegisterPanels() {
        Login_panel login_panel = new Login_panel(auth_service, new Login_panel.LoginListener() {
            public void onLoginSuccess(User user) {
                showUserPanel(user);
            }
            public void onShowRegister() {
                cardLayout.show(cardPanel, "register");
            }
        });

        Register_panel register_panel = new Register_panel(auth_service, new Register_panel.RegisterListener() {
            public void onRegisterSuccess(User user) {
                cardLayout.show(cardPanel, "login");
            }
            public void onShowLogin() {
                cardLayout.show(cardPanel, "login");
            }
        });

        cardPanel.add(login_panel, "login");
        cardPanel.add(register_panel, "register");

        cardLayout.show(cardPanel, "login");
    }

    private void showUserPanel(User user) {
        if (user.getRole() == Role.ADMIN) {
            Admin_panel adminPanel = new Admin_panel(route_service, ticket_service, user_service);
            cardPanel.add(adminPanel, "admin");
            cardLayout.show(cardPanel, "admin");
        } else {
            User_panel userPanel = new User_panel(user, route_service, ticket_service);
            cardPanel.add(userPanel, "client");
            cardLayout.show(cardPanel, "client");
        }
    }
}
