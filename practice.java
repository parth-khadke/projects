import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class practice {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Numerology Calculator");
            frame.setSize(450, 400);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setLayout(new GridBagLayout());
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;

            // Title Label
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 3;
            JLabel titleLabel = new JLabel("Numerology Calculator");
            titleLabel.setFont(new Font("Serif", Font.BOLD, 22));
            frame.add(titleLabel, gbc);

            // Enter DOB Label
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 1;
            JLabel dobLabel = new JLabel("Enter DOB:");
            dobLabel.setFont(new Font("Serif", Font.PLAIN, 16));
            frame.add(dobLabel, gbc);

            // Day Field
            gbc.gridx = 1;
            gbc.gridy = 1;
            JTextField dayField = new JTextField(2);
            frame.add(dayField, gbc);

            // Month Field
            gbc.gridx = 2;
            gbc.gridy = 1;
            JTextField monthField = new JTextField(2);
            frame.add(monthField, gbc);

            // Year Field
            gbc.gridx = 3;
            gbc.gridy = 1;
            JTextField yearField = new JTextField(4);
            frame.add(yearField, gbc);

            // Name Fields
            gbc.gridx = 0;
            gbc.gridy = 2;
            JLabel nameLabel = new JLabel("Enter Name:");
            nameLabel.setFont(new Font("Serif", Font.PLAIN, 16));
            frame.add(nameLabel, gbc);

            gbc.gridx = 1;
            gbc.gridy = 2;
            gbc.gridwidth = 3;
            JTextField nameField = new JTextField(15);
            frame.add(nameField, gbc);

            // Calculate Button
            gbc.gridx = 1;
            gbc.gridy = 3;
            gbc.gridwidth = 2;
            JButton calculateButton = new JButton("Calculate");
            frame.add(calculateButton, gbc);

            // Result Area
            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.gridwidth = 4;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            JTextArea resultArea = new JTextArea(5, 30);
            resultArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            resultArea.setEditable(false);
            frame.add(resultArea, gbc);

            // ActionListener for Calculate Button
            calculateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        int day = Integer.parseInt(dayField.getText());
                        int month = Integer.parseInt(monthField.getText());
                        int year = Integer.parseInt(yearField.getText());
                        String name = nameField.getText();
                        
                        NumerologyCalculator calculator = new NumerologyCalculator();
                        int psychicNumber = calculator.calculate(day);
                        int destinyNumber = calculator.calculate(day) + calculator.calculate(month) + calculator.calculate(year);
                        destinyNumber = calculator.calculate(destinyNumber);
                        int nameNumber = calculator.NameCalc(name);

                        // Display results
                        resultArea.setText("Psychic Number: " + psychicNumber + "\n" +
                                           "Destiny Number: " + destinyNumber + "\n" +
                                           "Name Number: " + nameNumber);
                    } catch (NumberFormatException ex) {
                        resultArea.setText("Please enter valid numerical values for DOB and name.");
                    }
                }
            });
            
            frame.setVisible(true);
        });
    }
}

class NumerologyCalculator {
    public int calculate(int num) {
        while (num > 9) {
            int sum = 0;
            while (num > 0) {
                sum += num % 10;
                num /= 10;
            }
            num = sum;
        }
        return num;
    }

    public int NameCalc(String name) {
        int nameNum = 0;
        String lowerName = name.toLowerCase().replace(" ", "");
        
        for (char ch : lowerName.toCharArray()) {
            if ("aijqyz".indexOf(ch) != -1) nameNum += 1;
            else if ("bckr".indexOf(ch) != -1) nameNum += 2;
            else if ("gls".indexOf(ch) != -1) nameNum += 3;
            else if ("dmt".indexOf(ch) != -1) nameNum += 4;
            else if ("en".indexOf(ch) != -1) nameNum += 5;
            else if ("uvwxy".indexOf(ch) != -1) nameNum += 6;
            else if ("oz".indexOf(ch) != -1) nameNum += 7;
            else if ("fhp".indexOf(ch) != -1) nameNum += 8;
        }
        return calculate(nameNum);
    }
}
