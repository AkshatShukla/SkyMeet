package com.rcoem.project.skymeet;

/**
 * Created by dhananjay on 12-03-2017.
 */

public class BlogModel {
    private String Name;
    private String msg;

    public BlogModel(){

    }


    public BlogModel(String Name,String msg){

        this.Name = Name;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
