/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sp1d.remoteexplorer.json;

/**
 *
 * @author sp1d
 */
public class File {

    String name;
    String date;
    String size;
    String perm;

    public File() {
    }

    
    public File(String filename) {
        this.name = filename;
        date = "01/01/1970";
        size = "15M";
        perm = "rwxrwxrwx";

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

}
