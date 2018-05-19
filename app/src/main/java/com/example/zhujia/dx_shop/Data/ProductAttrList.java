package com.example.zhujia.dx_shop.Data;

import java.util.List;

public class ProductAttrList
{
    private AttrKey attrKey;

    private List<AttrValues> attrValues;

    public void setAttrKey(AttrKey attrKey){
        this.attrKey = attrKey;
    }
    public AttrKey getAttrKey(){
        return this.attrKey;
    }
    public void setAttrValues(List<AttrValues> attrValues){
        this.attrValues = attrValues;
    }
    public List<AttrValues> getAttrValues(){
        return this.attrValues;
    }
}
