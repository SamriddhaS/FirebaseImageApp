package com.example.samriddha.firebaseauthentication;

import com.google.firebase.database.Exclude;

public class Upload {

    private String mName;
    private String mImageUrl ;
    private String mKey ;

    public Upload(){


    }
    public Upload(String name,String imageurl){

        if(name.trim().equals("")){
            name = "No Name";
        }

        mName = name ;
        mImageUrl= imageurl;
        }

    public String getName(){
        return mName;
    }

    public void setName(String name){
        mName = name;
    }

    public String getImageUrl(){
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl){
        mImageUrl = imageUrl ;
    }


    @Exclude
    public String getmKey() {
        return mKey;
    }

    @Exclude
    public void setmKey(String mKey) {
        this.mKey = mKey;
    }

}