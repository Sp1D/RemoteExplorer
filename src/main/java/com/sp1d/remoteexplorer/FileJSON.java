/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sp1d.remoteexplorer;

/**
 *
 * @author sp1d
 */
public class FileJSON {

    String name;
    String date;
    String size;
    String perm;

    public FileJSON() {
    }

    
    public FileJSON(String filename) {
        this.name = filename;
        date = "01/01/1970";
        size = "15M";
        perm = "rwxrwxrwx";

    }
    
    public FileJSON addName(String name){
        this.name = name;
        return this;
    }
    
    public FileJSON addDate(String date){
        this.date = date;
        return this;
    }
    
    public FileJSON addSize(String size){
        this.size = size;
        return this;
    }
    
    public FileJSON addPerm(String perm){
        this.perm = perm;
        return this;
    }

}
