package com.example.zhujia.dx_shop.Data;

public class AttrValues
{
    private String id;

    private String productModelId;

    private String catalogAttrId;

    private String modelAttrValue;

    private int indexNum;

    private String isDeleted;

    private String listImg;

    private String smallImg;

    private String originImg;

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
    public void setCatalogAttrId(String catalogAttrId){
        this.catalogAttrId = catalogAttrId;
    }
    public String getCatalogAttrId(){
        return this.catalogAttrId;
    }
    public void setModelAttrValue(String modelAttrValue){
        this.modelAttrValue = modelAttrValue;
    }
    public String getModelAttrValue(){
        return this.modelAttrValue;
    }
    public void setIndexNum(int indexNum){
        this.indexNum = indexNum;
    }
    public int getIndexNum(){
        return this.indexNum;
    }
    public void setIsDeleted(String isDeleted){
        this.isDeleted = isDeleted;
    }
    public String getIsDeleted(){
        return this.isDeleted;
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
}
