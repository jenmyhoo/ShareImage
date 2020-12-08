package com.xiuchezai.search.bean;

import java.io.Serializable;

/**
 * @author hoo
 * @date 2020-07-02 15:09
 */
public class AcShareAdCateBean implements Serializable {
    private static final long serialVersionUID = -2232377616832552958L;
    /**
     * 海报标识
     */
    private int ad_id;
    /**
     * 海报名称
     */
    private String ad_name;
    /**
     * 海报模板URL
     */
    private String ad_url;

    public AcShareAdCateBean() {
    }

    public int getAd_id() {
        return ad_id;
    }

    public void setAd_id(int ad_id) {
        this.ad_id = ad_id;
    }

    public String getAd_name() {
        return ad_name;
    }

    public void setAd_name(String ad_name) {
        this.ad_name = ad_name;
    }

    public String getAd_url() {
        return ad_url;
    }

    public void setAd_url(String ad_url) {
        this.ad_url = ad_url;
    }
}
