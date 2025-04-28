package tn.esprit.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class ImageResizer {
    private static final int TARGET_WIDTH = 375;
    private static final int TARGET_HEIGHT = 563;

    public static void resizeAllImages() {
        String[] imageDirs = {
            "src/main/resources/images",
            "src/main/resources/assets",
            "src/main/resources/uploads"
        };

        for (String dir : imageDirs) {
            try {
                Path path = Paths.get(dir);
                if (Files.exists(path)) {
                    try (Stream<Path> paths = Files.walk(path)) {
                        paths.filter(Files::isRegularFile)
                            .filter(p -> isImageFile(p.toString()))
                            .forEach(ImageResizer::resizeImage);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error processing directory: " + dir);
                e.printStackTrace();
            }
        }
    }

    private static boolean isImageFile(String fileName) {
        String lowerCase = fileName.toLowerCase();
        return lowerCase.endsWith(".jpg") || lowerCase.endsWith(".jpeg") 
            || lowerCase.endsWith(".png") || lowerCase.endsWith(".gif");
    }

    private static void resizeImage(Path imagePath) {
        try {
            File inputFile = imagePath.toFile();
            BufferedImage originalImage = ImageIO.read(inputFile);
            
            if (originalImage == null) {
                System.err.println("Could not read image: " + imagePath);
                return;
            }

            BufferedImage resizedImage = new BufferedImage(TARGET_WIDTH, TARGET_HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = resizedImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(originalImage, 0, 0, TARGET_WIDTH, TARGET_HEIGHT, null);
            g2d.dispose();

            // Save the resized image
            String formatName = getImageFormat(imagePath.toString());
            ImageIO.write(resizedImage, formatName, inputFile);
            
            System.out.println("Resized: " + imagePath);
        } catch (IOException e) {
            System.err.println("Error resizing image: " + imagePath);
            e.printStackTrace();
        }
    }

    private static String getImageFormat(String fileName) {
        String lowerCase = fileName.toLowerCase();
        if (lowerCase.endsWith(".jpg") || lowerCase.endsWith(".jpeg")) {
            return "jpg";
        } else if (lowerCase.endsWith(".png")) {
            return "png";
        } else if (lowerCase.endsWith(".gif")) {
            return "gif";
        }
        return "jpg"; // default format
    }
} 