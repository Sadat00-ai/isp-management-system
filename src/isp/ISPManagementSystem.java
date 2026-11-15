package isp;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class ISPManagementSystem {
    private final Scanner scanner = new Scanner(System.in);
    private final InputUtil input = new InputUtil(scanner);
    private final DataStore store = new DataStore();
    private AppData data;

    public void start() {
        Ui.splash();
        data = store.load();
        if (!login()) return;
        int choice;
        do {
            showMainMenu();
            choice = input.readInt("Enter choice: ");
            switch (choice) {
                case 1: customerMenu(); break;
                case 2: planMenu(); break;
                case 3: subscriptionMenu(); break;
                case 4: billingMenu(); break;
                case 5: paymentMenu(); break;
                case 6: supportMenu(); break;
                case 7: reportMenu(); break;
                case 0: store.save(data); Ui.goodbye(); break;
                default: Ui.err("Invalid option. Please choose a number from the menu.");
            }
            store.save(data);
        } while (choice != 0);
    }

    private boolean login() {
        Ui.println();
        List<String> hint = new ArrayList<String>();
        hint.add(Ui.dim("Demo login: admin / admin123"));
        Ui.panel("ADMIN LOGIN", hint, 44);
        for (int attempt = 1; attempt <= 3; attempt++) {
            String username = input.readText("Username: ");
            String password = input.readPassword("Password: ");
            if (username.equals("admin") && password.equals("admin123")) {
                Ui.ok("Login successful. Welcome, administrator!");
                return true;
            }
            Ui.err("Incorrect login. Attempts remaining: " + (3 - attempt));
        }
        Ui.err("Too many failed attempts.");
        return false;
    }

    private void showMainMenu() {
        Ui.clear();
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy", Locale.ENGLISH));
        Ui.topBar("ISP MANAGEMENT SYSTEM", "Admin  |  " + today);
        long activeLines = data.subscriptions.stream().filter(s -> s.status.equals("ACTIVE")).count();
        long unpaidBills = data.invoices.stream().filter(i -> i.status.equals("UNPAID")).count();
        long openTickets = data.tickets.stream().filter(t -> !t.status.equals("RESOLVED")).count();
        Ui.println();
        Ui.cards(new String[][] {
                {"CUSTOMERS", String.valueOf(data.customers.size()), "1;38;5;45"},
                {"ACTIVE LINES", String.valueOf(activeLines), "1;38;5;42"},
                {"UNPAID BILLS", String.valueOf(unpaidBills), "1;38;5;214"},
                {"OPEN TICKETS", String.valueOf(openTickets), "1;38;5;203"}
        });
        Ui.println();
        Ui.menu("MAIN MENU", new String[][] {
                {"1", "Customer Management"},
                {"2", "Internet Plan Management"},
                {"3", "Connection / Subscription Management"},
                {"4", "Billing and Invoice Management"},
                {"5", "Payment Management"},
                {"6", "Complaint / Support Management"},
                {"7", "Reports"},
                {"0", "Save and Exit"}
        });
    }

    private void customerMenu() {
        int c;
        do {
            Ui.println();
            Ui.menu("CUSTOMER MANAGEMENT", new String[][] {
                    {"1", "Add customer"}, {"2", "View all customers"}, {"3", "Search by ID"},
                    {"4", "Update customer"}, {"5", "Delete customer"}, {"0", "Back to main menu"}
            });
            c = input.readInt("Choice: ");
            switch (c) {
                case 1:
                    String name = input.readText("Full name: ");
                    String phone = input.readText("Phone: ");
                    String email = input.readText("Email: ");
                    String address = input.readText("Address: ");
                    data.customers.add(new Customer(data.customerSeq++, name, phone, email, address));
                    Ui.ok("Customer added (ID " + (data.customerSeq - 1) + ").");
                    break;
                case 2: listCustomers(); break;
                case 3:
                    int id = input.readInt("Customer ID: ");
                    Customer found = findCustomer(id);
                    if (found == null) Ui.err("Customer not found.");
                    else Ui.record("CUSTOMER #" + found.id, new String[][] {
                            {"Name", found.name}, {"Phone", found.phone},
                            {"Email", found.email}, {"Address", found.address}});
                    break;
                case 4:
                    Customer update = findCustomer(input.readInt("Customer ID to update: "));
                    if (update == null) Ui.err("Customer not found.");
                    else {
                        update.name = input.readText("New name: ");
                        update.phone = input.readText("New phone: ");
                        update.email = input.readText("New email: ");
                        update.address = input.readText("New address: ");
                        Ui.ok("Customer updated.");
                    }
                    break;
                case 5:
                    int deleteId = input.readInt("Customer ID to delete: ");
                    if (hasSubscription(deleteId)) Ui.warn("Cannot delete: customer has subscription records.");
                    else if (data.customers.removeIf(x -> x.id == deleteId)) Ui.ok("Customer deleted.");
                    else Ui.err("Customer not found.");
                    break;
                case 0: break;
                default: Ui.err("Invalid option.");
            }
        } while (c != 0);
    }

    private void listCustomers() {
        Ui.heading("Customers");
        if (data.customers.isEmpty()) { Ui.info("No customers found."); return; }
        List<String[]> rows = new ArrayList<String[]>();
        for (Customer c : data.customers)
            rows.add(new String[] {String.valueOf(c.id), c.name, c.phone, c.email, c.address});
        Ui.table(new String[] {"ID", "Name", "Phone", "Email", "Address"}, rows, new int[] {}, new int[] {});
    }

    private void planMenu() {
        int c;
        do {
            Ui.println();
            Ui.menu("INTERNET PLAN MANAGEMENT", new String[][] {
                    {"1", "Add plan"}, {"2", "View plans"}, {"3", "Update plan"},
                    {"4", "Delete plan"}, {"0", "Back to main menu"}
            });
            c = input.readInt("Choice: ");
            switch (c) {
                case 1:
                    String name = input.readText("Plan name: ");
                    int speed = input.readInt("Speed (Mbps): ");
                    double price = input.readDouble("Monthly price (Tk): ");
                    String desc = input.readText("Description: ");
                    if (speed <= 0) { Ui.err("Speed must be positive."); break; }
                    data.plans.add(new InternetPlan(data.planSeq++, name, speed, price, desc));
                    Ui.ok("Plan added (ID " + (data.planSeq - 1) + ").");
                    break;
                case 2: listPlans(); break;
                case 3:
                    InternetPlan p = findPlan(input.readInt("Plan ID to update: "));
                    if (p == null) Ui.err("Plan not found.");
                    else {
                        String newName = input.readText("New plan name: ");
                        int newSpeed = input.readInt("New speed (Mbps): ");
                        double newPrice = input.readDouble("New monthly price: ");
                        String newDesc = input.readText("New description: ");
                        if (newSpeed <= 0) Ui.err("Speed must be positive. Plan not updated.");
                        else {
                            p.name = newName;
                            p.speedMbps = newSpeed;
                            p.monthlyPrice = newPrice;
                            p.description = newDesc;
                            Ui.ok("Plan updated.");
                        }
                    }
                    break;
                case 4:
                    int planId = input.readInt("Plan ID to delete: ");
                    if (data.subscriptions.stream().anyMatch(s -> s.planId == planId)) Ui.warn("Cannot delete: plan is used by a subscription.");
                    else if (data.plans.removeIf(x -> x.id == planId)) Ui.ok("Plan deleted.");
                    else Ui.err("Plan not found.");
                    break;
                case 0: break;
                default: Ui.err("Invalid option.");
            }
        } while (c != 0);
    }

    private void listPlans() {
        Ui.heading("Internet plans");
        if (data.plans.isEmpty()) { Ui.info("No plans available."); return; }
        List<String[]> rows = new ArrayList<String[]>();
        for (InternetPlan p : data.plans)
            rows.add(new String[] {String.valueOf(p.id), p.name, String.valueOf(p.speedMbps),
                    Ui.amount(p.monthlyPrice), p.description});
        Ui.table(new String[] {"ID", "Plan", "Mbps", "Price (Tk/month)", "Description"}, rows,
                new int[] {2, 3}, new int[] {});
    }

    private void subscriptionMenu() {
        int c;
        do {
            Ui.println();
            Ui.menu("SUBSCRIPTION MANAGEMENT", new String[][] {
                    {"1", "Create connection"}, {"2", "View connections"},
                    {"3", "Change status"}, {"0", "Back to main menu"}
            });
            c = input.readInt("Choice: ");
            switch (c) {
                case 1:
                    listCustomers(); listPlans();
                    Ui.println();
                    int customerId = input.readInt("Customer ID: ");
                    int planId = input.readInt("Plan ID: ");
                    if (findCustomer(customerId) == null || findPlan(planId) == null) {
                        Ui.err("Invalid customer or plan ID."); break;
                    }
                    if (data.subscriptions.stream().anyMatch(s -> s.customerId == customerId && s.status.equals("ACTIVE"))) {
                        Ui.warn("This customer already has an active connection."); break;
                    }
                    String date = input.readText("Start date (e.g. 2026-11-15): ");
                    data.subscriptions.add(new Subscription(data.subscriptionSeq++, customerId, planId, date, "ACTIVE"));
                    Ui.ok("Connection created (ID " + (data.subscriptionSeq - 1) + ").");
                    break;
                case 2: showSubscriptions(); break;
                case 3:
                    int sid = input.readInt("Subscription ID: ");
                    Subscription sub = findSubscription(sid);
                    if (sub == null) Ui.err("Subscription not found.");
                    else {
                        String status = input.readText("Enter ACTIVE or INACTIVE: ").toUpperCase();
                        if (!status.equals("ACTIVE") && !status.equals("INACTIVE")) Ui.err("Invalid status.");
                        else if (status.equals("ACTIVE") && hasOtherActiveConnection(sub)) Ui.warn("This customer already has another active connection.");
                        else { sub.status = status; Ui.ok("Status updated."); }
                    }
                    break;
                case 0: break;
                default: Ui.err("Invalid option.");
            }
        } while (c != 0);
    }

    private void showSubscriptions() {
        Ui.heading("Connections");
        if (data.subscriptions.isEmpty()) { Ui.info("No connections found."); return; }
        List<String[]> rows = new ArrayList<String[]>();
        for (Subscription s : data.subscriptions) {
            Customer c = findCustomer(s.customerId);
            InternetPlan p = findPlan(s.planId);
            rows.add(new String[] {String.valueOf(s.id), c == null ? "Unknown" : c.name,
                    p == null ? "Unknown" : p.name, s.startDate, s.status});
        }
        Ui.table(new String[] {"ID", "Customer", "Plan", "Start", "Status"}, rows, new int[] {}, new int[] {4});
    }

    private void billingMenu() {
        int c;
        do {
            Ui.println();
            Ui.menu("BILLING / INVOICE MANAGEMENT", new String[][] {
                    {"1", "Generate invoice"}, {"2", "View all invoices"},
                    {"3", "View unpaid invoices"}, {"0", "Back to main menu"}
            });
            c = input.readInt("Choice: ");
            switch (c) {
                case 1:
                    showSubscriptions();
                    Ui.println();
                    int sid = input.readInt("Subscription ID: ");
                    Subscription s = findSubscription(sid);
                    if (s == null || !s.status.equals("ACTIVE")) { Ui.err("Active subscription not found."); break; }
                    String month = input.readText("Billing month (e.g. November 2026): ");
                    if (data.invoices.stream().anyMatch(i -> i.subscriptionId == sid && i.billingMonth.equalsIgnoreCase(month))) {
                        Ui.warn("Invoice already exists for this subscription and month."); break;
                    }
                    InternetPlan p = findPlan(s.planId);
                    String due = input.readText("Due date (e.g. 2026-11-30): ");
                    data.invoices.add(new Invoice(data.invoiceSeq++, s.customerId, sid, month, due, p.monthlyPrice, "UNPAID"));
                    Ui.ok("Invoice generated (ID " + (data.invoiceSeq - 1) + ", " + Ui.money(p.monthlyPrice) + ").");
                    break;
                case 2: showInvoices(false); break;
                case 3: showInvoices(true); break;
                case 0: break;
                default: Ui.err("Invalid option.");
            }
        } while (c != 0);
    }

    private void showInvoices(boolean unpaidOnly) {
        Ui.heading(unpaidOnly ? "Unpaid invoices" : "All invoices");
        List<String[]> rows = new ArrayList<String[]>();
        for (Invoice i : data.invoices) {
            if (unpaidOnly && !i.status.equals("UNPAID")) continue;
            Customer c = findCustomer(i.customerId);
            rows.add(new String[] {String.valueOf(i.id), c == null ? "Unknown" : c.name, i.billingMonth,
                    Ui.amount(i.amount), i.dueDate, i.status});
        }
        if (rows.isEmpty()) { Ui.info("No matching invoices."); return; }
        Ui.table(new String[] {"ID", "Customer", "Month", "Amount (Tk)", "Due", "Status"}, rows,
                new int[] {3}, new int[] {5});
    }

    private void paymentMenu() {
        int c;
        do {
            Ui.println();
            Ui.menu("PAYMENT MANAGEMENT", new String[][] {
                    {"1", "Record payment"}, {"2", "View payments"}, {"0", "Back to main menu"}
            });
            c = input.readInt("Choice: ");
            switch (c) {
                case 1:
                    showInvoices(true);
                    Ui.println();
                    int invoiceId = input.readInt("Invoice ID: ");
                    Invoice inv = findInvoice(invoiceId);
                    if (inv == null || !inv.status.equals("UNPAID")) { Ui.err("Unpaid invoice not found."); break; }
                    String date = input.readText("Payment date: ");
                    String method = input.readText("Payment method (Cash/Mobile Banking/Bank): ");
                    double amount = input.readDouble("Amount received (" + Ui.money(inv.amount) + "): ");
                    if (amount < inv.amount) { Ui.err("Payment is less than the invoice amount. Payment not recorded."); break; }
                    data.payments.add(new Payment(data.paymentSeq++, invoiceId, inv.amount, date, method));
                    inv.status = "PAID";
                    Ui.ok("Payment recorded. Invoice marked PAID.");
                    if (amount > inv.amount) Ui.info("Change to return: " + Ui.money(amount - inv.amount));
                    break;
                case 2:
                    Ui.heading("Payments");
                    if (data.payments.isEmpty()) { Ui.info("No payments found."); break; }
                    List<String[]> rows = new ArrayList<String[]>();
                    for (Payment pay : data.payments)
                        rows.add(new String[] {String.valueOf(pay.id), String.valueOf(pay.invoiceId),
                                Ui.amount(pay.amount), pay.date, pay.method});
                    Ui.table(new String[] {"ID", "Invoice", "Amount (Tk)", "Date", "Method"}, rows,
                            new int[] {2}, new int[] {});
                    break;
                case 0: break;
                default: Ui.err("Invalid option.");
            }
        } while (c != 0);
    }

    private void supportMenu() {
        int c;
        do {
            Ui.println();
            Ui.menu("COMPLAINT / SUPPORT MANAGEMENT", new String[][] {
                    {"1", "Create ticket"}, {"2", "View tickets"},
                    {"3", "Update ticket status"}, {"0", "Back to main menu"}
            });
            c = input.readInt("Choice: ");
            switch (c) {
                case 1:
                    listCustomers();
                    Ui.println();
                    int customerId = input.readInt("Customer ID: ");
                    if (findCustomer(customerId) == null) { Ui.err("Customer not found."); break; }
                    String subject = input.readText("Subject: ");
                    String details = input.readText("Complaint details: ");
                    String date = input.readText("Date: ");
                    data.tickets.add(new SupportTicket(data.ticketSeq++, customerId, subject, details, date, "OPEN"));
                    Ui.ok("Support ticket created (ID " + (data.ticketSeq - 1) + ").");
                    break;
                case 2:
                    Ui.heading("Support tickets");
                    if (data.tickets.isEmpty()) { Ui.info("No support tickets."); break; }
                    List<String[]> rows = new ArrayList<String[]>();
                    for (SupportTicket t : data.tickets) {
                        Customer cust = findCustomer(t.customerId);
                        rows.add(new String[] {String.valueOf(t.id), cust == null ? "Unknown" : cust.name,
                                t.subject, t.date, t.status, t.details});
                    }
                    Ui.table(new String[] {"ID", "Customer", "Subject", "Date", "Status", "Details"}, rows,
                            new int[] {}, new int[] {4});
                    break;
                case 3:
                    int tid = input.readInt("Ticket ID: ");
                    SupportTicket ticket = findTicket(tid);
                    if (ticket == null) Ui.err("Ticket not found.");
                    else {
                        String status = input.readText("New status (OPEN/IN PROGRESS/RESOLVED): ").toUpperCase();
                        if (status.equals("OPEN") || status.equals("IN PROGRESS") || status.equals("RESOLVED")) {
                            ticket.status = status; Ui.ok("Ticket status updated.");
                        } else Ui.err("Invalid status.");
                    }
                    break;
                case 0: break;
                default: Ui.err("Invalid option.");
            }
        } while (c != 0);
    }

    private void reportMenu() {
        int c;
        do {
            Ui.println();
            Ui.menu("REPORTS", new String[][] {
                    {"1", "Customer report"}, {"2", "Active connections"}, {"3", "Revenue report"},
                    {"4", "Outstanding bills"}, {"5", "Ticket summary"}, {"0", "Back to main menu"}
            });
            c = input.readInt("Choice: ");
            switch (c) {
                case 1:
                    listCustomers();
                    Ui.kv("Total customers", String.valueOf(data.customers.size()));
                    break;
                case 2:
                    long active = data.subscriptions.stream().filter(s -> s.status.equals("ACTIVE")).count();
                    showSubscriptions();
                    Ui.kv("Active connections", String.valueOf(active));
                    break;
                case 3:
                    double revenue = 0;
                    for (Payment p : data.payments) revenue += p.amount;
                    double unpaid = 0;
                    for (Invoice i : data.invoices) if (i.status.equals("UNPAID")) unpaid += i.amount;
                    double total = revenue + unpaid;
                    double rate = total == 0 ? 0 : revenue / total;
                    List<String> lines = new ArrayList<String>();
                    lines.add(Ui.padRight("Collected", 13) + Ui.bar(rate, 24, "38;5;42") + "  " + Ui.money(revenue));
                    lines.add(Ui.padRight("Outstanding", 13) + Ui.bar(total == 0 ? 0 : unpaid / total, 24, "38;5;214") + "  " + Ui.money(unpaid));
                    lines.add("");
                    lines.add(Ui.dim("Collection rate: ") + Ui.bold(Math.round(rate * 100) + "%"));
                    Ui.println();
                    Ui.panel("REVENUE REPORT", lines, 66);
                    Ui.kv("Total collected revenue", Ui.money(revenue));
                    break;
                case 4:
                    showInvoices(true);
                    double outstanding = 0;
                    for (Invoice i : data.invoices) if (i.status.equals("UNPAID")) outstanding += i.amount;
                    Ui.kv("Total outstanding", Ui.money(outstanding));
                    break;
                case 5:
                    int open = 0, progress = 0, resolved = 0;
                    for (SupportTicket t : data.tickets) {
                        if (t.status.equals("RESOLVED")) resolved++;
                        else if (t.status.equals("IN PROGRESS")) progress++;
                        else open++;
                    }
                    int all = data.tickets.size();
                    List<String> rows = new ArrayList<String>();
                    rows.add(Ui.padRight("Open", 13) + Ui.bar(all == 0 ? 0 : open / (double) all, 24, "38;5;203") + "  " + open);
                    rows.add(Ui.padRight("In progress", 13) + Ui.bar(all == 0 ? 0 : progress / (double) all, 24, "38;5;214") + "  " + progress);
                    rows.add(Ui.padRight("Resolved", 13) + Ui.bar(all == 0 ? 0 : resolved / (double) all, 24, "38;5;42") + "  " + resolved);
                    Ui.println();
                    Ui.panel("TICKET SUMMARY", rows, 66);
                    Ui.kv("Total tickets", String.valueOf(all));
                    break;
                case 0: break;
                default: Ui.err("Invalid option.");
            }
        } while (c != 0);
    }

    private Customer findCustomer(int id) {
        for (Customer c : data.customers) if (c.id == id) return c;
        return null;
    }
    private InternetPlan findPlan(int id) {
        for (InternetPlan p : data.plans) if (p.id == id) return p;
        return null;
    }
    private Subscription findSubscription(int id) {
        for (Subscription s : data.subscriptions) if (s.id == id) return s;
        return null;
    }
    private Invoice findInvoice(int id) {
        for (Invoice i : data.invoices) if (i.id == id) return i;
        return null;
    }
    private SupportTicket findTicket(int id) {
        for (SupportTicket t : data.tickets) if (t.id == id) return t;
        return null;
    }
    private boolean hasSubscription(int customerId) {
        return data.subscriptions.stream().anyMatch(s -> s.customerId == customerId);
    }
    private boolean hasOtherActiveConnection(Subscription sub) {
        return data.subscriptions.stream().anyMatch(s -> s != sub && s.customerId == sub.customerId && s.status.equals("ACTIVE"));
    }
}
