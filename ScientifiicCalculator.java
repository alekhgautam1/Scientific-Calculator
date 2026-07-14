package ScientificCalculator;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ScientifiicCalculator extends JFrame implements ActionListener {
    JTextField display = new JTextField("0");
    String op = "";
    double a = 0;
    boolean newInput = true;
    boolean degMode = true;

    String[] keys = {
        "C","⌫","DEG","sin","cos","tan",
        "ln","log","√","x²","x³","1/x",
        "7","8","9","/","(",")",
        "4","5","6","*","^","%",
        "1","2","3","-","+","=",
        "0",".","π","e"
    };

    public ScientifiicCalculator() {
        setTitle("Scientific Calculator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 560);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        display.setFont(new Font("Segoe UI", Font.BOLD, 28));
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setEditable(false);
        add(display, BorderLayout.NORTH);

        JPanel p = new JPanel(new GridLayout(0, 6, 5, 5));
        for (String k : keys) {
            JButton b = new JButton(k);
            b.setFont(new Font("Segoe UI", Font.BOLD, 16));
            b.addActionListener(this);
            p.add(b);
        }
        add(p, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent e) {
        String c = e.getActionCommand();

        if (c.matches("\\d") || c.equals(".")) {
            if (newInput || display.getText().equals("0")) {
                display.setText(c.equals(".") ? "0." : c);
                newInput = false;
            } else if (!(c.equals(".") && display.getText().contains("."))) {
                display.setText(display.getText() + c);
            }
            return;
        }

        if (c.equals("C")) {
            display.setText("0");
            a = 0;
            op = "";
            newInput = true;
            return;
        }

        if (c.equals("⌫")) {
            String t = display.getText();
            display.setText(t.length() <= 1 ? "0" : t.substring(0, t.length() - 1));
            return;
        }

        if (c.equals("DEG")) {
            degMode = !degMode;
            return;
        }

        if (c.equals("π")) { display.setText(String.valueOf(Math.PI)); newInput = false; return; }
        if (c.equals("e")) { display.setText(String.valueOf(Math.E)); newInput = false; return; }

        if (c.equals("=")) {
            calculate();
            return;
        }

        if (c.equals("sin") || c.equals("cos") || c.equals("tan") ||
            c.equals("ln") || c.equals("log") || c.equals("√") ||
            c.equals("x²") || c.equals("x³") || c.equals("1/x")) {
            applyUnary(c);
            return;
        }

        if (c.equals("+") || c.equals("-") || c.equals("*") || c.equals("/") || c.equals("^") || c.equals("%")) {
            a = Double.parseDouble(display.getText());
            op = c;
            newInput = true;
        }
    }

    void applyUnary(String f) {
        try {
            double x = Double.parseDouble(display.getText());
            double r = 0;

            switch (f) {
                case "sin": r = Math.sin(degMode ? Math.toRadians(x) : x); break;
                case "cos": r = Math.cos(degMode ? Math.toRadians(x) : x); break;
                case "tan": r = Math.tan(degMode ? Math.toRadians(x) : x); break;
                case "ln":  r = Math.log(x); break;
                case "log": r = Math.log10(x); break;
                case "√":   r = Math.sqrt(x); break;
                case "x²":  r = x * x; break;
                case "x³":  r = x * x * x; break;
                case "1/x": r = 1.0 / x; break;
            }
            setResult(r);
        } catch (Exception ex) {
            display.setText("Error");
            newInput = true;
        }
    }

    void calculate() {
        try {
            double b = Double.parseDouble(display.getText());
            double r = 0;

            switch (op) {
                case "+": r = a + b; break;
                case "-": r = a - b; break;
                case "*": r = a * b; break;
                case "/": r = b == 0 ? Double.NaN : a / b; break;
                case "^": r = Math.pow(a, b); break;
                case "%": r = a % b; break;
                default: return;
            }
            setResult(r);
        } catch (Exception ex) {
            display.setText("Error");
            newInput = true;
        }
    }

    void setResult(double r) {
        if (Double.isNaN(r) || Double.isInfinite(r)) {
            display.setText("Error");
        } else if (Math.abs(r - Math.rint(r)) < 1e-10) {
            display.setText(String.valueOf((long) Math.rint(r)));
        } else {
            display.setText(String.valueOf(r));
        }
        newInput = true;
        op = "";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ScientificCalculator().setVisible(true));
    }
}