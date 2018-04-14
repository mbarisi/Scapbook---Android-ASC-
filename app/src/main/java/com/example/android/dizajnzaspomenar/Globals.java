package com.example.android.dizajnzaspomenar;

import android.app.Application;

/**
 * Created by Tena on 5/9/2017.
 */

public class Globals extends Application{

    private static Globals instance = null;

    // Global variable
    private boolean log = false;
    private int username_id = -1;
    private String username;
    private int currentPage;
    private boolean cakes = false;

    // Restrict the constructor from being instantiated
    private Globals(){}

    public boolean isLogged()
    { return log; }

    public void setId(int d){
        this.username_id=d;
        this.log = true;
    }

    public int getId(){
        return this.username_id;
    }

    public void setUsername(String s) //to vjerojatno nece ostati kasnije jer nema potrebe pamtiti username?
    //osim ako ga nećemo ispisivati negdje u kutu ili izborniku
    {
        this.username=s;
    }
    public String getUsername(){
        return this.username;
    }

    public static synchronized Globals getInstance(){
        if(instance==null)
        {
            instance=new Globals();
        }
        return instance;
    }

    public void logout()
    {
        this.log = false;
    }

    /*public void setQuestionId(){
        ++this.questionId;
    } */

    public void setCurrentPage(int position){
        this.currentPage = position + 1;
    }

    public boolean getCakes(){
        return this.cakes;
    }

    public void setCakes()
    {
        if ( !this.cakes )
            this.cakes = true;
        else
            this.cakes = false;
    }
}
