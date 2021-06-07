package com.xiuchezai.search.util;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * @author hoo
 * @date 2020-06-08 13:50
 */
public class ImageUtil {
    /**
     * 根据网络地址获得数据流
     *
     * @param strUrl 网络连接地址
     * @return
     */
    public static byte[] getImageFromNetByUrl(String strUrl) {
        HttpURLConnection conn = null;
        InputStream inStream = null;
        try {
            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5 * 1000);
            // 通过输入流获取图片数据
            inStream = conn.getInputStream();
            byte[] bytes = readInputStream(inStream);
            return bytes;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 根据地址获得数据的字节流
     *
     * @param strUrl 本地连接地址
     * @return
     */
    public static byte[] getImageFromLocalByUrl(String strUrl) {
        InputStream inStream = null;
        try {
            File imageFile = new File(strUrl);
            if (!imageFile.exists()) {
                return null;
            }
            inStream = new FileInputStream(imageFile);
            // 得到图片的二进制数据
            byte[] btImg = readInputStream(inStream);
            return btImg;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 从输入流中获取数据
     *
     * @param inStream 输入流
     * @return
     * @throws Exception
     */
    public static byte[] readInputStream(InputStream inStream) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[10240];
            int len = -1;
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            outStream.flush();
            byte[] bytes = outStream.toByteArray();
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 从数据流中获取输入流
     *
     * @param bytes 流二进制
     * @return
     * @throws Exception
     */
    public static InputStream readBytes(byte[] bytes) {
        InputStream inputStream = new ByteArrayInputStream(bytes);
        return inputStream;
    }

    /**
     * 将图片写入到磁盘
     *
     * @param imageBytes   图片数据流
     * @param fileImageUrl 文件保存时的名称
     */
    public static void writeImageToDisk(byte[] imageBytes, String fileImageUrl) {
        FileOutputStream fops = null;
        try {
            File file = new File(fileImageUrl);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            fops = new FileOutputStream(file);
            fops.write(imageBytes);
            fops.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fops != null) {
                try {
                    fops.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取图像对象
     */
    public static Image getImage(String fileName) {
        try {
            // 读入文件
            File file = new File(fileName);
            if (!file.exists()) {
                return null;
            }
            // 构造Image对象
            //BufferedImage image = ImageIO.read(file);
            Image image = Toolkit.getDefaultToolkit().getImage(file.getPath());
            return image;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取图像对象
     */
    public static Image getImage(InputStream stream) {
        try {
            // 构造Image对象
            //BufferedImage image = ImageIO.read(stream);
            Image image = Toolkit.getDefaultToolkit().createImage(readInputStream(stream));
            return image;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取图像对象
     */
    public static Image getImage(byte[] bytes) {
        try {
            // 构造Image对象
            //BufferedImage image = ImageIO.read(readBytes(bytes));
            Image image = Toolkit.getDefaultToolkit().createImage(bytes);
            return image;
        } catch (Exception e) {
            return null;
        }
    }

    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        image = new ImageIcon(image).getImage();
        BufferedImage bufferedImage = null;
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            int transparency = Transparency.OPAQUE;
            GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
            GraphicsConfiguration graphicsConfiguration = graphicsDevice.getDefaultConfiguration();
            bufferedImage = graphicsConfiguration.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {

        }
        if (bufferedImage == null) {
            int imageType = BufferedImage.TYPE_INT_RGB;
            bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), imageType);
        }
        Graphics graphics = bufferedImage.createGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();
        return bufferedImage;
    }

    /**
     * 按宽度\高度进行压缩
     *
     * @param image     源图像对象
     * @param maxWidth  int 最大宽度
     * @param maxHeight int 最大高度
     */
    public static byte[] resizeFix(Image image, int maxWidth, int maxHeight) {
        if (image == null) {
            return null;
        }
        // 得到源图宽
        int width = image.getWidth(null);
        // 得到源图长
        int height = image.getHeight(null);
        if (width / height > maxWidth / maxHeight) {
            int h = (int) (height * maxWidth / width);
            return resize(image, maxWidth, h);
        } else {
            int w = (int) (width * maxHeight / height);
            return resize(image, w, maxHeight);
        }
    }

    /**
     * 以宽度为基准，等比例放缩图片
     *
     * @param image 源图像对象
     * @param w     int 新宽度
     */
    public static byte[] resizeByWidth(Image image, int w) {
        if (image == null) {
            return null;
        }
        // 得到源图宽
        int width = image.getWidth(null);
        // 得到源图长
        int height = image.getHeight(null);
        int h = (int) (height * w / width);
        return resize(image, w, h);
    }

    /**
     * 以高度为基准，等比例缩放图片
     *
     * @param image 源图像对象
     * @param h     int 新高度
     */
    public static byte[] resizeByHeight(Image image, int h) {
        if (image == null) {
            return null;
        }
        // 得到源图宽
        int width = image.getWidth(null);
        // 得到源图长
        int height = image.getHeight(null);
        int w = (int) (width * h / height);
        return resize(image, w, h);
    }

    /**
     * 强制压缩/放大图片到固定的大小
     *
     * @param sourceImage 源图像对象
     * @param w           int 新宽度
     * @param h           int 新高度
     */
    public static byte[] resize(Image sourceImage, int w, int h) {
        byte[] bytes = null;
        ByteArrayOutputStream out = null;
        try {
            // SCALE_SMOOTH 的缩略算法 生成缩略图片的平滑度的 优先级比速度高 生成的图片质量比较好 但速度慢
            BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            // 绘制缩小后的图
            image.getGraphics().drawImage(sourceImage, 0, 0, w, h, null);
            sourceImage.flush();
            sourceImage = null;
            out = new ByteArrayOutputStream(w * h);
//            // 可以正常实现bmp、png、gif转jpg
//            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
//            // JPEG编码
//            encoder.encode(image);
            ImageIO.write(image, "jpg", out);
            out.flush();
            bytes = out.toByteArray();
            image.flush();
            image = null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytes;
    }
}
