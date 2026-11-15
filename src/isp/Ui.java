package isp;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

// Everything that is printed to the screen goes through this class: colours, boxes,
// tables, bars and the start-up effect. If the terminal can't show colours or Unicode,
// it falls back to a plain ASCII layout. Special characters are written as unicode
// escapes so the file compiles the same way on every system.
public final class Ui {
    private Ui() { }

    static final int WIDTH = 78;

    // colours and box drawing on, or plain ASCII
    static boolean fancy;
    // typing and loading effect at start-up
    static boolean animate;
    // show typed text when input is piped (used for the screenshots)
    static boolean echoInput;
    static PrintStream out = System.out;

    private static final String ESC = "\u001B[";
    private static final String RESET = ESC + "0m";
    private static final Pattern ANSI = Pattern.compile("\u001B\\[[0-9;]*m");

    // box characters, set in applyChars()
    private static String H = "-", V = "|", TL = "+", TR = "+", BL = "+", BR = "+";
    private static String LT = "+", RT = "+", TT = "+", BT = "+", XX = "+";

    private static final String[] LOGO_FANCY = {
            "\u2588\u2588\u2557\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2557\u2588\u2588\u2588\u2588\u2588\u2588\u2557 ",
            "\u2588\u2588\u2551\u2588\u2588\u2554\u2550\u2550\u2550\u2550\u255d\u2588\u2588\u2554\u2550\u2550\u2588\u2588\u2557",
            "\u2588\u2588\u2551\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2557\u2588\u2588\u2588\u2588\u2588\u2588\u2554\u255d",
            "\u2588\u2588\u2551\u255a\u2550\u2550\u2550\u2550\u2588\u2588\u2551\u2588\u2588\u2554\u2550\u2550\u2550\u255d ",
            "\u2588\u2588\u2551\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2551\u2588\u2588\u2551     ",
            "\u255a\u2550\u255d\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u255d\u255a\u2550\u255d     "
    };
    private static final String[] LOGO_PLAIN = {
            " ___ ____  ____  ",
            "|_ _/ ___||  _ \\ ",
            " | |\\___ \\| |_) |",
            " | | ___) |  __/ ",
            "|___|____/|_|    "
    };

