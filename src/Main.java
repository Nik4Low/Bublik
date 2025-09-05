import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class Main extends JPanel implements ActionListener {
    private double angleX = 0;
    private double angleY = 0;
    private double angleZ = 0;
    private Timer timer;
    private List<Point3D> vertices;
    private List<int[]> faces;
    
    public Main() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);
        
        // Создаем тор
        createTorus(0.5, 0.2, 40, 20);
        
        // Запускаем анимацию
        timer = new Timer(20, this); // ~60 FPS
        timer.start();
    }
    
    private void createTorus(double r1, double r2, int n1, int n2) {
        vertices = new ArrayList<>();
        faces = new ArrayList<>();
        
        // Создаем вершины
        for (int i = 0; i <= n1; i++) {
            double theta = i * 2 * Math.PI / n1;
            for (int j = 0; j <= n2; j++) {
                double phi = j * 2 * Math.PI / n2;
                double x = (r1 + r2 * Math.cos(phi)) * Math.cos(theta);
                double y = (r1 + r2 * Math.cos(phi)) * Math.sin(theta);
                double z = r2 * Math.sin(phi);
                vertices.add(new Point3D(x, y, z));
            }
        }
        
        // Создаем грани
        for (int i = 0; i < n1; i++) {
            for (int j = 0; j < n2; j++) {
                int p00 = i * (n2 + 1) + j;
                int p01 = p00 + 1;
                int p10 = (i + 1) * (n2 + 1) + j;
                int p11 = p10 + 1;
                faces.add(new int[]{p00, p10, p01});
                faces.add(new int[]{p10, p11, p01});
            }
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Центр экрана
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        
        // Масштаб
        double scale = 200;
        
        // Рисуем тор
        g2d.setColor(Color.CYAN);
        g2d.setStroke(new BasicStroke(1));
        
        for (int[] face : faces) {
            Point3D p1 = vertices.get(face[0]);
            Point3D p2 = vertices.get(face[1]);
            Point3D p3 = vertices.get(face[2]);
            
            // Применяем вращение
            Point3D rotated1 = rotatePoint(p1, angleX, angleY, angleZ);
            Point3D rotated2 = rotatePoint(p2, angleX, angleY, angleZ);
            Point3D rotated3 = rotatePoint(p3, angleX, angleY, angleZ);
            
            // Проекция на экран
            int x1 = centerX + (int) (rotated1.x * scale);
            int y1 = centerY + (int) (rotated1.y * scale);
            int x2 = centerX + (int) (rotated2.x * scale);
            int y2 = centerY + (int) (rotated2.y * scale);
            int x3 = centerX + (int) (rotated3.x * scale);
            int y3 = centerY + (int) (rotated3.y * scale);
            
            // Рисуем линии
            g2d.drawLine(x1, y1, x2, y2);
            g2d.drawLine(x2, y2, x3, y3);
            g2d.drawLine(x3, y3, x1, y1);
        }
    }
    
    private Point3D rotatePoint(Point3D p, double angleX, double angleY, double angleZ) {
        double x = p.x;
        double y = p.y;
        double z = p.z;
        
        // Вращение вокруг оси X
        double cosX = Math.cos(angleX);
        double sinX = Math.sin(angleX);
        double newY = y * cosX - z * sinX;
        double newZ = y * sinX + z * cosX;
        y = newY;
        z = newZ;
        
        // Вращение вокруг оси Y
        double cosY = Math.cos(angleY);
        double sinY = Math.sin(angleY);
        double newX = x * cosY + z * sinY;
        newZ = -x * sinY + z * cosY;
        x = newX;
        z = newZ;
        
        // Вращение вокруг оси Z
        double cosZ = Math.cos(angleZ);
        double sinZ = Math.sin(angleZ);
        newX = x * cosZ - y * sinZ;
        newY = x * sinZ + y * cosZ;
        x = newX;
        y = newY;
        
        return new Point3D(x, y, z);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // Обновляем углы вращения
        angleX += 0.02;
        angleY += 0.01;
        angleZ += 0.015;
        
        repaint();
    }
    
    private static class Point3D {
        double x, y, z;
        
        Point3D(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Rotating Torus");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new Main());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}