package com.example.luisa.popularmovies.core;

import android.text.TextUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Created by LuisA on 8/28/2015.
 */
public class BaseBusinessObject extends DataAccessObject{

    public BaseBusinessObject() {

    }

    public BaseBusinessObject(long id) {
        this.id = id;
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