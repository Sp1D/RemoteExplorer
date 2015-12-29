package com.sp1d.remoteexplorer.json;

/**
 *  Класс предназначен для формирования JSON сообщения
 * 
 * @author sp1d
 */
public class File {

    String name;
    String date;
    String size;
    String perm;
    String icon;

    public File() {
    }
    
    public File addName(String name){
        this.name = name;
        return this;
    }
    
    public File addDate(String date){
        this.date = date;
        return this;
    }
    
    public File addSize(String size){
        this.size = size;
        return this;
    }
    
    public File addPerm(String perm){
        this.perm = perm;
        return this;
    }
    
    public File addIcon(String icon){
        this.icon = icon;
        return this;
    }

}
