package com.example.zhujia.dx_shop.Data;

import java.util.List;

public class Objects
{
    private Product product;

    private List<ProductImageList> productImageList;

    private List<ProductItemList> productItemList;

    private List<ProductAttrList> productAttrList;

    public void setProduct(Product product){
        this.product = product;
    }
    public Product getProduct(){
        return this.product;
    }
    public void setProductImageList(List<ProductImageList> productImageList){
        this.productImageList = productImageList;
    }
    public List<ProductImageList> getProductImageList(){
        return this.productImageList;
    }
    public void setProductItemList(List<ProductItemList> productItemList){
        this.productItemList = productItemList;
    }
    public List<ProductItemList> getProductItemList(){
        return this.productItemList;
    }
    public void setProductAttrList(List<ProductAttrList> productAttrList){
        this.productAttrList = productAttrList;
    }
    public List<ProductAttrList> getProductAttrList(){
        return this.productAttrList;
    }
}

