package com.xiuchezai.search.bean;

import java.io.Serializable;

/**
 * @author hoo
 * @date 2020-07-02 15:01
 */
public class AcShareAdContentBean implements Serializable {
    private static final long serialVersionUID = -6747617090703767036L;
    private int content_id;
    /**
     * 海报标识
     */
    private int ad_id;
    /**
     * 类型【1文本/2URL图片/3base64图片】
     */
    private int content_type;
    /**
     * 参数名【由前端传】
     *
     * @return
     */
    private String content_param;
    /**
     * 起始坐标X
     */
    private int start_x;
    /**
     * 起始坐标Y
     */
    private int start_y;
    /**
     * 终止坐标X
     */
    private int end_x;
    /**
     * 终止坐标Y
     */
    private int end_y;
    /**
     * 最大行数
     */
    private int max_line;
    /**
     * 对齐方式【1靠左2居中3靠右】
     */
    private int align;
    /**
     * 文本颜色
     */
    private String color;
    /**
     * 字体名称
     */
    private String font_name;
    /**
     * 字体大小
     */
    private int font_size;

    public AcShareAdContentBean() {
    }

    public int getContent_id() {
        return content_id;
    }

    public void setContent_id(int content_id) {
        this.content_id = content_id;
    }

    public int getAd_id() {
        return ad_id;
    }

    public void setAd_id(int ad_id) {
        this.ad_id = ad_id;
    }

    public int getContent_type() {
        return content_type;
    }

    public void setContent_type(int content_type) {
        this.content_type = content_type;
    }

    public String getContent_param() {
        return content_param;
    }

    public void setContent_param(String content_param) {
        this.content_param = content_param;
    }

    public int getStart_x() {
        return start_x;
    }

    public void setStart_x(int start_x) {
        this.start_x = start_x;
    }

    public int getStart_y() {
        return start_y;
    }

    public void setStart_y(int start_y) {
        this.start_y = start_y;
    }

    public int getEnd_x() {
        return end_x;
    }

    public void setEnd_x(int end_x) {
        this.end_x = end_x;
    }

    public int getEnd_y() {
        return end_y;
    }

    public void setEnd_y(int end_y) {
        this.end_y = end_y;
    }

    public int getMax_line() {
        return max_line;
    }

    public void setMax_line(int max_line) {
        this.max_line = max_line;
    }

    public int getAlign() {
        return align;
    }

    public void setAlign(int align) {
        this.align = align;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFont_name() {
        return font_name;
    }

    public void setFont_name(String font_name) {
        this.font_name = font_name;
    }

    public int getFont_size() {
        return font_size;
    }

    public void setFont_size(int font_size) {
        this.font_size = font_size;
    }
}
