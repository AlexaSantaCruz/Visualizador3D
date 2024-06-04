
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class Model {
    List<Vertex> vertices = new ArrayList<>();
    List<TextureVertex> textureVertices = new ArrayList<>();
    List<Face> faces = new ArrayList<>();
    Texture texture = new Texture();
    double minX, maxX, minY, maxY, minZ, maxZ;

    public void loadFromOBJ(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        boolean firstVertex = true;
        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split("\\s+");
            if (tokens[0].equals("v")) {
                double x = Double.parseDouble(tokens[1]);
                double y = Double.parseDouble(tokens[2]);
                double z = Double.parseDouble(tokens[3]);

                if (firstVertex) {
                    minX = maxX = x;
                    minY = maxY = y;
                    minZ = maxZ = z;
                    firstVertex = false;
                } else {
                    if (x < minX) minX = x;
                    if (x > maxX) maxX = x;
                    if (y < minY) minY = y;
                    if (y > maxY) maxY = y;
                    if (z < minZ) minZ = z;
                    if (z > maxZ) maxZ = z;
                }

                vertices.add(new Vertex(x, y, z));
            } else if (tokens[0].equals("vt")) {
                textureVertices.add(new TextureVertex(Double.parseDouble(tokens[1]),
                                                      Double.parseDouble(tokens[2])));
            } else if (tokens[0].equals("f")) {
                int[] vertexIndices = new int[tokens.length - 1];
                int[] textureIndices = new int[tokens.length - 1];
                for (int i = 1; i < tokens.length; i++) {
                    String[] subTokens = tokens[i].split("/");
                    vertexIndices[i - 1] = Integer.parseInt(subTokens[0]) - 1;
                    textureIndices[i - 1] = subTokens.length > 1 ? Integer.parseInt(subTokens[1]) - 1 : -1;
                }
                faces.add(new Face(vertexIndices, textureIndices));
            }
        }
        reader.close();
    }

    public void loadTexture(String filename) throws IOException {
        texture.load(filename);
    }

    public void scale(double scaleFactor) {
        for (Vertex vertex : vertices) {
            vertex.x *= scaleFactor;
            vertex.y *= scaleFactor;
            vertex.z *= scaleFactor;
        }
        // Actualizar límites después de la escala
        minX *= scaleFactor;
        maxX *= scaleFactor;
        minY *= scaleFactor;
        maxY *= scaleFactor;
        minZ *= scaleFactor;
        maxZ *= scaleFactor;
    }


    public void translate(double dx, double dy, double dz) {
        for (Vertex vertex : vertices) {
            vertex.x += dx;
            vertex.y += dy;
            vertex.z += dz;
        }
    }
}