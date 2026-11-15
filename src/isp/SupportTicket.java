package isp;

import java.io.Serializable;

public class SupportTicket implements Serializable {
    private static final long serialVersionUID = 1L;
    int id, customerId;
    String subject, details, date, status;

    public SupportTicket(int id, int customerId, String subject, String details,
                         String date, String status) {
        this.id = id;
        this.customerId = customerId;
        this.subject = subject;
        this.details = details;
        this.date = date;
        this.status = status;
    }
}
