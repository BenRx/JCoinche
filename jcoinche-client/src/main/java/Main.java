import controllers.ClientController;

public class Main {
    public static void main(String[] args) {
        ClientController clientController = new ClientController();

        while (clientController.mustRun()) {
            clientController.getCmdController().getCmd();
        }
    }
}
