package isp;

import java.io.Serializable;

public class Invoice implements Serializable {
    private static final long serialVersionUID = 1L;
    int id, customerId, subscriptionId;
    String billingMonth, dueDate, status;
    double amount;

    public Invoice(int id, int customerId, int subscriptionId, String billingMonth,
                   String dueDate, double amount, String status) {
        this.id = id;
        this.customerId = customerId;
        this.subscriptionId = subscriptionId;
        this.billingMonth = billingMonth;
        this.dueDate = dueDate;
        this.amount = amount;
        this.status = status;
    }
}
