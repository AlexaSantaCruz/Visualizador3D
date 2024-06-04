

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

class Texture {
    private BufferedImage image;

    public void load(String filename) throws IOException {
        image = ImageIO.read(new File(filename));
    }

    public int getRGB(double u, double v) {
        int x = (int) (u * (image.getWidth() - 1));
        int y = (int) (v * (image.getHeight() - 1));

        x = Math.max(0, Math.min(x, image.getWidth() - 1));
        y = Math.max(0, Math.min(y, image.getHeight() - 1));

        return image.getRGB(x, y);
    }
}