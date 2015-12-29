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
        <div class="">
            <div class="leftpane">
                <table class="panetable">
                    <thead>
                        <tr><th colspan="4" id="leftpath">/fakepath</th></tr>
                        <tr>
                            <th>Filename</th>
                            <th>Size</th>
                            <th>Datetime</th>
                            <th>Attributes</th>
                        </tr>
                    </thead>
                    <tbody id="leftbody">
                        <tr>
                            <td>Filename</td>
                            <td>Size</td>
                            <td>Datetime</td>
                            <td>Attributes</td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <div class="rightpane">
                <table class="panetable">
                    <thead>
                        <tr><th colspan="4" id="rightpath">/fakepath</th></tr>
                        <tr>
                            <th>Filename</th>
                            <th>Size</th>
                            <th>Datetime</th>
                            <th>Attributes</th>
                        </tr>
                    </thead>
                    <tbody id="rightbody">

                    </tbody>
                </table>
            </div>

        </div>   


        <ul class="taskbar">
            <li id="btnCopy">Copy</li>
            <li id="btnMove">Move</li>
            <li id="btnCreate">Create</li>
            <li id="btnDelete">Delete</li>
            <ul class="tasks">
                <li><span id="badgeTasks">66</span>&nbsp;Tasks</li>
            </ul>
        </ul>

        <div id="popupTasks">
            <div class="popTask">Copy&nbsp;/tmp/fake -> /tmp/anotherfake</div>
            <div class="popTask">Move&nbsp;/tmp/verylongdirectoryname/anotherdirectoryname/nonexistingfile.jpg -> /tmp/newdirectory</div>
            <div class="popTask popTaskFinished">Create&nbsp;/tmp/null</div>            
        </div>

        <div id="dlgCreate" class="dialog dlg-create">
            <div class="dialog-caption">
                Create directory
            </div>
            <div class="dialog-content">
                <form>
                    <label>Directory name:<br>
                        <input type="text" id="dirname"/>
                    </label>                
                </form>
            </div>
            <div class="dialog-buttons">
                <ul>
                    <li id="btnCreateDir">Create</li>
                    <li class="btnCancel">Cancel</li>                                      
                </ul>
            </div>
        </div>     
        <div id="hideAll">

        </div>

    </body>
</html>
