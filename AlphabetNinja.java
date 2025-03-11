import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class AlphabetNinja extends JFrame {
    // Game constants
    private static final int WIDTH = 1366;
    private static final int HEIGHT = 768;
    private static final int LETTER_SIZE = 50;
    private static final int WORD_SIZE = 35;
    private static final int SPAWN_DELAY = 1850; // milliseconds between spawns
    
    // Words for the game
    private static final String[] WORDS = {
        "NINJA", "SWING", "JAVA", "CODE", "GAME", "LEVEL", "TYPE", "FAST", 
        "SCORE",  "WORD",  "SKILL"
    };
    
    // Game variables
    private boolean gameRunning = false;
    private int score = 0;
    private int wrongAttempts = 0;
    private final int maxWrongAttempts = 3;
    private ArrayList<GameItem> gameItems = new ArrayList<>();
    private ArrayList<HitEffect> hitEffects = new ArrayList<>();
    private Random random = new Random();
    private Timer spawnTimer;
    private Timer gameTimer;
    private StringBuilder currentInput = new StringBuilder();
    private String warningMessage = "";
    private int warningTimer = 0;
    
    // Game panel
    private GamePanel gamePanel;
    
    public AlphabetNinja() {
        // Set up the window
        super("AlphabetNinja - Level 1");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Set up the icon
        ImageIcon icon = new ImageIcon("images/icon.png");
        setIconImage(icon.getImage());

        


        // Set up the game panel
        gamePanel = new GamePanel();
        add(gamePanel);
        
        // Set up key listener
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (gameRunning) {
                    handleKeyInput(e.getKeyChar());
                } else {
                    startGame();
                }
            }
            
            @Override
            public void keyPressed(KeyEvent e) {
                // Handle backspace for word typing
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && gameRunning) {
                    if (currentInput.length() > 0) {
                        currentInput.deleteCharAt(currentInput.length() - 1);
                    }
                }
                // Handle Enter key for word submission
                if (e.getKeyCode() == KeyEvent.VK_ENTER && gameRunning) {
                    checkWordInput();
                }
            }
        });
        
        if (JOptionPane.showOptionDialog(this, 
    "<html><b><big>Welcome to AlphabetNinja - Level 1!</big></b><br>" +
    "<h3>Type the letters and complete words to score points.</h3>" +
    "<h3>For words, type the entire word and press 'ENTER'.</h3>" +
    "<h3>If you miss a letter or mistype a word, you lose!</h3><br>" +
    "<h3>Press any key to start.</h3></html>",
    "Alphabet Ninja",
    JOptionPane.DEFAULT_OPTION,
    JOptionPane.INFORMATION_MESSAGE,
    new ImageIcon(icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)),
    new Object[] {"OK"},
    "OK") == JOptionPane.CLOSED_OPTION) {
    System.exit(0);
}

        // Display the window
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }
    
    private void startGame() {
        // Reset game variables
        score = 0;
        wrongAttempts = 0;
        gameItems.clear();
        hitEffects.clear();
        currentInput.setLength(0);
        warningMessage = "";
        warningTimer = 0;
        gameRunning = true;
        
        // Set up spawn timer
        spawnTimer = new Timer(SPAWN_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                spawnItem();
            }
        });
        spawnTimer.start();
        
        // Set up game timer (for animation)
        gameTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateGame();
                gamePanel.repaint();
            }
        });
        gameTimer.start();
    }
    
    private void spawnItem() {
        // Randomize x position
        int x = random.nextInt(WIDTH - 150);
        
        // Adjusted initial velocity for a more controlled arc
        double initialVelocity = 11.0 + random.nextDouble() * 3.0;
        
        // 30% chance to spawn a word, 70% chance to spawn a letter
        if (random.nextDouble() < 0.3) {
            // Choose a random word
            String word = WORDS[random.nextInt(WORDS.length)];
            gameItems.add(new WordItem(word, x, HEIGHT + 20, initialVelocity));
        } else {
            // Generate a random letter (A-Z)
            char letter = (char)('A' + random.nextInt(26));
            gameItems.add(new LetterItem(letter, x, HEIGHT + 20, initialVelocity));
        }
    }
    
    private void updateGame() {
        // Move each item
        for (int i = 0; i < gameItems.size(); i++) {
            GameItem item = gameItems.get(i);
            item.update();
            
            // Check if item went off-screen (bottom)
            if (item.y > HEIGHT + 50) {
                // Game over - item missed
                endGame();
                return;
            }
        }
        
        // Update hit effects
        for (int i = hitEffects.size() - 1; i >= 0; i--) {
            HitEffect effect = hitEffects.get(i);
            effect.update();
            if (effect.isDead()) {
                hitEffects.remove(i);
            }
        }
        
        // Update warning message timer
        if (warningTimer > 0) {
            warningTimer--;
            if (warningTimer == 0) {
                warningMessage = "";
            }
        }
    }
    
    private void handleKeyInput(char typed) {
        // Convert typed character to uppercase
        char upperTyped = Character.toUpperCase(typed);
        
        // Ignore Enter key in keyTyped (we handle it in keyPressed)
        if (typed == '\n' || typed == '\r') {
            return;
        }
        
        // Check for single letter items
        for (int i = 0; i < gameItems.size(); i++) {
            if (gameItems.get(i) instanceof LetterItem) {
                LetterItem letterItem = (LetterItem) gameItems.get(i);
                if (letterItem.character == upperTyped) {
                    // Letter hit!
                    addHitEffect(letterItem.x, letterItem.y);
                    gameItems.remove(i);
                    score++;
                    return;
                }
            }
        }
        
        // If it's not a matching single letter, add it to current input for word matching
        currentInput.append(upperTyped);
    }
    
    private void checkWordInput() {
        if (currentInput.length() == 0) {
            return;
        }
        
        String typedWord = currentInput.toString().toUpperCase();
        boolean found = false;
        
        // Check all active word items
        for (int i = 0; i < gameItems.size(); i++) {
            if (gameItems.get(i) instanceof WordItem) {
                WordItem wordItem = (WordItem) gameItems.get(i);
                if (wordItem.word.equalsIgnoreCase(typedWord)) {
                    // Word hit!
                    addHitEffect(wordItem.x, wordItem.y);
                    gameItems.remove(i);
                    score++;
                    found = true;
                    break;
                }
            }
        }
        
        // Reset current input
        currentInput.setLength(0);
        
        // If word not found, it's a wrong input
        if (!found) {
            // Instead of ending game immediately, penalize the player
            wrongAttempts++;
            if (wrongAttempts >= maxWrongAttempts) {
                endGame();
            } else {
                showWarning("Wrong word! " + (maxWrongAttempts - wrongAttempts) + " attempts left");
            }
        }
    }
    
    private void addHitEffect(double x, double y) {
        hitEffects.add(new HitEffect(x, y));
    }
    
    private void showWarning(String message) {
        warningMessage = message;
        warningTimer = 120; // Show for about 2 seconds (120 frames at 60fps)
    }
    
    private void endGame() {
        gameRunning = false;
        spawnTimer.stop();
        gameTimer.stop();
        
        JOptionPane.showMessageDialog(this, 
            "Game Over!\nYour score: " + score + "\n\nPress any key to play again.");
    }
    
    // Abstract class for game items (letters and words)
    private abstract class GameItem {
        double x;
        double y;
        Color color;
        
        // Physics variables
        double velocityY;
        double gravity = 0.20;  // Slightly increased gravity to limit height
        double initialVelocityY; // Store initial velocity for height calculation
        double maxHeight;       // Maximum height the item should reach
        
        public GameItem(int x, int y, double initialVelocity) {
            this.x = x;
            this.y = y;
            this.initialVelocityY = initialVelocity;
            this.velocityY = -initialVelocity;  // Negative for upward movement
            
            // Calculate approximate max height: y_max = y_initial - (v_initial^2)/(2*gravity)
            // But ensure it stays in view (doesn't go above 50px from top)
            this.maxHeight = Math.max(50, y - (initialVelocity * initialVelocity) / (2 * gravity));
            
            // Random color
            int r = random.nextInt(200) + 55;
            int g = random.nextInt(200) + 55;
            int b = random.nextInt(200) + 55;
            this.color = new Color(r, g, b);
        }
        
        public void update() {
            // Apply gravity to velocity
            velocityY += gravity;
            
            // Calculate next position
            double nextY = y + velocityY;
            
            // Make sure item doesn't go too high (stays in view)
            if (nextY < maxHeight) {
                // If would go too high, reverse direction at maxHeight
                y = maxHeight;
                velocityY = Math.abs(velocityY) * 0.7; // Bounce with some energy loss
            } else {
                // Regular movement
                y = nextY;
            }
        }
        
        public abstract void draw(Graphics g);
    }
    
    // Class for single letters
    private class LetterItem extends GameItem {
        char character;
        
        public LetterItem(char character, int x, int y, double initialVelocity) {
            super(x, y, initialVelocity);
            this.character = character;
        }
        
        @Override
        public void draw(Graphics g) {
            g.setColor(color);
            g.setFont(new Font("Arial", Font.BOLD, LETTER_SIZE));
            g.drawString(String.valueOf(character), (int)x, (int)y);
        }
    }
    
    // Class for words
    private class WordItem extends GameItem {
        String word;
        
        public WordItem(String word, int x, int y, double initialVelocity) {
            super(x, y, initialVelocity + 2.0); // Increase initial velocity by 2.0 for word items
            this.word = word;
        }
        
        @Override
        public void draw(Graphics g) {
            g.setColor(color);
            g.setFont(new Font("Arial", Font.BOLD, WORD_SIZE));
            g.drawString(word, (int)x, (int)y);
            
            // Debug - draw a small rectangle to show hitbox (uncomment for debugging)
            // g.setColor(new Color(255, 0, 0, 100)); // Semi-transparent red
            // FontMetrics metrics = g.getFontMetrics();
            // int width = metrics.stringWidth(word);
            // g.drawRect((int)x, (int)y - metrics.getAscent(), width, metrics.getHeight());
        }
    }
    
    // Class for visual hit effects
    private class HitEffect {
        double x, y;
        int lifetime = 20; // frames
        
        public HitEffect(double x, double y) {
            this.x = x;
            this.y = y;
        }
        
        public void update() {
            lifetime--;
        }
        
        public void draw(Graphics g) {
            int size = 50 + (20 - lifetime) * 3;
            int alpha = (int)(255 * (lifetime / 20.0));
            g.setColor(new Color(255, 0, 0, alpha));
            g.drawOval((int)x - size/2, (int)y - size/2, size, size);
        }
        
        public boolean isDead() {
            return lifetime <= 0;
        }
    }
    
    // Inner class for game rendering
    private class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            // Draw background
            g.setColor(Color.BLACK);
            // Load and draw background image
            Image backgroundImage = new ImageIcon("D:/Project/images1.jpg").getImage();
            g.drawImage(backgroundImage, 0, 0, WIDTH, HEIGHT, this);
            
            if (gameRunning) {
                // Draw all game items
                for (GameItem item : gameItems) {
                    item.draw(g);
                }
                
                // Draw hit effects
                for (HitEffect effect : hitEffects) {
                    effect.draw(g);
                }
                
                // Draw score
                g.setColor(Color.black);
                g.setFont(new Font("Arial", Font.BOLD, 20));
                g.drawImage(new ImageIcon("D:/Project/icon.png").getImage(), 660, 10, 50, 50, this);
                g.drawString("Alphabet Ninja", 625, 80);
                g.drawString("Score: " + score, 650, 100);
                
                // Draw warning message if active
                if (warningTimer > 0) {
                    g.setColor(Color.RED);
                    g.setFont(new Font("Arial", Font.BOLD, 24));
                    g.drawString(warningMessage, WIDTH/2 - 150, 60);
                }
                
                // Draw current input text for word typing
                if (currentInput.length() > 0) {
                    g.setColor(Color.GREEN);
                    g.setFont(new Font("Arial", Font.BOLD, 24));
                    g.drawString("Typing: " + currentInput.toString(), WIDTH/2 - 80, HEIGHT - 30);
                }
                
                // Draw attempts left indicator
                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial", Font.BOLD, 20));
                g.drawString("Attempts left: " + (maxWrongAttempts - wrongAttempts), WIDTH - 180, 30);
            } else {
                // Draw start/restart message
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 30));
                g.drawString("Press any key to start", WIDTH/2 - 150, HEIGHT/2);
            }
        }
    }
    
        // Main method
        public static void main(String[] args) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Set system look and feel for better UI appearance
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    } catch (Exception e) {
                        // If system look and feel is not available, continue with default
                        System.out.println("Could not set system look and feel: " + e.getMessage());
                    }
                    
                    // Create and start the game
                    new AlphabetNinja();
                    
                    // Print startup message to console
                    System.out.println("AlphabetNinja game started successfully!");
                }
            });
        }
    }