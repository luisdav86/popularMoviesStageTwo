package com.example.luisa.popularmovies.core;

import java.util.List;

/**
 * Created by LuisA on 8/28/2015.
 */
public class BaseBusinessObject extends DataAccessObject{

    public BaseBusinessObject() {

    }

    public List<? extends BaseBusinessObject> findAll(){
        return DataAccessObject.findAll(this.getClass());
    }

    public String[] getEntityProjection(){
        return null;
    }

    public void loadExtraAttributes() {

    }
}