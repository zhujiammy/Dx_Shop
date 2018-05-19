package com.example.zhujia.dx_shop.Data;

public class ProductImageList
{
    private String id;

    private String productModelId;

    private String listImg;

    private String smallImg;

    private String originImg;

    private int indexNum;

    public void setId(String id){
        this.id = id;
    }
    public String getId(){
        return this.id;
    }
    public void setProductModelId(String productModelId){
        this.productModelId = productModelId;
    }
    public String getProductModelId(){
        return this.productModelId;
    }
    public void setListImg(String listImg){
        this.listImg = listImg;
    }
    public String getListImg(){
        return this.listImg;
    }
    public void setSmallImg(String smallImg){
        this.smallImg = smallImg;
    }
    public String getSmallImg(){
        return this.smallImg;
    }
    public void setOriginImg(String originImg){
        this.originImg = originImg;
    }
    public String getOriginImg(){
        return this.originImg;
    }
    public void setIndexNum(int indexNum){
        this.indexNum = indexNum;
    }
    public int getIndexNum(){
        return this.indexNum;
    }
}
