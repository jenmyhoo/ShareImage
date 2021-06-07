package com.xiuchezai.search.util;

import com.xiuchezai.search.bean.AcShareAdCateBean;
import com.xiuchezai.search.bean.AcShareAdContentBean;
import sun.font.FontDesignMetrics;

import javax.imageio.ImageIO;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author hoo
 * @date 2020-07-02 13:59
 */
public class AdPlayBillUtil {
    public static void main(String[] args) {
        AcShareAdCateBean acShareAdCateBean = new AcShareAdCateBean();
        acShareAdCateBean.setAd_url("D:/1.jpg");

        List<AcShareAdContentBean> acShareAdContentBeanList = new ArrayList<>(8);

        AcShareAdContentBean acShareAdContentBean1 = new AcShareAdContentBean();
        acShareAdContentBean1.setContent_type(1);
        acShareAdContentBean1.setContent_param("title");
        acShareAdContentBean1.setStart_x(36);
        acShareAdContentBean1.setStart_y(88);
        acShareAdContentBean1.setEnd_x(714);
        acShareAdContentBean1.setEnd_y(168);
        acShareAdContentBean1.setMax_line(2);
        acShareAdContentBean1.setAlign(1);
        acShareAdContentBean1.setColor("#333333");
        acShareAdContentBean1.setFont_name("黑体");
        acShareAdContentBean1.setFont_size(36);
        acShareAdContentBeanList.add(acShareAdContentBean1);

        AcShareAdContentBean acShareAdContentBean2 = new AcShareAdContentBean();
        acShareAdContentBean2.setContent_type(1);
        acShareAdContentBean2.setContent_param("price");
        acShareAdContentBean2.setStart_x(36);
        acShareAdContentBean2.setStart_y(188);
        acShareAdContentBean2.setEnd_x(714);
        acShareAdContentBean2.setEnd_y(208);
        acShareAdContentBean2.setMax_line(1);
        acShareAdContentBean2.setAlign(1);
        acShareAdContentBean2.setColor("#FB3235");
        acShareAdContentBean2.setFont_name("黑体");
        acShareAdContentBean2.setFont_size(36);
        acShareAdContentBeanList.add(acShareAdContentBean2);

        AcShareAdContentBean acShareAdContentBean3 = new AcShareAdContentBean();
        acShareAdContentBean3.setContent_type(2);
        acShareAdContentBean3.setContent_param("goods_main_img");
        acShareAdContentBean3.setStart_x(36);
        acShareAdContentBean3.setStart_y(248);
        acShareAdContentBean3.setEnd_x(714);
        acShareAdContentBean3.setEnd_y(926);
        acShareAdContentBean3.setMax_line(1);
        acShareAdContentBean3.setAlign(1);
        acShareAdContentBeanList.add(acShareAdContentBean3);

        AcShareAdContentBean acShareAdContentBean4 = new AcShareAdContentBean();
        acShareAdContentBean4.setContent_type(2);
        acShareAdContentBean4.setContent_param("qr");
        acShareAdContentBean4.setStart_x(136);
        acShareAdContentBean4.setStart_y(1014);
        acShareAdContentBean4.setEnd_x(376);
        acShareAdContentBean4.setEnd_y(1254);
        acShareAdContentBeanList.add(acShareAdContentBean4);

        Map<String, String> requestParamMap = new HashMap<>(8);
        requestParamMap.put("title", "王老吉刺柠吉230ml*12罐整箱果汁饮料刺梨汁夏日解渴维c饮料特价");
        requestParamMap.put("price", "￥20.99");
        requestParamMap.put("goods_main_img", "D:/4.jpg");
        requestParamMap.put("qr", "D:/3.jpg");

        BufferedImage image = draw(acShareAdCateBean, acShareAdContentBeanList, requestParamMap);
        try {
            if (image != null) {
                File file = new File("D:/2.jpg");
                ImageIO.write(image, "jpg", file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage draw(AcShareAdCateBean acShareAdCateBean, List<AcShareAdContentBean> acShareAdContentBeanList, Map<String, String> requestParamMap) {
        if (acShareAdCateBean == null || acShareAdCateBean.getAd_url() == null) {
            return null;
        }
        if (acShareAdContentBeanList == null || acShareAdContentBeanList.size() == 0) {
            return null;
        }
        //获取图片对象
        BufferedImage bgImage = getImageByUrl(acShareAdCateBean.getAd_url());
        if (bgImage == null) {
            return null;
        }
        Graphics2D graphics2d = bgImage.createGraphics();
        graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        for (AcShareAdContentBean bean : acShareAdContentBeanList) {
            //类型【1文本/2URL图片/3base64图片】
            switch (bean.getContent_type()) {
                case 1: {
                    drawText(graphics2d, requestParamMap.get(bean.getContent_param()), bean.getStart_x(), bean.getStart_y(), bean.getEnd_x(), bean.getEnd_y()
                            , bean.getMax_line(), bean.getAlign(), bean.getColor(), bean.getFont_name(), bean.getFont_size());
                    break;
                }
                case 2: {
                    //获取图片对象
                    BufferedImage image = getImageByUrl(requestParamMap.get(bean.getContent_param()));
                    if (image == null) {
                        break;
                    }
                    drawImage(graphics2d, image, bean.getAlign(), bean.getStart_x(), bean.getStart_y(), bean.getEnd_x(), bean.getEnd_y());
                    break;
                }
                case 3: {
                    byte[] bytes = FingerUtil.decryptByteBase64(requestParamMap.get(bean.getContent_param()));
                    BufferedImage image = null;
                    try {
                        //image = ImageIO.read(ImageUtil.readBytes(bytes));
                        image = ImageUtil.toBufferedImage(ImageUtil.getImage(bytes));
                        drawImage(graphics2d, image, bean.getAlign(), bean.getStart_x(), bean.getStart_y(), bean.getEnd_x(), bean.getEnd_y());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                default:
                    break;
            }
        }
        graphics2d.dispose();

        return bgImage;
    }

    public static void drawText(Graphics2D graphics2d, String text, int startX, int startY, int endX, int endY,
                                int limitLineNum, int align, String colorValue, String fontName, int fontSize) {
        if (text == null || "".equals(text.trim())) {
            return;
        }
        // 获取计算机上允许使用的中文字体
        List<String> fontNames = Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        if (fontNames == null || !fontNames.contains(fontName)) {
            throw new RuntimeException("计算机上未安装字体【" + fontName + "】");
        }
        Font font = new Font(fontName, Font.PLAIN, fontSize);
        FontDesignMetrics fontDesignMetrics = FontDesignMetrics.getMetrics(font);
        int fontHeight = fontDesignMetrics.getHeight();
//        int ascent = fontDesignMetrics.getAscent();
//        int baselineY = fontHeight * ascent / fontSize;
        //替换后台十六进制表示
        colorValue = colorValue.replaceFirst("#", "").replaceFirst("0x", "");
        Color color = new Color(Integer.parseInt(colorValue, 16));
        graphics2d.setFont(font);
        graphics2d.setPaint(color);

        int lineWidth = endX - startX;
        String[] wraps = makeLineFeed(text, fontDesignMetrics, lineWidth).split("\n");
        int totalLineNum = wraps.length;
        int lineNum = Math.min(totalLineNum, limitLineNum);
        for (int i = 0; i < lineNum; i++) {
            String wrap;
            //最后一行需要特殊处理
            if (i == lineNum - 1) {
                //实际行数大于限定行数
                if (totalLineNum > limitLineNum) {
                    String builder = "";
                    char[] chars = wraps[i].toCharArray();
                    int length = chars.length;
                    int dotWidth = fontDesignMetrics.charWidth('.') * 3;
                    for (int t = 1; t <= 3; t++) {
                        dotWidth = dotWidth - fontDesignMetrics.charWidth(chars[length - t]);
                        if (dotWidth >= 0) {
                            chars[length - t] = '.';
                        } else {
                            builder += ".";
                        }
                    }
                    wrap = new String(chars).concat(builder.toString());
                } else {
                    wrap = wraps[i];
                }
                switch (align) {
                    //居中
                    case 2: {
                        int lastRowWidth = fontDesignMetrics.stringWidth(wrap);
                        startX += (lineWidth - lastRowWidth) / 2;
                        break;
                    }
                    //靠右
                    case 3: {
                        int lastRowWidth = fontDesignMetrics.stringWidth(wrap);
                        startX += (lineWidth - lastRowWidth);
                        break;
                    }
                    //默认靠左
                    default: {
                        break;
                    }
                }
            } else {
                wrap = wraps[i];
            }
            startY += fontHeight;
            graphics2d.drawString(wrap, startX, startY);
        }
    }

    public static String makeLineFeed(String zh, FontDesignMetrics metrics, int max_width) {
        // 每个单词后追加空格
        if (zh == null || "".equals(zh)) {
            return "";
        }
        String lineString = "\n";
        //防止widows/linux不一致问题
        zh = zh.replace("\r\n", lineString);
        StringBuilder sb = new StringBuilder(zh.length());
        int line_width = 0;
        int length = zh.length();
        for (int i = 0; i < length; i++) {
            char c = zh.charAt(i);
            // 如果主动换行则跳过
            if (c == '\n') {
                sb.append(lineString);
                line_width = 0;
                continue;
            }
            // FontDesignMetrics 的 charWidth() 方法可以计算字符的宽度
            int char_width = metrics.charWidth(c);
            line_width += char_width;
            // 宽度超出了海报的最大宽度，则换行
            if (line_width > max_width) {
                //英文逗号44【金额千分位分隔符】或者点号46【小数点】则判断前后是否数字
                if (c == 44 || c == 46) {
                    if (i == length - 1) {
                        sb.append(lineString).append(c);
                        line_width = char_width;
                    } else {
                        int t = i - 1;
                        char prevChar = zh.charAt(t);
                        char nextChar = zh.charAt(t + 2);
                        //当前为逗号或者点号时则前后必须都是数字才是整体
                        if ((prevChar >= 48 && prevChar <= 57) && (nextChar >= 48 && nextChar <= 57)) {
                            //准备回退数字，但当单字竖排除外
                            int new_line_width = char_width;
                            StringBuilder tempBuilder = new StringBuilder("").append(c);
                            //起码一行一个字符，所以定t=i-1>=0
                            for (; t >= 0; t--) {
                                prevChar = zh.charAt(t);
                                if (prevChar == '\n') {
                                    break;
                                } else if (prevChar == c || (prevChar >= 48 && prevChar <= 57)) {
                                    //当前为逗号时回退也只能碰上逗号或者数字表示金额，当前是点号时一般原则回退只能是数字为程序判断简单直接相等也可采用在IP地址表示
                                    new_line_width += metrics.charWidth(prevChar);
                                    if (new_line_width > max_width) {
                                        sb.append(lineString);
                                        break;
                                    } else {
                                        tempBuilder.append(prevChar);
                                        sb.deleteCharAt(sb.length() - 1);
                                    }
                                } else {
                                    sb.append(lineString);
                                    break;
                                }
                            }
                            int tempLastIndex = tempBuilder.length() - 1;
                            char tempChar = tempBuilder.charAt(tempLastIndex);
                            if (tempChar == 44 || tempChar == 46) {
                                //删除换行符号，压回不是数字体的英文逗号或者点号，说明此时的逗号或者点号为段落分隔符
                                sb.deleteCharAt(sb.length() - 1).append(tempChar).append(lineString);
                                tempBuilder.deleteCharAt(tempLastIndex);
                            }
                            String temp = tempBuilder.reverse().toString();
                            line_width = metrics.stringWidth(temp);
                        } else {
                            sb.append(lineString).append(c);
                            line_width = char_width;
                        }
                    }
                } else if (c >= 48 && c <= 57) {
                    //准备回退数字，但当单字竖排除外
                    int new_line_width = char_width;
                    StringBuilder tempBuilder = new StringBuilder("").append(c);
                    //起码一行一个字符，所以定t=i-1>=0
                    for (int t = i - 1; t >= 0; t--) {
                        char prevChar = zh.charAt(t);
                        if (prevChar == '\n') {
                            break;
                        } else if (prevChar == 44 || prevChar == 46 || (prevChar >= 48 && prevChar <= 57)) {
                            //上一个是英文逗号或者点号或者数字
                            new_line_width += metrics.charWidth(prevChar);
                            if (new_line_width > max_width) {
                                sb.append(lineString);
                                break;
                            } else {
                                tempBuilder.append(prevChar);
                                sb.deleteCharAt(sb.length() - 1);
                            }
                        } else {
                            sb.append(lineString);
                            break;
                        }
                    }
                    int tempLastIndex = tempBuilder.length() - 1;
                    char tempChar = tempBuilder.charAt(tempLastIndex);
                    if (tempChar == 44 || tempChar == 46) {
                        //删除换行符号，压回不是数字体的英文逗号或者点号，说明此时的逗号或者点号为段落分隔符
                        sb.deleteCharAt(sb.length() - 1).append(tempChar).append(lineString);
                        tempBuilder.deleteCharAt(tempLastIndex);
                    }
                    String temp = tempBuilder.reverse().toString();
                    line_width = metrics.stringWidth(temp);
                } else if ((c >= 65 && c <= 90) || (c >= 97 && c <= 122)) {
                    //准备回退英文单词，但当单字竖排除外
                    int new_line_width = char_width;
                    StringBuilder tempBuilder = new StringBuilder(c);
                    //起码一行一个字符，所以定t=i-1>=0
                    for (int t = i - 1; t >= 0; t--) {
                        char prevChar = zh.charAt(t);
                        if (prevChar == '\n') {
                            break;
                        } else if ((prevChar >= 65 && prevChar <= 90) || (prevChar >= 97 && prevChar <= 122)) {
                            //上一个是英文，则找到最后一次出现不是英文的字符包括分隔符如逗号等
                            new_line_width += metrics.charWidth(prevChar);
                            if (new_line_width > max_width) {
                                sb.append(lineString);
                                break;
                            } else {
                                tempBuilder.append(prevChar);
                                sb.deleteCharAt(sb.length() - 1);
                            }
                        } else {
                            sb.append(lineString);
                            break;
                        }
                    }
                    String temp = tempBuilder.reverse().toString();
                    sb.append(temp);
                    line_width = metrics.stringWidth(temp);
                } else {
                    //其它情况直接换行
                    sb.append(lineString).append(c);
                    line_width = char_width;
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static void drawImage(Graphics2D graphics2d, BufferedImage bufferedImage, int align, int startX, int startY, int endX, int endY) {
        int backgroundWidth = endX - startX;
        int backgroundHeight = endY - startY;
        int width = backgroundWidth;
        int height = backgroundHeight;
        int pic_width = bufferedImage.getWidth();
        int pic_height = bufferedImage.getHeight();
        // 宽度
        int maxWidth = Math.min(width, pic_width);
        // 高度
        int maxHeight = Math.min(height, pic_height);
        if (pic_width / width > pic_height / height) {
            width = maxWidth;
            height = pic_height * width / pic_width;
        } else {
            height = maxHeight;
            width = pic_width * height / pic_height;
        }

        // 在背景上绘制封面图，位置大家可以拓展，如靠左居中
        // 经上述计算后图片宽高最大值为图片绘制宽高最大值，所以减法不会出现负数
        switch (align) {
            //居中
            case 2: {
                startX += (backgroundWidth - width) / 2;
                startY += (backgroundHeight - height) / 2;
                break;
            }
            //靠右
            case 3: {
                startX += (backgroundWidth - width);
                startY += 0;
                break;
            }
            //默认靠左
            default: {
                startX += 0;
                startY += 0;
                break;
            }
        }
        graphics2d.drawImage(bufferedImage, startX, startY, width, height, null);
    }

    public static BufferedImage getImageByUrl(String imageUrl) {
        if (imageUrl == null) {
            return null;
        }
        //网络图片没有前缀，此处为修车仔系统使用fastdfs原因
        if (imageUrl.startsWith("group")) {
            ResourceBundle bundle = PropertiesUtil.loadResourceBundle("config");
            imageUrl = bundle.getString("img_domain") + imageUrl;
        }
        //获取图片对象
        byte[] bytes;
        if (imageUrl.startsWith("http")) {
            bytes = ImageUtil.getImageFromNetByUrl(imageUrl);
        } else {
            bytes = ImageUtil.getImageFromLocalByUrl(imageUrl);
        }
        BufferedImage image = null;
        try {
            image = ImageUtil.toBufferedImage(ImageUtil.getImage(bytes));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }
}
