import transport.Transport_type;
import repository.Route_repository;
import repository.User_repository;
import repository.Ticket_repository;
import file.File_user;
import file.File_route;
import file.File_ticket;
import service.*;
import ui.Main_frame;
import javax.swing.*;
import java.util.EnumMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        User_repository userRepo = new File_user("users.txt");
        Route_repository routeRepo = new File_route("routes.txt");
        Ticket_repository ticketRepo = new File_ticket("tickets.txt");

        Auth_service authService = new Auth_service(userRepo);
        Route_service routeService = new Route_service(routeRepo);

        Map<Transport_type, Double> prices = new EnumMap<>(Transport_type.class);
        prices.put(Transport_type.BUS, 0.1);
        prices.put(Transport_type.TRAIN, 0.15);
        prices.put(Transport_type.PLANE, 0.5);

        Ticket_service ticketService = new Ticket_service(ticketRepo, routeRepo, prices);
        User_service userService = new User_service(userRepo);

        authService.createAdminIfNotExists("admin", "admin");

        SwingUtilities.invokeLater(() -> {
            Main_frame frame = new Main_frame(authService, routeService, ticketService, userService);
            frame.setVisible(true);
        });
    }
}
