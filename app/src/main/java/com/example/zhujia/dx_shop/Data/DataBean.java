package com.example.zhujia.dx_shop.Data;

public class DataBean
{

	private String image;

	private int quantity;

	private double salePrice;

	private int id;

	private String productName;

	private String productAttr;

	private String productType;

	private String ID;

	private String promotionTitle;


	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getPromotionTitle() {
		return promotionTitle;
	}

	public void setPromotionTitle(String promotionTitle) {
		this.promotionTitle = promotionTitle;
	}

	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}

	public void setImage(String image){
		this.image = image;
	}
	public String getImage(){
		return this.image;
	}
	public void setQuantity(int quantity){
		this.quantity = quantity;
	}
	public int getQuantity(){
		return this.quantity;
	}
	public void setSalePrice(double salePrice){
		this.salePrice = salePrice;
	}
	public double getSalePrice(){
		return this.salePrice;
	}
	public void setId(int id){
		this.id = id;
	}
	public int getId(){
		return this.id;
	}
	public void setProductName(String productName){
		this.productName = productName;
	}
	public String getProductName(){
		return this.productName;
	}
	public void setProductAttr(String productAttr){
		this.productAttr = productAttr;
	}
	public String getProductAttr(){
		return this.productAttr;
	}

}
