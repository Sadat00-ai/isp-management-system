package isp;

import java.io.Serializable;

public class Subscription implements Serializable {
    private static final long serialVersionUID = 1L;
    int id, customerId, planId;
    String startDate, status;

    public Subscription(int id, int customerId, int planId, String startDate, String status) {
        this.id = id;
        this.customerId = customerId;
        this.planId = planId;
        this.startDate = startDate;
        this.status = status;
    }
}
