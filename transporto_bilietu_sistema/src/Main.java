import repository.Route_repository;
import repository.User_repository;
import repository.Ticket_repository;
import file.File_user;
import file.File_route;
import file.File_ticket;
import service.*;
import ui.Main_frame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        User_repository userRepo = new File_user("users.txt");
        Route_repository routeRepo = new File_route("routes.txt");
        Ticket_repository ticketRepo = new File_ticket("tickets.txt");

        Auth_service authService = new Auth_service(userRepo);
        Route_service routeService = new Route_service(routeRepo);
        Ticket_service ticketService = new Ticket_service(ticketRepo, routeRepo);
        User_service userService = new User_service(userRepo);

        authService.createAdminIfNotExists("admin", "admin");

        SwingUtilities.invokeLater(() -> {
            Main_frame frame = new Main_frame(authService, routeService, ticketService, userService);
            frame.setVisible(true);
        });
    }
}
