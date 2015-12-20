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

    public FileJSON(String filename) {
        this.name = filename;
        date = "01/01/1970";
        size = "15M";
        perm = "rwxrwxrwx";

    }

}
