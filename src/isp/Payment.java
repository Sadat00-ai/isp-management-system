package isp;

import java.io.Serializable;

public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;
    int id, invoiceId;
    double amount;
    String date, method;

    public Payment(int id, int invoiceId, double amount, String date, String method) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.amount = amount;
        this.date = date;
        this.method = method;
    }
}
