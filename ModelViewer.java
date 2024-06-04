import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ModelViewer extends JPanel {
    private Model model;
    private double angleX = 0;
    private double angleY = 0;
    private BufferedImage buffer;
    private double scaleFactor = 1.5;
    private double translateX = 0;
    private double translateY = 0;
    private double translateZ = 0;

    private boolean showWireframe = false; // Variable para alternar entre malla y relleno

    public ModelViewer(Model model) {
        this.model = model;

        setPreferredSize(new Dimension(800, 600));
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            private int lastX, lastY;

            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - lastX;
                int dy = e.getY() - lastY;
                angleX += dy * 0.01;
                angleY += dx * 0.01;
                lastX = e.getX();
                lastY = e.getY();
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                lastX = e.getX();
                lastY = e.getY();
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_B:
                        scaleFactor *= 1.1;
                        model.scale(1.1);
                        repaint();
                        break;
                    case KeyEvent.VK_N:
                        scaleFactor *= 1.1;
                        model.scale(1.1);
                        repaint();
                        break;
                    case KeyEvent.VK_M:
                        scaleFactor /= 1.1;
                        model.scale(1 / 1.1);
                        repaint();
                        break;
                    case KeyEvent.VK_UP:
                        translateY -= 5;
                        repaint();
                        break;
                    case KeyEvent.VK_DOWN:
                        translateY += 5;
                        repaint();
                        break;
                    case KeyEvent.VK_LEFT:
                        translateX -= 5;
                        repaint();
                        break;
                    case KeyEvent.VK_RIGHT:
                        translateX += 5;
                        repaint();
                        break;
                        case KeyEvent.VK_I:
                        translateZ += 1;
                        repaint();
                        break;
                    case KeyEvent.VK_O:
                        translateZ -= 1;
                        repaint();
                        break;
                    case KeyEvent.VK_1: // Tecla 1 para cambiar de modelo
                        loadNewModel("Luigi\\luigijp.obj", "Pikachu/yellow.png");
                        break;
                    case KeyEvent.VK_2: // Tecla 2 para cambiar de modelo
                        loadNewModel("Yoshi/Yoshi.obj", "Pikachu/yellow.png");
                        break;
                    case KeyEvent.VK_3: // Tecla 3 para cambiar de modelo
                        loadNewModel("Mario\\mariojp.obj", "Pikachu/yellow.png");
                        break;
                    case KeyEvent.VK_4: // Tecla 4 para cambiar de modelo
                        loadNewModel("Sonic\\Sonic_1.obj", "Pikachu/yellow.png");
                        break;
                    case KeyEvent.VK_5: // Tecla 5 para cambiar de modelo
                        loadNewModel("Dragonair\\hakuryu.obj", "Pikachu/yellow.png");
                        break;
                    case KeyEvent.VK_6: // Tecla 6 para cambiar de modelo
                        loadNewModel("Pit\\pitb.obj", "Pikachu/yellow.png");
                        break;
                    case KeyEvent.VK_7: // Tecla 7 para cambiar de modelo
                        loadNewModel("Clefairy_Teacher\\Clefairy Teacher\\clefteacher.obj", "Pikachu/yellow.png");
                        break;
                    case KeyEvent.VK_P: // Tecla P para alternar entre malla y relleno
                        showWireframe = !showWireframe;
                        repaint();
                        break;
                }
            }
        });
        setFocusable(true);
        requestFocusInWindow();
    }

    private void loadNewModel(String modelPath, String texturePath) {
        try {
            model = new Model();
            model.loadFromOBJ(modelPath);
            if (texturePath != null && !texturePath.isEmpty()) {
                model.loadTexture(texturePath);
            }
            resetTransforms();
            repaint();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetTransforms() {
        angleX = 0;
        angleY = 0;
        translateX = 0;
        translateY = 0;
        translateZ = 0;
        scaleFactor = 1.5;
    }

    private Point project(Vertex v) {
        double scale = 200 * scaleFactor;
        double[] rotated = rotate(v.x, v.y, v.z);
        int x = (int) (rotated[0] * scale + getWidth() / 2 + translateX);
        int y = (int) (-rotated[1] * scale + getHeight() / 2 + translateY);
        return new Point(x, y);
    }

    private double[] rotate(double x, double y, double z) {
        double centerX = (model.maxX + model.minX) / 2;
        double centerY = (model.maxY + model.minY) / 2;
        double centerZ = (model.maxZ + model.minZ) / 2;

        x -= centerX;
        y -= centerY;
        z -= centerZ;

        double cosY = Math.cos(angleY);
        double sinY = Math.sin(angleY);
        double cosX = Math.cos(angleX);
        double sinX = Math.sin(angleX);

        double xz = cosY * x - sinY * (z + translateZ);
        double yz = sinY * x + cosY * (z + translateZ);
        double yx = cosX * yz - sinX * y;
        double yy = sinX * yz + cosX * y;

        xz += centerX;
        yx += centerY;
        yy += centerZ;

        return new double[]{xz, yx, yy};
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (buffer != null) {
            g.drawImage(buffer, 0, 0, null);
        }
    }

    public void putPixel(int x, int y, int rgb) {
        if (x >= 0 && x < buffer.getWidth() && y >= 0 && y < buffer.getHeight()) {
            buffer.setRGB(x, y, rgb);
        }
    }

 
    @Override
    public void repaint() {
        if (buffer == null) return;
        super.repaint();
        buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = buffer.createGraphics();
        for (Face face : model.faces) {
            if (face.vertexIndices.length == 3) {
                Vertex v0 = model.vertices.get(face.vertexIndices[0]);
                Vertex v1 = model.vertices.get(face.vertexIndices[1]);
                Vertex v2 = model.vertices.get(face.vertexIndices[2]);
                Point p0 = project(v0);
                Point p1 = project(v1);
                Point p2 = project(v2);

                if (!showWireframe && face.textureIndices[0] >= 0 && face.textureIndices[1] >= 0 && face.textureIndices[2] >= 0) {
                    TextureVertex t0 = model.textureVertices.get(face.textureIndices[0]);
                    TextureVertex t1 = model.textureVertices.get(face.textureIndices[1]);
                    TextureVertex t2 = model.textureVertices.get(face.textureIndices[2]);
                    drawTexturedTriangle(p0.x, p0.y, t0.u, t0.v,
                                         p1.x, p1.y, t1.u, t1.v,
                                         p2.x, p2.y, t2.u, t2.v);
                } else {
                    drawNonVerticalLineBresenham(p0.x, p0.y, p1.x, p1.y, Color.BLACK);
                    drawNonVerticalLineBresenham(p1.x, p1.y, p2.x, p2.y, Color.BLACK);
                    drawNonVerticalLineBresenham(p2.x, p2.y, p0.x, p0.y, Color.BLACK);
                }

                // Dibujar la malla
                if (showWireframe) {
                    drawNonVerticalLineBresenham(p0.x, p0.y, p1.x, p1.y, Color.RED);
                    drawNonVerticalLineBresenham(p1.x, p1.y, p2.x, p2.y, Color.RED);
                    drawNonVerticalLineBresenham(p2.x, p2.y, p0.x, p0.y, Color.RED);
                }
            }
        }
        g2d.dispose();
    }

    public void drawNonVerticalLineBresenham(int x0, int y0, int x1, int y1, Color color) {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;
        int e2;

        while (true) {
            putPixel(x0, y0, color.getRGB());

            if (x0 == x1 && y0 == y1) {
                break;
            }

            e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }
    }

    public void putPixel(int x, int y, Color c) {
        if (x >= 0 && x < buffer.getWidth() && y >= 0 && y < buffer.getHeight()) {
            buffer.setRGB(x, y, c.getRGB());
        }
    }

    private void drawTexturedTriangle(int x0, int y0, double u0, double v0,
                                      int x1, int y1, double u1, double v1,
                                      int x2, int y2, double u2, double v2) {
        if (y0 > y1) {
            int tempX = x0, tempY = y0; double tempU = u0, tempV = v0;
            x0 = x1; y0 = y1; u0 = u1; v0 = v1;
            x1 = tempX; y1 = tempY; u1 = tempU; v1 = tempV;
        }
        if (y0 > y2) {
            int tempX = x0, tempY = y0; double tempU = u0, tempV = v0;
            x0 = x2; y0 = y2; u0 = u2; v0 = v2;
            x2 = tempX; y2 = tempY; u2 = tempU; v2 = tempV;
        }
        if (y1 > y2) {
            int tempX = x1, tempY = y1; double tempU = u1; double tempV = v1;
            x1 = x2; y1 = y2; u1 = u2; v1 = v2;
            x2 = tempX; y2 = tempY; u2 = tempU; v2 = tempV;
        }

        int totalHeight = y2 - y0;
        for (int y = y0; y <= y2; y++) {
            boolean secondHalf = y > y1 || y1 == y0;
            int segmentHeight = secondHalf ? y2 - y1 : y1 - y0;
            double alpha = (double)(y - y0) / totalHeight;
            double beta = (double)(y - (secondHalf ? y1 : y0)) / segmentHeight;
            int A = x0 + (int)((x2 - x0) * alpha);
            int B = secondHalf ? x1 + (int)((x2 - x1) * beta) : x0 + (int)((x1 - x0) * beta);
            double texA_u = u0 + (u2 - u0) * alpha;
            double texA_v = v0 + (v2 - v0) * alpha;
            double texB_u = secondHalf ? u1 + (u2 - u1) * beta : u0 + (u1 - u0) * beta;
            double texB_v = secondHalf ? v1 + (v2 - v1) * beta : v0 + (v1 - v0) * beta;
            if (A > B) {
                int tempX = A; A = B; B = tempX;
                double tempU = texA_u; texA_u = texB_u; texB_u = tempU;
                double tempV = texA_v; texA_v = texB_v; texB_v = tempV;
            }
            for (int x = A; x <= B; x++) {
                double phi = B == A ? 1.0 : (double)(x - A) / (double)(B - A);
                double texU = texA_u + (texB_u - texA_u) * phi;
                double texV = texA_v + (texB_v - texA_v) * phi;
                texU = Math.max(0.0, Math.min(1.0, texU));
                texV = Math.max(0.0, Math.min(1.0, texV));
                putPixel(x, y, model.texture.getRGB(texU, texV));
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Model model = new Model();
            try {
                model.loadFromOBJ("Yoshi/Yoshi.obj");
                //model.loadFromOBJ("Clefairy_Teacher\\Clefairy Teacher\\clefteacher.obj");

                model.loadTexture("Pikachu/yellow.png");
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            JFrame frame = new JFrame("Visualizador de Modelo 3D");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new ModelViewer(model));
            frame.pack();
            frame.setVisible(true);
        });
    }
}
