package com.xiuchezai.search.image;

import com.aliyun.imagesearch.Client;
import com.aliyun.imagesearch.models.AddImageAdvanceRequest;
import com.aliyun.imagesearch.models.AddImageResponse;
import com.aliyun.imagesearch.models.Config;
import com.aliyun.imagesearch.models.DeleteImageRequest;
import com.aliyun.imagesearch.models.DeleteImageResponse;
import com.aliyun.imagesearch.models.SearchImageByNameRequest;
import com.aliyun.imagesearch.models.SearchImageByNameResponse;
import com.aliyun.imagesearch.models.SearchImageByPicAdvanceRequest;
import com.aliyun.imagesearch.models.SearchImageByPicResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teautil.models.RuntimeOptions;
import com.google.gson.Gson;
import com.xiuchezai.search.util.ImageUtil;
import com.xiuchezai.search.util.PropertiesUtil;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author hoo
 * @date 2020-06-07 11:03
 */
public class AliyunImageSearchServiceImpl {
    private Config authConfig = new Config();
    private String INSTANCE;

    public static void main(String[] args) throws Exception {
        AliyunImageSearchServiceImpl aliyunImageSearchService = AliyunImageSearchServiceImpl.getInstance();
        ImageSearchBean imageSearchBean = new ImageSearchBean();
//        imageSearchBean.setProductId(2020061000000005L);
//        imageSearchBean.setProductName("NGK 火花塞宝骏系列/五菱系列/长安系列/东风系列/哈飞/金杯/力帆/铃木/威旺/雪佛兰/中华");
//        imageSearchBean.setProductCateId(970000);
//        imageSearchBean.setProductBrandId(561);
        imageSearchBean.setProductDomain("http://img.8673h.com/");
        imageSearchBean.setProductUrl("group2/M00/00/2F/wKgCd177A-aAAu1DAASHHzPojKk688.jpg");
        Gson gson = new Gson();
        //aliyunImageSearchService.add(imageSearchBean);
        List<ImageSearchBean> beanList = aliyunImageSearchService.searchByPic(imageSearchBean);
        //aliyunImageSearchService.searchByName(imageSearchBean);
        //aliyunImageSearchService.delete(imageSearchBean);
        System.out.println(gson.toJson(imageSearchBean));
        System.out.println(gson.toJson(beanList));
    }

    private static class SingletonAliyunImageSearchServiceImpl {
        private static AliyunImageSearchServiceImpl INSTANCE = new AliyunImageSearchServiceImpl();
    }

    public static AliyunImageSearchServiceImpl getInstance() {
        return SingletonAliyunImageSearchServiceImpl.INSTANCE;
    }

    private AliyunImageSearchServiceImpl() {
        ResourceBundle bundle = PropertiesUtil.loadResourceBundle("aliyun");
        INSTANCE = bundle.getString("IMAGE_SEARCH_INSTANCE");
        authConfig.type = "access_key";
        authConfig.accessKeyId = bundle.getString("ACCESS_KEY_ID");
        authConfig.accessKeySecret = bundle.getString("ACCESS_KEY_SECRET");
        authConfig.endpoint = bundle.getString("IMAGE_SEARCH_ENDPOINT");
        authConfig.regionId = bundle.getString("IMAGE_SEARCH_REGIONID");
    }

