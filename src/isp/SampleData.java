package isp;

/**
 * Creates fictional sample records for the first application launch.
 * All names, contact details and records are demonstration data.
 */
public class SampleData {
    private SampleData() { }

    public static AppData create() {
        AppData data = new AppData();

        data.customers.add(new Customer(data.customerSeq++, "Ayesha Rahman",
                "01700000001", "ayesha.demo@example.com", "Dhanmondi, Dhaka"));
        data.customers.add(new Customer(data.customerSeq++, "Tanvir Hasan",
                "01700000002", "tanvir.demo@example.com", "Mirpur, Dhaka"));
        data.customers.add(new Customer(data.customerSeq++, "Nusrat Jahan",
                "01700000003", "nusrat.demo@example.com", "Uttara, Dhaka"));
        data.customers.add(new Customer(data.customerSeq++, "Fahim Ahmed",
                "01700000004", "fahim.demo@example.com", "Bashundhara, Dhaka"));
        data.customers.add(new Customer(data.customerSeq++, "Mehedi Islam",
                "01700000005", "mehedi.demo@example.com", "Mohammadpur, Dhaka"));

        data.plans.add(new InternetPlan(data.planSeq++, "Basic Home", 20, 600,
                "Suitable for browsing and online classes"));
        data.plans.add(new InternetPlan(data.planSeq++, "Family Plus", 40, 900,
                "For streaming and multiple devices"));
        data.plans.add(new InternetPlan(data.planSeq++, "Gamer Pro", 60, 1300,
                "Low-latency package for gaming and heavy use"));
        data.plans.add(new InternetPlan(data.planSeq++, "Business Max", 100, 2200,
                "High-speed package for small businesses"));

        data.subscriptions.add(new Subscription(data.subscriptionSeq++, 1, 2, "2026-10-01", "ACTIVE"));
        data.subscriptions.add(new Subscription(data.subscriptionSeq++, 2, 1, "2026-10-05", "ACTIVE"));
        data.subscriptions.add(new Subscription(data.subscriptionSeq++, 3, 3, "2026-09-15", "ACTIVE"));
        data.subscriptions.add(new Subscription(data.subscriptionSeq++, 4, 4, "2026-08-20", "INACTIVE"));
        data.subscriptions.add(new Subscription(data.subscriptionSeq++, 5, 2, "2026-11-01", "ACTIVE"));

        data.invoices.add(new Invoice(data.invoiceSeq++, 1, 1, "October 2026", "2026-10-10", 900, "PAID"));
        data.invoices.add(new Invoice(data.invoiceSeq++, 2, 2, "November 2026", "2026-11-20", 600, "UNPAID"));
        data.invoices.add(new Invoice(data.invoiceSeq++, 3, 3, "November 2026", "2026-11-20", 1300, "UNPAID"));
        data.invoices.add(new Invoice(data.invoiceSeq++, 5, 5, "November 2026", "2026-11-25", 900, "UNPAID"));

        data.payments.add(new Payment(data.paymentSeq++, 1, 900, "2026-10-08", "Mobile Banking"));

        data.tickets.add(new SupportTicket(data.ticketSeq++, 2, "Slow internet speed",
                "Connection becomes slow during evening hours.", "2026-11-10", "IN PROGRESS"));
        data.tickets.add(new SupportTicket(data.ticketSeq++, 3, "Router configuration",
                "Need assistance with router configuration.", "2026-11-12", "OPEN"));
        data.tickets.add(new SupportTicket(data.ticketSeq++, 1, "Billing confirmation",
                "Customer requested confirmation of the previous payment.", "2026-11-05", "RESOLVED"));

        return data;
    }
}
