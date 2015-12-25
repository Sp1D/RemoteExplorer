<%-- 
    Document   : index
    Created on : Dec 16, 2015, 1:11:39 PM
    Author     : sp1d
--%>


<%@page import="com.sp1d.remoteexplorer.AppService"%>
<%@page trimDirectiveWhitespaces="true" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    String contextPath = request.getContextPath();
    
%>

<!DOCTYPE html>
<html>
    <head>
        <script>
            var contextPath = '<%= contextPath%>';            
        </script>           
        <script src="<%= contextPath%>/static/js/jquery-2.1.4.min.js"></script>        
        <script src="<%= contextPath%>/static/js/remoteexplorer.js"></script>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="<%= contextPath%>/static/css/local.css" rel="stylesheet">                     
        <title>Remote Explorer</title>
    </head>
    <body>
        <div class="container-fluid">
            <div class="leftpane">
                <table class="panetable">
                    <thead>
                        <tr><th colspan="4" id="leftpath">/fakepath</th></tr>
                        <tr>
                            <th>Filename</th>
                            <th>Size</th>
                            <th>Datetime</th>
                            <th>Permissions</th>
                        </tr>
                    </thead>
                    <tbody id="leftbody">
                        <tr>
                            <td>Filename</td>
                            <td>Size</td>
                            <td>Datetime</td>
                            <td>Permissions</td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <div class="rightpane">
                <table class="table table-condensed">
                    <thead>
                        <tr><th colspan="4" id="rightpath">/fakepath</th></tr>
                        <tr>
                            <th>Filename</th>
                            <th>Size</th>
                            <th>Datetime</th>
                            <th>Permissions</th>
                        </tr>
                    </thead>
                    <tbody id="rightbody">

                    </tbody>
                </table>
            </div>

        </div>   

        <!-- Small modal -->

        <div class="modal" id="newdirmodal" tabindex="-1" role="dialog">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        Create directory
                    </div>
                    <div class="modal-body">
                        <label for="basic-url">Enter directory name</label>
                        <div class="input-group">
                            <span class="input-group-addon" id="newdircurrentpath">currentpath/</span>
                            <input type="text" class="form-control" id="dirname">
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button id="createdirbutton" type="button"  class="btn btn-default" data-dismiss="modal">Create</button>                        
                    </div>
                </div>
            </div>
        </div>

        <nav class="navbar navbar-default navbar-fixed-bottom">
            <div class="container-fluid">
                <button id="btncopy" type="button" class="btn btn-default navbar-btn">Copy</button>
                <button id="btnmove" type="button" class="btn btn-default navbar-btn" >Move</button>
                <button id="btncreate" type="button" class="btn btn-default navbar-btn">Create</button>
                <button id="btndelete" type="button" class="btn btn-default navbar-btn">Delete</button>
                <!--<p class="navbar-text" id="test"></p>-->
                <p class="navbar-text navbar-right"><span id="tasksBadge" class="badge">666</span>&nbsp;Current tasks&nbsp;&nbsp;&nbsp;</p>                
            </div>
        </nav>

    </body>
</html>
