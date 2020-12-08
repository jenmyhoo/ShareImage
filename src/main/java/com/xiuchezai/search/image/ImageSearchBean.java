package com.xiuchezai.search.image;

/**
 * @author hoo
 * @date 2020-06-08 10:44
 */
public class ImageSearchBean<T> {
    private Boolean success;
    private String code;
    private String message;
    private long productId;
    private String productName;
    private Integer productCateId;
    private Integer productBrandId;
    private String productDomain;
    /**
     * 不包含域名部分
     */
    private String productUrl;
    private String region;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getProductCateId() {
        return productCateId;
    }

    public void setProductCateId(Integer productCateId) {
        this.productCateId = productCateId;
    }

    public Integer getProductBrandId() {
        return productBrandId;
    }

    public void setProductBrandId(Integer productBrandId) {
        this.productBrandId = productBrandId;
    }

    public String getProductDomain() {
        return productDomain;
    }

    public void setProductDomain(String productDomain) {
        this.productDomain = productDomain;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
