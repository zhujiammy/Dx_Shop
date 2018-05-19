package com.example.zhujia.dx_shop.Data;

public class AttrKey
{
    private String id;

    private String catalogId;

    private String catalogAttrValue;

    private int indexNum;

    private String isColorAttr;

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
    public void setCatalogAttrValue(String catalogAttrValue){
        this.catalogAttrValue = catalogAttrValue;
    }
    public String getCatalogAttrValue(){
        return this.catalogAttrValue;
    }
    public void setIndexNum(int indexNum){
        this.indexNum = indexNum;
    }
    public int getIndexNum(){
        return this.indexNum;
    }
    public void setIsColorAttr(String isColorAttr){
        this.isColorAttr = isColorAttr;
    }
    public String getIsColorAttr(){
        return this.isColorAttr;
    }
}
