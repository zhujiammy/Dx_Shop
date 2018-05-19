package com.example.zhujia.dx_shop.Data;

public class Product
{
    private String id;

    private String catalogId;

    private String typeId;

    private String seriesId;

    private String brandId;

    private String modelNo;

    private String modelName;

    private String modelTitle;

    private String customNo1;

    private String customNo2;

    private int createTime;

    private int lastUpdate;

    private String status;

    private String originalImg;

    private String modelImg;

    private String smallImg;

    private int salePrice;

    private String video;

    public void setId(String id){
        this.id = id;
    }
    public String getId(){
        return this.id;
    }
    public void setCatalogId(String catalogId){
        this.catalogId = catalogId;
    }
    public String getCatalogId(){
        return this.catalogId;
    }
    public void setTypeId(String typeId){
        this.typeId = typeId;
    }
    public String getTypeId(){
        return this.typeId;
    }
    public void setSeriesId(String seriesId){
        this.seriesId = seriesId;
    }
    public String getSeriesId(){
        return this.seriesId;
    }
    public void setBrandId(String brandId){
        this.brandId = brandId;
    }
    public String getBrandId(){
        return this.brandId;
    }
    public void setModelNo(String modelNo){
        this.modelNo = modelNo;
    }
    public String getModelNo(){
        return this.modelNo;
    }
    public void setModelName(String modelName){
        this.modelName = modelName;
    }
    public String getModelName(){
        return this.modelName;
    }
    public void setModelTitle(String modelTitle){
        this.modelTitle = modelTitle;
    }
    public String getModelTitle(){
        return this.modelTitle;
    }
    public void setCustomNo1(String customNo1){
        this.customNo1 = customNo1;
    }
    public String getCustomNo1(){
        return this.customNo1;
    }
    public void setCustomNo2(String customNo2){
        this.customNo2 = customNo2;
    }
    public String getCustomNo2(){
        return this.customNo2;
    }
    public void setCreateTime(int createTime){
        this.createTime = createTime;
    }
    public int getCreateTime(){
        return this.createTime;
    }
    public void setLastUpdate(int lastUpdate){
        this.lastUpdate = lastUpdate;
    }
    public int getLastUpdate(){
        return this.lastUpdate;
    }
    public void setStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return this.status;
    }
    public void setOriginalImg(String originalImg){
        this.originalImg = originalImg;
    }
    public String getOriginalImg(){
        return this.originalImg;
    }
    public void setModelImg(String modelImg){
        this.modelImg = modelImg;
    }
    public String getModelImg(){
        return this.modelImg;
    }
    public void setSmallImg(String smallImg){
        this.smallImg = smallImg;
    }
    public String getSmallImg(){
        return this.smallImg;
    }
    public void setSalePrice(int salePrice){
        this.salePrice = salePrice;
    }
    public int getSalePrice(){
        return this.salePrice;
    }
    public void setVideo(String video){
        this.video = video;
    }
    public String getVideo(){
        return this.video;
    }
}