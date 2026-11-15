package isp;

public class Main {
    public static void main(String[] args) {
        for (String arg : args) {
            if ("--help".equals(arg) || "-h".equals(arg)) {
                System.out.println("ISP Management System");
                System.out.println("Usage: java -cp out isp.Main [options]");
                System.out.println("  --fancy     force colours and box drawing");
                System.out.println("  --plain     plain ASCII layout, no colours");
                System.out.println("  --no-anim   skip the start-up animation");
                System.out.println("  --echo      print typed input when input is piped (for demos)");
                return;
            }
        }
        Ui.init(args);
        ISPManagementSystem app = new ISPManagementSystem();
        app.start();
    }
}
