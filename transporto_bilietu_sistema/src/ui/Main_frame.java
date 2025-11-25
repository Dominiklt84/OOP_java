package ui;
import users.Auth_service;
import users.Role;
import users.User;
import system.Ticket_system;
import system.System_ticket;

import javax.swing.*;
import java.awt.*;
public class Main_frame extends JFrame {
    private final Auth_service auth_service;
    private final Ticket_system system;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);

    public Main_frame() {
        this.auth_service = new Auth_service();
        this.system = new System_ticket();

        setTitle("Transporto bilietu sistema");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        Login_panel login_Panel = new Login_panel(auth_service, this::onLoginSuccess);
        User_panel user_Panel = new User_panel(auth_service, system);
        Admin_panel admin_Panel = new Admin_panel(auth_service, system);

        contentPanel.add(login_Panel, "LOGIN");
        contentPanel.add(user_Panel, "USER");
        contentPanel.add(admin_Panel, "ADMIN");

        setContentPane(contentPanel);
        cardLayout.show(contentPanel, "LOGIN");
    }

    private void onLoginSuccess() {
        User n = auth_service.getLoggedIn();
        if (n.getRole() == Role.ADMIN) {
            cardLayout.show(contentPanel, "ADMIN");
        } else {
            cardLayout.show(contentPanel, "USER");
        }
    }
}
