import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ScientificCalculator extends JFrame implements ActionListener {
    private JTextField display;
    private String input = "";
    private boolean useDegrees = false;

    public ScientificCalculator() {
        setTitle("Scientific Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        display = new JTextField("");
        display.setFont(new Font("Arial", Font.BOLD, 32));
        display.setBackground(Color.WHITE);
        display.setForeground(new Color(30, 30, 30));
        display.setEditable(false);
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)));
        display.setMargin(new Insets(10, 10, 10, 10));

        JLabel modeLabel = new JLabel("RAD");
        modeLabel.setFont(new Font("Arial", Font.BOLD, 13));
        modeLabel.setForeground(new Color(100, 100, 100));
        modeLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel mainPanel = new JPanel(new BorderLayout(8, 8));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(display, BorderLayout.CENTER);
        topPanel.add(modeLabel, BorderLayout.SOUTH);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(8, 5, 5, 5));
        buttonPanel.setBackground(Color.WHITE);

        String[][] buttons = {
            {"sin", "cos", "tan", "ln", "log"},
            {"asin", "acos", "atan", "e^x", "10^x"},
            {"π", "e", "√", "x²", "x³"},
            {"n!", "|x|", "1/x", "^", "±"},
            {"7", "8", "9", "÷", "C"},
            {"4", "5", "6", "×", "⌫"},
            {"1", "2", "3", "-", "("},
            {"0", ".", ")", "+", "="},
        };

        for (String[] row : buttons)
            for (String label : row)
                buttonPanel.add(createButton(label, modeLabel));

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
    }

    private JButton createButton(String label, JLabel modeLabel) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Arial", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(12, 0, 12, 0)));

        Color bg = new Color(245, 245, 245);
        Color fg = new Color(30, 30, 30);

        if (label.equals("=")) { bg = new Color(60, 140, 220); fg = Color.WHITE; }
        else if (label.equals("C") || label.equals("⌫")) { bg = new Color(255, 100, 100); fg = Color.WHITE; }
        else if (label.equals("+") || label.equals("-") || label.equals("÷") || label.equals("×")) { bg = new Color(180, 200, 230); fg = new Color(30, 60, 120); }
        else if ("sin|cos|tan|asin|acos|atan|ln|log|√|e^x|10^x|π|n!|1/x|x²|x³|^".contains(label)) { bg = new Color(230, 235, 250); fg = new Color(50, 80, 150); }

        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setOpaque(true);
        btn.addActionListener(this);

        if ("|x|".equals(label)) btn.addActionListener(ev -> { useDegrees = !useDegrees; modeLabel.setText(useDegrees ? "DEG" : "RAD"); });

        return btn;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        if (cmd.equals("C")) {
            input = "";
            display.setText("");
        } else if (cmd.equals("⌫")) {
            if (!input.isEmpty()) { input = input.substring(0, input.length() - 1); display.setText(input); }
        } else if (cmd.equals("±")) {
            if (!input.isEmpty()) {
                input = input.startsWith("-") ? input.substring(1) : "-" + input;
                display.setText(input);
            }
        } else if (cmd.equals("=")) {
            calculate();
        } else if (cmd.equals("÷") || cmd.equals("×") || cmd.equals("+") || cmd.equals("-") ||
                   cmd.equals("(") || cmd.equals(")") || cmd.equals(".") || cmd.equals("^")) {
            input += cmd.equals("÷") ? "/" : cmd.equals("×") ? "*" : cmd;
            display.setText(input);
        } else if (Character.isDigit(cmd.charAt(0))) {
            input += cmd; display.setText(input);
        } else if (cmd.equals("π")) { input += "π"; display.setText(input); }
        else if (cmd.equals("e")) { input += "E"; display.setText(input); }
        else if (cmd.equals("x²")) { input += "^2"; display.setText(input); }
        else if (cmd.equals("x³")) { input += "^3"; display.setText(input); }
        else if (cmd.equals("√")) { input += "sqrt("; display.setText(input); }
        else if (cmd.equals("e^x")) { input += "exp("; display.setText(input); }
        else if (cmd.equals("10^x")) { input += "10^("; display.setText(input); }
        else if (cmd.equals("ln")) { input += "ln("; display.setText(input); }
        else if (cmd.equals("log")) { input += "log("; display.setText(input); }
        else if (cmd.equals("n!")) { input += "fact("; display.setText(input); }
        else if (cmd.equals("|x|")) { input += "abs("; display.setText(input); }
        else if (cmd.equals("1/x")) { input += "recip("; display.setText(input); }
        else { input += cmd + "("; display.setText(input); }
    }

    private void calculate() {
        try {
            double result = eval(input);
            String formatted = formatResult(result);
            display.setText(formatted);
            input = formatted;
        } catch (Exception ex) {
            display.setText("Error");
            input = "";
        }
    }

    private double eval(String expr) throws Exception {
        return parseExpr(expr, 0, expr.length()).value;
    }

    private Result parseExpr(String e, int s, int end) throws Exception {
        Result left = parseTerm(e, s, end);
        int p = left.nextPos;
        while (p < end && (e.charAt(p) == '+' || e.charAt(p) == '-')) {
            char op = e.charAt(p++);
            Result right = parseTerm(e, p, end);
            left.value = op == '+' ? left.value + right.value : left.value - right.value;
            p = right.nextPos;
        }
        left.nextPos = p; return left;
    }

    private Result parseTerm(String e, int s, int end) throws Exception {
        Result left = parseFactor(e, s, end);
        int p = left.nextPos;
        while (p < end && (e.charAt(p) == '*' || e.charAt(p) == '/')) {
            char op = e.charAt(p++);
            Result right = parseFactor(e, p, end);
            left.value = op == '*' ? left.value * right.value : left.value / right.value;
            if (op == '/' && right.value == 0) throw new ArithmeticException("Div by zero");
            p = right.nextPos;
        }
        left.nextPos = p; return left;
    }

    private Result parseFactor(String e, int s, int end) throws Exception {
        return parsePower(e, s, end);
    }

    private Result parsePower(String e, int s, int end) throws Exception {
        Result base = parseUnary(e, s, end);
        int p = base.nextPos;
        if (p < end && e.charAt(p) == '^') {
            p++;
            Result exp = parseUnary(e, p, end);
            base.value = Math.pow(base.value, exp.value);
            p = exp.nextPos;
        }
        base.nextPos = p; return base;
    }

    private Result parseUnary(String e, int s, int end) throws Exception {
        int p = s;
        if (p < end && e.charAt(p) == '-' && (p == 0 || "+-*/(^".indexOf(e.charAt(p - 1)) >= 0)) {
            p++;
            Result r = parsePrimary(e, p, end);
            r.value = -r.value; return r;
        }
        return parsePrimary(e, p, end);
    }

    private Result parsePrimary(String e, int s, int end) throws Exception {
        int p = s;
        if (p < end && e.charAt(p) == '(') {
            p++;
            Result r = parseExpr(e, p, end);
            if (r.nextPos < end && e.charAt(r.nextPos) == ')') r.nextPos++;
            return r;
        }
        String[] funcs = {"sin(", "cos(", "tan(", "asin(", "acos(", "atan(",
                          "ln(", "log(", "sqrt(", "exp(", "10^(", "fact(", "abs(", "recip("};
        for (String f : funcs) {
            if (e.startsWith(f, p)) {
                Result r = parseExpr(e, p + f.length(), end);
                if (r.nextPos < end && e.charAt(r.nextPos) == ')') r.nextPos++;
                r.value = applyFunc(f, r.value); return r;
            }
        }
        if (p < end && e.charAt(p) == 'π') return new Result(Math.PI, p + 1);
        if (p < end && e.charAt(p) == 'E') return new Result(Math.E, p + 1);
        if (p < end && (Character.isDigit(e.charAt(p)) || e.charAt(p) == '.')) {
            int ns = p;
            while (p < end && (Character.isDigit(e.charAt(p)) || e.charAt(p) == '.')) p++;
            return new Result(Double.parseDouble(e.substring(ns, p)), p);
        }
        throw new IllegalArgumentException("Unexpected char at " + p);
    }

    private double applyFunc(String f, double v) throws ArithmeticException {
        switch (f) {
            case "sin(":  return useDegrees ? Math.sin(Math.toRadians(v)) : Math.sin(v);
            case "cos(":  return useDegrees ? Math.cos(Math.toRadians(v)) : Math.cos(v);
            case "tan(":  return useDegrees ? Math.tan(Math.toRadians(v)) : Math.tan(v);
            case "asin(": { if (v < -1 || v > 1) throw new ArithmeticException("Out of range");
                          double res = Math.asin(v); return useDegrees ? Math.toDegrees(res) : res; }
            case "acos(": { if (v < -1 || v > 1) throw new ArithmeticException("Out of range");
                          double res = Math.acos(v); return useDegrees ? Math.toDegrees(res) : res; }
            case "atan(": { double res = Math.atan(v); return useDegrees ? Math.toDegrees(res) : res; }
            case "ln(":   if (v <= 0) throw new ArithmeticException("Must be > 0");
                          return Math.log(v);
            case "log(":  if (v <= 0) throw new ArithmeticException("Must be > 0");
                          return Math.log10(v);
            case "sqrt(": if (v < 0) throw new ArithmeticException("Must be >= 0");
                          return Math.sqrt(v);
            case "exp(":  return Math.exp(v);
            case "10^(":  return Math.pow(10, v);
            case "fact(": if (v < 0 || v != Math.floor(v)) throw new ArithmeticException("Need non-neg int");
                          if (v > 170) throw new ArithmeticException("Overflow");
                          long res = 1; for (long i = 2; i <= (long)v; i++) res *= i; return (double)res;
            case "abs(":  return Math.abs(v);
            case "recip(":if (v == 0) throw new ArithmeticException("Div by zero");
                          return 1.0 / v;
            default: throw new IllegalArgumentException("Unknown: " + f);
        }
    }

    private String formatResult(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) return "Error";
        if (Math.abs(v - Math.round(v)) < 1e-10 && Math.abs(v) < 1e15)
            return String.valueOf(Math.round(v));
        if (Math.abs(v) >= 1e-4 && Math.abs(v) < 1e15) {
            String s = String.format("%.10f", v);
            if (s.contains(".")) s = s.replaceAll("0+$", "").replaceAll("\\.$", "");
            return s;
        }
        return String.format("%.6e", v);
    }

    private static class Result {
        double value; int nextPos;
        Result(double v, int p) { value = v; nextPos = p; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ScientificCalculator().setVisible(true);
        });
    }
}