    public void add(ImageSearchBean imageSearchBean) {
        AddImageAdvanceRequest request = new AddImageAdvanceRequest();
        // 必填，图像搜索实例名称。
        request.instanceName = INSTANCE;
        // 必填，商品id，最多支持 512个字符。
        // 一个商品可有多张图片。
        request.productId = String.valueOf(imageSearchBean.getProductId());
        // 必填，图片名称，最多支持 512个字符。
        // 1. ProductId + PicName唯一确定一张图片。
        // 2. 如果多次添加图片具有相同的ProductId + PicName，以最后一次添加为准，前面添加的的图片将被覆盖。
        request.picName = imageSearchBean.getProductUrl();
        // 选填，图片类目。
        // 1. 对于商品搜索：若设置类目，则以设置的为准；若不设置类目，将由系统进行类目预测，预测的类目结果可在Response中获取 。
        // 2. 对于布料、商标、通用搜索：不论是否设置类目，系统会将类目设置为88888888。
        //request.categoryId = 88888888;
        // 选填，用户自定义的内容，最多支持 4096个字符。
        // 查询时会返回该字段。例如可添加图片的描述等文本。
        request.customContent = imageSearchBean.getProductName();
        // 选填，整数类型属性，可用于查询时过滤，查询时会返回该字段。
        //  例如不同的站点的图片/不同用户的图片，可以设置不同的intAttr，查询时通过过滤来达到隔离的目的
        request.intAttr = imageSearchBean.getProductCateId();
        // 选填，字符串类型属性，最多支持 128个字符。可用于查询时过滤，查询时会返回该字段。
        request.strAttr = String.valueOf(imageSearchBean.getProductBrandId());
        // 选填，是否需要进行主体识别，默认为true。
        // 1. 为true时，由系统进行主体识别，以识别的主体进行搜索，主体识别结果可在Response中获取。
        // 2. 为false时，则不进行主体识别，以整张图进行搜索。
        // 3.对于布料图片搜索，此参数会被忽略，系统会以整张图进行搜索。
        request.crop = true;
        // 选填，图片的主体区域，格式为 x1,x2,y1,y2, 其中 x1,y1 是左上角的点，x2，y2是右下角的点。
        // 若用户设置了Region，则不论Crop参数为何值，都将以用户输入Region进行搜索。
        // 对于布料图片搜索，此参数会被忽略，系统会以整张图进行搜索。
        //request.region = "167,467,220,407";
        InputStream inputStream = null;
        try {
            // 必填，图片内容
            // 最多支持 2MB大小图片以及5s的传输等待时间。当前仅支持jpg和png格式图片；
            // 对于商品、商标、通用图片搜索，图片长和宽的像素必须都大于等于200，并且小于等于1024；
            // 对于布料搜索，图片长和宽的像素必须都大于等于448，并且小于等于1024；
            // 图像中不能带有旋转信息。
            byte[] bytes = ImageUtil.getImageFromNetByUrl(imageSearchBean.getProductDomain() + imageSearchBean.getProductUrl());
            inputStream = ImageUtil.readBytes(bytes);
            request.picContentObject = inputStream;
            // 创建RuntimeObject实例并设置运行参数
            RuntimeOptions runtimeOptions = new RuntimeOptions();
            AddImageResponse response = new Client(authConfig).addImageAdvance(request, runtimeOptions);
//            System.out.println("success: " + response.success + ". message: " + response.message
//                    + ". categoryId: " + response.picInfo.categoryId + ". region:" + response.picInfo.region
//                    + ". requestId: " + response.requestId);
            imageSearchBean.setSuccess(response.success);
            imageSearchBean.setCode(response.code.toString());
            imageSearchBean.setMessage(response.message);
            imageSearchBean.setRegion(response.picInfo.region);
        } catch (Exception e) {
            imageSearchBean.setSuccess(false);
            String code = null;
            String message = null;
            if (e instanceof TeaException) {
                TeaException tea = (TeaException) e;
//                System.out.println(tea.getCode());
//                System.out.println(tea.getMessage());
//                System.out.println(tea.getData());
                code = tea.getCode();
                message = tea.getMessage();
            } else {
                code = "500";
                message = e.getMessage();
            }
            imageSearchBean.setCode(code);
            imageSearchBean.setMessage(message);
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<ImageSearchBean> searchByPic(ImageSearchBean imageSearchBean) {
        List<ImageSearchBean> imageSearchBeanList = null;
        SearchImageByPicAdvanceRequest request = new SearchImageByPicAdvanceRequest();
        // 必填，图像搜索实例名称。
        request.instanceName = INSTANCE;
        // 选填，商品类目。
        // 1. 对于商品搜索：若设置类目，则以设置的为准；若不设置类目，将由系统进行类目预测，预测的类目结果可在Response中获取 。
        // 2. 对于布料、商标、通用搜索：不论是否设置类目，系统会将类目设置为88888888。
        //request.categoryId = 88888888;
        // 选填，返回结果的数目。取值范围：1-100。默认值：10。
        request.num = 100;
        // 选填，返回结果的起始位置。取值范围：0-499。默认值：0。
        request.start = 0;
        // 选填，是否需要进行主体识别，默认为true。
        // 1.为true时，由系统进行主体识别，以识别的主体进行搜索，主体识别结果可在Response中获取。
        // 2. 为false时，则不进行主体识别，以整张图进行搜索。
        // 3.对于布料图片搜索，此参数会被忽略，系统会以整张图进行搜索。
        request.crop = true;
        // 选填，图片的主体区域，格式为 x1,x2,y1,y2, 其中 x1,y1 是左上角的点，x2，y2是右下角的点。
        // 若用户设置了Region，则不论Crop参数为何值，都将以用户输入Region进行搜索。
        // 3.对于布料图片搜索，此参数会被忽略，系统会以整张图进行搜索。
        //request.region = "167,467,220,407";
        // 选填，过滤条件
        // int_attr支持的操作符有>、>=、<、<=、=，str_attr支持的操作符有=和!=，多个条件只支持AND和OR进行连接。
        // 示例:
        //  1. 根据IntAttr过滤结果，int_attr>=100
        //  2. 根据StrAttr过滤结果，str_attr!="value1"
        //  3. 根据IntAttr和StrAttr联合过滤结果，int_attr=1000 AND str_attr="value1"
        StringBuilder filter = new StringBuilder("");
        if (imageSearchBean.getProductCateId() != null) {
            if (imageSearchBean.getProductCateId() % 10000 == 0) {
                filter.append("int_attr>=").append(imageSearchBean.getProductCateId());
                filter.append(" AND int_attr<=").append(imageSearchBean.getProductCateId() + 9999);
            } else if (imageSearchBean.getProductCateId() % 100 == 0) {
                filter.append("int_attr>=").append(imageSearchBean.getProductCateId());
                filter.append(" AND int_attr<=").append(imageSearchBean.getProductCateId() + 99);
            } else {
                filter.append("int_attr=").append(imageSearchBean.getProductCateId());
            }
        }
        if (imageSearchBean.getProductBrandId() != null) {
            if (filter.length() > 1) {
                filter.append(" AND ");
            }
            filter.append("str_attr=").append("\"").append(imageSearchBean.getProductBrandId()).append("\"");
        }
        if (filter.length() > 1) {
            request.filter = filter.toString();
        }
        InputStream byteStream = null;
        try {
            // 图片内容，最多支持 2MB大小图片以及5s的传输等待时间。当前仅支持jpg和png格式图片；
            // 对于商品、商标、通用图片搜索，图片长和宽的像素必须都大于等于200，并且小于等于1024；
            // 对于布料搜索，图片长和宽的像素必须都大于等于448，并且小于等于1024；
            // 图像中不能带有旋转信息
            byte[] bytes = ImageUtil.getImageFromNetByUrl(imageSearchBean.getProductDomain() + imageSearchBean.getProductUrl());
            Image image = ImageUtil.getImage(bytes);
            // 得到源图宽
            int width = image.getWidth(null);
            // 得到源图长
            int height = image.getHeight(null);
            if (width > 1024 || height > 1024) {
                byte[] newBytes = ImageUtil.resizeFix(image, 1024, 1024);
                byteStream = ImageUtil.readBytes(newBytes);
            } else {
                byteStream = ImageUtil.readBytes(bytes);
            }
            request.picContentObject = byteStream;
//            String fileName = imageSearchBean.getProductUrl().substring(imageSearchBean.getProductUrl().lastIndexOf("/") + 1);
//            ImageUtil.writeImageToDisk(bytes, "D:/temp/" + fileName);
//            request.picContentObject = new FileInputStream("D:/temp/" + fileName);

            RuntimeOptions runtimeOptions = new RuntimeOptions();
            SearchImageByPicResponse response = new Client(authConfig).searchImageByPicAdvance(request, runtimeOptions);
//            System.out.println(response.requestId);
//            System.out.println(response.picInfo.categoryId);
//            System.out.println(response.picInfo.region);
            imageSearchBean.setSuccess(response.success);
            imageSearchBean.setCode(response.code.toString());
            imageSearchBean.setMessage(response.msg);
            imageSearchBean.setRegion(response.picInfo.region);
            List<SearchImageByPicResponse.SearchImageByPicResponseAuctions> auctions = response.auctions;
            if (auctions != null) {
                imageSearchBeanList = new ArrayList<ImageSearchBean>(auctions.size() * 2);
                for (SearchImageByPicResponse.SearchImageByPicResponseAuctions auction : auctions) {
//                    System.out.println(auction.categoryId + " " + auction.picName + " " + auction.productId + " "
//                            + auction.customContent + " " + auction.sortExprValues + " " + auction.strAttr + " " + auction.intAttr);
                    ImageSearchBean bean = new ImageSearchBean();
                    bean.setProductId(Long.parseLong(auction.productId));
                    bean.setProductName(auction.customContent);
                    bean.setProductCateId(auction.intAttr);
                    bean.setProductBrandId(Integer.valueOf(auction.strAttr));
                    bean.setProductUrl(auction.picName);
                    imageSearchBeanList.add(bean);
                }
            }
        } catch (Exception e) {
            imageSearchBean.setSuccess(false);
            String code = null;
            String message = null;
            if (e instanceof TeaException) {
                TeaException tea = (TeaException) e;
//                System.out.println(tea.getCode());
//                System.out.println(tea.getMessage());
//                System.out.println(tea.getData());
                code = tea.getCode();
                message = tea.getMessage();
            } else {
                code = "500";
                message = e.getMessage();
            }
            imageSearchBean.setCode(code);
            imageSearchBean.setMessage(message);
            e.printStackTrace();
        } finally {
            try {
                if (byteStream != null) {
                    byteStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imageSearchBeanList;
    }

    public List<ImageSearchBean> searchByName(ImageSearchBean imageSearchBean) {
        List<ImageSearchBean> imageSearchBeanList = null;
        SearchImageByNameRequest request = new SearchImageByNameRequest();
        // 必填，图像搜索实例名称。
        request.instanceName = INSTANCE;
        // 必填，商品id，最多支持 512个字符。
        // 一个商品可有多张图片。
        request.productId = String.valueOf(imageSearchBean.getProductId());
        // 必填，图片名称，最多支持 512个字符。
        // 1. ProductId + PicName唯一确定一张图片。
        request.picName = imageSearchBean.getProductUrl();
        // 选填，商品类目。
        // 1. 对于商品搜索：若设置类目，则以设置的为准；若不设置类目，将由系统进行类目预测，预测的类目结果可在Response中获取 。
        // 2. 对于布料、商标、通用搜索：不论是否设置类目，系统会将类目设置为88888888。
        //request.categoryId = 88888888;
        // 选填，返回结果的数目。取值范围：1-100。默认值：10。
        request.num = 10;
        // 选填，返回结果的起始位置。取值范围：0-499。默认值：0。
        request.start = 0;
        // 选填，过滤条件
        // int_attr支持的操作符有>、>=、<、<=、=，str_attr支持的操作符有=和!=，多个条件只支持AND和OR进行连接。
        // 示例:
        //  1. 根据IntAttr过滤结果，int_attr>=100
        //  2. 根据StrAttr过滤结果，str_attr!="value1"
        //  3. 根据IntAttr和StrAttr联合过滤结果，int_attr=1000 AND str_attr="value1"
        StringBuilder filter = new StringBuilder("");
        if (imageSearchBean.getProductCateId() != null) {
            filter.append("int_attr=").append(imageSearchBean.getProductCateId());
        }
        if (imageSearchBean.getProductBrandId() != null) {
            if (filter.length() > 1) {
                filter.append(" AND ");
            }
            filter.append("str_attr=").append("\"").append(imageSearchBean.getProductBrandId()).append("\"");
        }
        if (filter.length() > 1) {
            request.filter = filter.toString();
        }
        try {
            RuntimeOptions runtimeOptions = new RuntimeOptions();
            SearchImageByNameResponse response = new Client(authConfig).searchImageByName(request, runtimeOptions);
//            System.out.println(response.requestId);
//            System.out.println(response.picInfo.categoryId);
//            System.out.println(response.picInfo.region);
            imageSearchBean.setSuccess(response.success);
            imageSearchBean.setCode(response.code.toString());
            imageSearchBean.setMessage(response.msg);
            imageSearchBean.setRegion(response.picInfo.region);
            List<SearchImageByNameResponse.SearchImageByNameResponseAuctions> auctions = response.auctions;
            if (auctions != null) {
                imageSearchBeanList = new ArrayList<ImageSearchBean>(auctions.size() * 2);
                for (SearchImageByNameResponse.SearchImageByNameResponseAuctions auction : auctions) {
//                    System.out.println(auction.categoryId + " " + auction.picName + " " + auction.productId + " "
//                            + auction.customContent + " " + auction.sortExprValues + " " + auction.strAttr + " " + auction.intAttr);
                    ImageSearchBean bean = new ImageSearchBean();
                    bean.setProductId(Long.parseLong(auction.productId));
                    bean.setProductName(auction.customContent);
                    bean.setProductCateId(auction.intAttr);
                    bean.setProductBrandId(Integer.valueOf(auction.strAttr));
                    bean.setProductUrl(auction.picName);
                    imageSearchBeanList.add(bean);
                }
            }
        } catch (Exception e) {
            imageSearchBean.setSuccess(false);
            String code = null;
            String message = null;
            if (e instanceof TeaException) {
                TeaException tea = (TeaException) e;
//                System.out.println(tea.getCode());
//                System.out.println(tea.getMessage());
//                System.out.println(tea.getData());
                code = tea.getCode();
                message = tea.getMessage();
            } else {
                code = "500";
                message = e.getMessage();
            }
            imageSearchBean.setCode(code);
            imageSearchBean.setMessage(message);
            e.printStackTrace();
        }

        return imageSearchBeanList;
    }

    public void delete(ImageSearchBean imageSearchBean) {
        DeleteImageRequest request = new DeleteImageRequest();
        request.instanceName = INSTANCE;
        request.productId = String.valueOf(imageSearchBean.getProductId());
        request.picName = imageSearchBean.getProductUrl();
        RuntimeOptions runtimeOptions = new RuntimeOptions();
        try {
            DeleteImageResponse response = new Client(authConfig).deleteImage(request, runtimeOptions);
//            System.out.println("requestId: " + response.requestId + ". success: " + response.success
//                    + ". message: " + response.message);
            imageSearchBean.setSuccess(response.success);
            imageSearchBean.setCode(response.code.toString());
            imageSearchBean.setMessage(response.message);
        } catch (Exception e) {
            imageSearchBean.setSuccess(false);
            String code = null;
            String message = null;
            if (e instanceof TeaException) {
                TeaException tea = (TeaException) e;
//                System.out.println(tea.getCode());
//                System.out.println(tea.getMessage());
//                System.out.println(tea.getData());
                code = tea.getCode();
                message = tea.getMessage();
            } else {
                code = "500";
                message = e.getMessage();
            }
            imageSearchBean.setCode(code);
            imageSearchBean.setMessage(message);
            e.printStackTrace();
        }
    }
}
