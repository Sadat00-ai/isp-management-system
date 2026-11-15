package isp;

import java.io.Serializable;
import java.util.ArrayList;

public class AppData implements Serializable {
    private static final long serialVersionUID = 1L;
    ArrayList<Customer> customers = new ArrayList<>();
    ArrayList<InternetPlan> plans = new ArrayList<>();
    ArrayList<Subscription> subscriptions = new ArrayList<>();
    ArrayList<Invoice> invoices = new ArrayList<>();
    ArrayList<Payment> payments = new ArrayList<>();
    ArrayList<SupportTicket> tickets = new ArrayList<>();
    int customerSeq = 1;
    int planSeq = 1;
    int subscriptionSeq = 1;
    int invoiceSeq = 1;
    int paymentSeq = 1;
    int ticketSeq = 1;
}
