/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sp1d.remoteexplorer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;

/**
 *
 * @author sp1d
 */
public class Attributes {
    
    public Path leftPath, rightPath;
    public List<Path> leftListing, rightListing;
    public final Path rootPath = Paths.get("/tmp");
    
    
    
    
}
