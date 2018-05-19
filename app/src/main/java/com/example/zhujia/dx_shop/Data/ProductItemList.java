package com.example.zhujia.dx_shop.Data;

public class ProductItemList
{
    private String id;

    private String productId;

    private String itemNo;

    private String itemName;

    private String itemTitle;

    private String status;

    private String customNo1;

    private String customNo2;

    private int createTime;

    private int lastUpdate;

    private String productModelAttrs;

    private String isDeleted;

    private double salePrice;

    private String listImg;

    private String originImg;

    private String smallImg;

    public void setId(String id){
        this.id = id;
    }
    public String getId(){
        return this.id;
    }
    public void setProductId(String productId){
        this.productId = productId;
    }
    public String getProductId(){
        return this.productId;
    }
    public void setItemNo(String itemNo){
        this.itemNo = itemNo;
    }
    public String getItemNo(){
        return this.itemNo;
    }
    public void setItemName(String itemName){
        this.itemName = itemName;
    }
    public String getItemName(){
        return this.itemName;
    }
    public void setItemTitle(String itemTitle){
        this.itemTitle = itemTitle;
    }
    public String getItemTitle(){
        return this.itemTitle;
    }
    public void setStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return this.status;
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
    public void setProductModelAttrs(String productModelAttrs){
        this.productModelAttrs = productModelAttrs;
    }
    public String getProductModelAttrs(){
        return this.productModelAttrs;
    }
    public void setIsDeleted(String isDeleted){
        this.isDeleted = isDeleted;
    }
    public String getIsDeleted(){
        return this.isDeleted;
    }
    public void setSalePrice(double salePrice){
        this.salePrice = salePrice;
    }
    public double getSalePrice(){
        return this.salePrice;
    }
    public void setListImg(String listImg){
        this.listImg = listImg;
    }
    public String getListImg(){
        return this.listImg;
    }
    public void setOriginImg(String originImg){
        this.originImg = originImg;
    }
    public String getOriginImg(){
        return this.originImg;
    }
    public void setSmallImg(String smallImg){
        this.smallImg = smallImg;
    }
    public String getSmallImg(){
        return this.smallImg;
    }
}

