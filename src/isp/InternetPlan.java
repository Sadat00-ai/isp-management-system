package isp;

import java.io.Serializable;

public class InternetPlan implements Serializable {
    private static final long serialVersionUID = 1L;
    int id;
    String name;
    int speedMbps;
    double monthlyPrice;
    String description;

    public InternetPlan(int id, String name, int speedMbps, double monthlyPrice, String description) {
        this.id = id;
        this.name = name;
        this.speedMbps = speedMbps;
        this.monthlyPrice = monthlyPrice;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Plan ID: " + id + " | " + name + " | " + speedMbps + " Mbps | Tk "
                + monthlyPrice + "/month | " + description;
    }
}