    static void init(String[] args) {
        boolean forceFancy = false, forcePlain = false, noAnim = false;
        for (String a : args) {
            if ("--fancy".equals(a)) forceFancy = true;
            else if ("--plain".equals(a)) forcePlain = true;
            else if ("--no-anim".equals(a)) noAnim = true;
            else if ("--echo".equals(a)) echoInput = true;
        }
        String env = System.getenv("ISP_UI");
        if ("plain".equalsIgnoreCase(env)) forcePlain = true;
        else if ("fancy".equalsIgnoreCase(env)) forceFancy = true;

        if (forcePlain) fancy = false;
        else if (forceFancy) fancy = true;
        else fancy = detectFancy();

        if (fancy) {
            try {
                out = new PrintStream(new FileOutputStream(FileDescriptor.out), true, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                out = System.out;
            }
        }
        animate = fancy && !noAnim && !"off".equalsIgnoreCase(System.getenv("ISP_ANIM"))
                && System.console() != null;
        applyChars();
    }

    private static boolean detectFancy() {
        if (System.getenv("NO_COLOR") != null) return false;
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (os.contains("win")) {
            boolean modern = System.getenv("WT_SESSION") != null
                    || "ON".equalsIgnoreCase(System.getenv("ConEmuANSI"))
                    || System.getenv("ANSICON") != null
                    || "vscode".equalsIgnoreCase(System.getenv("TERM_PROGRAM"));
            return modern && utf8Console();
        }
        String term = System.getenv("TERM");
        return System.console() != null && term != null && !"dumb".equals(term) && utf8Console();
    }

    private static boolean utf8Console() {
        if ("1".equals(System.getenv("ISP_UTF8"))) return true;
        String enc = System.getProperty("stdout.encoding");
        if (enc == null) enc = System.getProperty("sun.stdout.encoding");
        if (enc == null) enc = Charset.defaultCharset().name();
        return enc.toUpperCase(Locale.ROOT).replace("-", "").equals("UTF8");
    }

    private static void applyChars() {
        if (fancy) {
            H = "\u2500"; V = "\u2502"; TL = "\u250C"; TR = "\u2510"; BL = "\u2514"; BR = "\u2518";
            LT = "\u251C"; RT = "\u2524"; TT = "\u252C"; BT = "\u2534"; XX = "\u253C";
        } else {
            H = "-"; V = "|"; TL = "+"; TR = "+"; BL = "+"; BR = "+";
            LT = "+"; RT = "+"; TT = "+"; BT = "+"; XX = "+";
        }
    }

    static String paint(String codes, String text) {
        return fancy ? ESC + codes + "m" + text + RESET : text;
    }
    static String bold(String t)   { return paint("1", t); }
    static String dim(String t)    { return paint("38;5;245", t); }
    static String accent(String t) { return paint("1;38;5;45", t); }
    static String yellow(String t) { return paint("1;38;5;214", t); }
    private static String bc(String t) { return paint("38;5;39", t); }   // panel border
    private static String tc(String t) { return paint("38;5;244", t); }  // table border

    static int vlen(String s) {
        return ANSI.matcher(s).replaceAll("").length();
    }

    static String rep(String s, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(s);
        return sb.toString();
    }

    static String padRight(String s, int w) {
        int missing = w - vlen(s);
        return missing > 0 ? s + rep(" ", missing) : s;
    }

    static String padLeft(String s, int w) {
        int missing = w - vlen(s);
        return missing > 0 ? rep(" ", missing) + s : s;
    }

    static String truncate(String s, int w) {
        if (s.length() <= w) return s;
        if (w <= 1) return s.substring(0, Math.max(w, 0));
        return s.substring(0, w - 1) + (fancy ? "\u2026" : "~");
    }

    private static String fit(String s, int w) {
        if (vlen(s) <= w) return s;
        return truncate(ANSI.matcher(s).replaceAll(""), w);
    }

    static String money(double value) {
        return "Tk " + String.format(Locale.US, "%,.2f", value);
    }

    static String amount(double value) {
        return String.format(Locale.US, "%,.2f", value);
    }

    static void println() { out.println(); }

    static void clear() {
        if (fancy) {
            out.print(ESC + "2J" + ESC + "H");
            out.flush();
        }
    }

    static String prompt(String text) {
        return "  " + accent(">") + " " + text;
    }

    static void echo(String typed) {
        out.println(bold(typed));
    }

    static void sleep(int ms) {
        if (!animate) return;
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void typewriter(String text, String code, int msPerChar) {
        if (!animate) {
            out.println(paint(code, text));
            return;
        }
        out.print(ESC + code + "m");
        for (int i = 0; i < text.length(); i++) {
            out.print(text.charAt(i));
            out.flush();
            sleep(msPerChar);
        }
        out.println(RESET);
    }

    private static void tag(String label, String code, String msg) {
        String t = fancy ? ESC + code + "m " + label + " " + RESET : "[" + label + "]";
        out.println("  " + t + " " + msg);
    }
    static void ok(String m)   { tag("OK", "30;48;5;42", m); }
    static void err(String m)  { tag("ERROR", "30;48;5;203", m); }
    static void warn(String m) { tag("WARN", "30;48;5;214", m); }
    static void info(String m) { tag("INFO", "30;48;5;39", m); }

    // status text for tables: coloured text in fancy mode, [TEXT] in plain mode
    static String badge(String status) {
        String s = status.toUpperCase(Locale.ROOT);
        String code;
        if (s.equals("ACTIVE") || s.equals("PAID") || s.equals("RESOLVED")) code = "1;38;5;42";
        else if (s.equals("UNPAID") || s.equals("OPEN")) code = "1;38;5;203";
        else if (s.equals("IN PROGRESS")) code = "1;38;5;214";
        else if (s.equals("INACTIVE")) code = "38;5;245";
        else code = "1;38;5;39";
        return fancy ? paint(code, status) + "  " : "[" + status + "]";
    }

    static void heading(String title) {
        out.println();
        out.println("  " + accent(title));
    }

    static void kv(String label, String value) {
        out.println("  " + dim(label + ": ") + bold(value));
    }

    static void panel(String title, List<String> lines, int width) {
        int inner = width - 2;
        String head;
        if (title == null || title.isEmpty()) {
            head = bc(TL + rep(H, inner) + TR);
        } else {
            String t = truncate(title, inner - 4);
            int rest = inner - (t.length() + 3);
            head = bc(TL + H) + " " + accent(t) + " " + bc(rep(H, Math.max(rest, 0)) + TR);
        }
        out.println("  " + head);
        for (String line : lines) {
            out.println("  " + bc(V) + " " + padRight(fit(line, inner - 2), inner - 2) + " " + bc(V));
        }
        out.println("  " + bc(BL + rep(H, inner) + BR));
    }

    static void menu(String title, String[][] items) {
        List<String> lines = new ArrayList<String>();
        for (String[] it : items) {
            String key = "[" + it[0] + "]";
            lines.add(" " + ("0".equals(it[0]) ? yellow(key) : accent(key)) + "  " + it[1]);
        }
        panel(title, lines, 52);
    }

    static void record(String title, String[][] kv) {
        int kw = 0;
        for (String[] p : kv) kw = Math.max(kw, p[0].length());
        List<String> lines = new ArrayList<String>();
        for (String[] p : kv) lines.add(padRight(dim(p[0]), kw) + "   " + p[1]);
        panel(title, lines, 64);
    }

    static void topBar(String left, String right) {
        int gap = WIDTH - 2 - vlen(left) - vlen(right);
        out.println();
        out.println("  " + bold(left) + rep(" ", Math.max(gap, 1)) + dim(right));
        out.println("  " + tc(rep(H, WIDTH - 2)));
    }

    // items are {label, value, colour}; the cards are drawn side by side
    static void cards(String[][] items) {
        int n = items.length;
        int cw = (WIDTH - 2 - (n - 1)) / n;
        StringBuilder top = new StringBuilder("  "), l1 = new StringBuilder("  "),
                l2 = new StringBuilder("  "), bottom = new StringBuilder("  ");
        for (int i = 0; i < n; i++) {
            String sep = i == 0 ? "" : " ";
            top.append(sep).append(bc(TL + rep(H, cw - 2) + TR));
            l1.append(sep).append(bc(V)).append(" ").append(padRight(dim(items[i][0]), cw - 4)).append(" ").append(bc(V));
            l2.append(sep).append(bc(V)).append(" ").append(padRight(paint(items[i][2], items[i][1]), cw - 4)).append(" ").append(bc(V));
            bottom.append(sep).append(bc(BL + rep(H, cw - 2) + BR));
        }
        out.println(top);
        out.println(l1);
        out.println(l2);
        out.println(bottom);
    }

    static String bar(double fraction, int width, String colourCode) {
        double f = Math.max(0, Math.min(1, fraction));
        int filled = (int) Math.round(f * width);
        String on = rep(fancy ? "\u2588" : "#", filled);
        String off = rep(fancy ? "\u2591" : "-", width - filled);
        if (!fancy) return "[" + on + off + "]";
        return ESC + colourCode + "m" + on + RESET + ESC + "38;5;240m" + off + RESET;
    }

    private static int maxTableWidth() {
        int limit = 98;
        String cols = System.getenv("COLUMNS");
        if (cols != null) {
            try {
                limit = Math.max(60, Math.min(Integer.parseInt(cols.trim()) - 2, 140));
            } catch (NumberFormatException ignored) {
                // keep default
            }
        }
        return limit;
    }

    private static int tableWidth(int[] w) {
        int sum = 0;
        for (int x : w) sum += x;
        return sum + 3 * (w.length - 1) + 4;
    }

    static void table(String[] headers, List<String[]> rows, int[] rightCols, int[] badgeCols) {
        int n = headers.length;
        boolean[] right = new boolean[n];
        boolean[] badge = new boolean[n];
        for (int c : rightCols) right[c] = true;
        for (int c : badgeCols) badge[c] = true;

        int[] w = new int[n];
        for (int i = 0; i < n; i++) {
            w[i] = headers[i].length();
            for (String[] r : rows) w[i] = Math.max(w[i], r[i].length() + (badge[i] ? 2 : 0));
        }
        int limit = maxTableWidth();
        while (tableWidth(w) > limit) {
            int idx = -1;
            for (int i = 0; i < n; i++) {
                if (!badge[i] && w[i] > Math.max(8, headers[i].length()) && (idx < 0 || w[i] > w[idx])) idx = i;
            }
            if (idx < 0) break;
            w[idx]--;
        }

        StringBuilder top = new StringBuilder(tc(TL)), mid = new StringBuilder(tc(LT)), bottom = new StringBuilder(tc(BL));
        for (int i = 0; i < n; i++) {
            boolean last = i == n - 1;
            top.append(tc(rep(H, w[i] + 2) + (last ? TR : TT)));
            mid.append(tc(rep(H, w[i] + 2) + (last ? RT : XX)));
            bottom.append(tc(rep(H, w[i] + 2) + (last ? BR : BT)));
        }
        StringBuilder head = new StringBuilder(tc(V));
        for (int i = 0; i < n; i++) {
            String h = padRight(accent(truncate(headers[i], w[i])), w[i]);
            if (right[i]) h = padLeft(accent(truncate(headers[i], w[i])), w[i]);
            head.append(" ").append(h).append(" ").append(tc(V));
        }

        out.println("  " + top);
        out.println("  " + head);
        out.println("  " + mid);
        for (String[] r : rows) {
            StringBuilder line = new StringBuilder(tc(V));
            for (int i = 0; i < n; i++) {
                String cell;
                if (badge[i]) cell = padRight(badge(r[i]), w[i]);
                else if (right[i]) cell = padLeft(truncate(r[i], w[i]), w[i]);
                else cell = padRight(truncate(r[i], w[i]), w[i]);
                line.append(" ").append(cell).append(" ").append(tc(V));
            }
            out.println("  " + line);
        }
        out.println("  " + bottom);
        out.println("  " + dim(rows.size() + (rows.size() == 1 ? " record" : " records")));
    }

    static void splash() {
        clear();
        out.println();
        String[] logo = fancy ? LOGO_FANCY : LOGO_PLAIN;
        int[] gradient = {51, 45, 39, 33, 63, 99};
        for (int i = 0; i < logo.length; i++) {
            out.println("   " + paint("1;38;5;" + gradient[i % gradient.length], logo[i]));
            sleep(70);
        }
        out.println();
        out.println("   " + bold("INTERNET SERVICE PROVIDER MANAGEMENT SYSTEM"));
        typewriter("   Reliable Connection. Better Tomorrow.", "38;5;45", 14);
        out.println();
        loadingBar();
    }

    private static void loadingBar() {
        if (!animate) return;
        String[] steps = {"customers", "plans", "connections", "billing", "payments", "support", "reports", "storage"};
        for (int i = 0; i < steps.length; i++) {
            int pct = (i + 1) * 100 / steps.length;
            out.print("\r" + ESC + "2K   " + bar((i + 1) / (double) steps.length, 26, "38;5;45")
                    + "  " + padLeft(pct + "%", 4) + "  " + dim("Loading " + steps[i] + "..."));
            out.flush();
            sleep(120);
        }
        out.print("\r" + ESC + "2K");
        out.flush();
    }

    static void goodbye() {
        ok("Data saved.");
        out.println("  " + accent("Thank you for using the ISP Management System. Goodbye!"));
        out.println();
    }
}
