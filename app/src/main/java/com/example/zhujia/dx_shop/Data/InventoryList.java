package com.example.zhujia.dx_shop.Data;

public class InventoryList {
    private String lockQuantity;
    private String dmsItemNo;
    private String quantity;
    private String warehouse;
    private String productItemId;


    public String getLockQuantity() {
        return lockQuantity;
    }

    public void setLockQuantity(String lockQuantity) {
        this.lockQuantity = lockQuantity;
    }

    public String getDmsItemNo() {
        return dmsItemNo;
    }

    public void setDmsItemNo(String dmsItemNo) {
        this.dmsItemNo = dmsItemNo;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public String getProductItemId() {
        return productItemId;
    }

    public void setProductItemId(String productItemId) {
        this.productItemId = productItemId;
    }
}
