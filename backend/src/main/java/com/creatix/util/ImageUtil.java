package com.creatix.util;

import com.creatix.domain.enums.util.ImageSize;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Tomas Michalek on 22/05/2017.
 */
public class ImageUtil {

    public static byte[] resizeImageToJpeg(byte[] image, ImageSize imageSize) throws IOException {
        if (imageSize == ImageSize.ORIGINAL) {
            return image;
        }

        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(image));

        double ratio = imageSize.getWidth() / originalImage.getWidth();

        BufferedImage resizedImage = new BufferedImage(imageSize.getWidth(), (int) (originalImage.getHeight() * ratio), originalImage.getType());
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, imageSize.getWidth(), (int) (originalImage.getHeight() * ratio), null);
        g.dispose();

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpeg", baos);
        return baos.toByteArray();
    }

}
