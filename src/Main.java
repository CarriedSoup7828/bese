import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

public class Main extends JFrame implements MouseListener {
    private BufferedImage image;
    private WritableRaster r;
    private Point[] points = new Point[4];
    private int[] whitePixel = {255, 255, 255};
    private int k = 0;
    private int width = 0, height = 0;

    Main() throws IOException {
        setTitle("Drawing Graphics in Frames");
        setBounds(30, 0, 1200, 1000); // положение и размеры окна
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            image = ImageIO.read(new File("nature-wallpaper-07.jpg"));
            width = image.getWidth();
            height = image.getHeight();
            r = image.getRaster();
            for (int x = 0; x < width; x++)
                for (int y = 0; y < height; y++) {

                }
        } catch (IOException e) {
            e.printStackTrace();
        }
        addMouseListener(this);
        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        if (image != null) g.drawImage(image, 0, 0, null);
    }

    private int sign(int x) {
        if (x > 0) return 1;
        else if (x < 0) return -1;
        else return 0;
    }

    // рисуем линию из точки (x1,y1) в точку (x2,y2)
    public void line(int x1, int y1, int x2, int y2) {
        int[] pixel = new int[3];
        pixel[0] = 255;
        pixel[1] = pixel[2] = 0;
        int dX = Math.abs(x2 - x1);
        int dY = Math.abs(y2 - y1);
        if (dX >= dY) { // если наклон по X больше Y, то X меняем на 1 и смотрим Y
            if (x1 > x2) { // если точка 2 правее точки 1, меняем их местами
                int t = x1;
                x1 = x2;
                x2 = t;
                t = y1;
                y1 = y2;
                y2 = t;
            }
            int err = 0; // накапливаемая "ошибка"
            int dErr = dY;
            int y = y1;
            int dirY = sign(y2 - y1);
            for (int x = x1; x <= x2; x++) {
                r.setPixel(x, y, pixel);
                err += dErr;
                if (err + err >= dX) {
                    y += dirY;
                    err -= dX;
                }
            }
        } else { // если наклон по Y больше, то, наоборот, Y меняем на 1 и смотрим X
            if (y1 > y2) { // если точка 2 ближе точки 1, меняем их местами
                int t = x1;
                x1 = x2;
                x2 = t;
                t = y1;
                y1 = y2;
                y2 = t;
            }
            int err = 0; // накапливаемая "ошибка"
            int dErr = dX;
            int x = x1;
            int dirX = sign(x2 - x1);
            for (int y = y1; y <= y2; y++) {
                r.setPixel(x, y, pixel);
                err += dErr;
                if (err + err >= dY) {
                    x += dirX;
                    err -= dY;
                }
            }
        }
    }

    // Закрашиваем треугольник по трем указанным точкам
    public void bezie(Point[] points) {
        // рисование кривой Безье по четырем точкам, указанным в массиве points
        int oldBx = points[0].x, oldBy = points[0].y;
        double t;
        for (int i = 0, Bx, By; i <= 100; i++) {
            t = i / 100.0;
            Bx = (int) ((1 - t) * (1 - t) * (1 - t) * points[0].x + 3 * t * (1 - t) * (1 - t) * points[1].x +
                    3 * t * t * (1 - t) * points[2].x + t * t * t * points[3].x + 0.5);
            By = (int) ((1 - t) * (1 - t) * (1 - t) * points[0].y + 3 * t * (1 - t) * (1 - t) * points[1].y +
                    3 * t * t * (1 - t) * points[2].y + t * t * t * points[3].y + 0.5);
            line(oldBx, oldBy, Bx, By);
            oldBx = Bx;
            oldBy = By;
        }
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new Main();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        // Закрасим картинку в белый цвет
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                r.setPixel(x, y, whitePixel);
        // зададим опорные точки для кривой Безье
        points[0] = new Point(width / 50+180, height / 15+400);
        points[1] = new Point(width / 50+180, height * 3 / 15+400);
        points[2] = new Point(width * 8/ 50+180, height * 3 / 15+400);
        points[3] = new Point(width *8/ 50+180, height / 15+400);

        // нарисуем опорные вектора
        line(points[0].x, points[0].y, points[1].x, points[1].y);
        line(points[2].x, points[2].y, points[3].x, points[3].y);
        // Вызовем рисование кривой Безье
        bezie(points);
        points[0] = new Point(width / 50+180, height  / 15+400);
        points[1] = new Point(width / 50+180, 400-height / 15);
        points[2] = new Point(width * 8/ 50+180, 400-height  / 15);
        points[3] = new Point(width *8/ 50+180, height / 15+400);


        // нарисуем опорные вектора
        line(points[0].x, points[0].y, points[1].x, points[1].y);
        line(points[2].x, points[2].y, points[3].x, points[3].y);
        // Вызовем рисование кривой Безье
        bezie(points);
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
    }
}