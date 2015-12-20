<%-- 
    Document   : index
    Created on : Dec 16, 2015, 1:11:39 PM
    Author     : sp1d
--%>

<%@page import="com.sp1d.remoteexplorer.Attributes"%>
<%@page import="com.sp1d.remoteexplorer.AppService"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="java.util.concurrent.ExecutorCompletionService"%>
<%@page import="java.util.concurrent.Future"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.concurrent.Callable"%>
<%@page import="java.util.concurrent.ExecutorService"%>
<%@page import="java.util.concurrent.Executors"%>
<%@page import="java.nio.file.FileAlreadyExistsException"%>
<%@page import="java.nio.file.StandardCopyOption"%>
<%@page import="java.nio.file.CopyOption"%>
<%@page import="java.util.Map"%>
<%--<%@page import="javax.enterprise.context.spi.Context"%>--%>
<%@page import="java.nio.file.InvalidPathException"%>
<%@page import="java.nio.file.PathMatcher"%>
<%@page import="java.nio.file.FileSystems"%>
<%@page import="java.nio.file.attribute.PosixFilePermissions"%>
<%@page import="java.nio.file.attribute.BasicFileAttributes"%>
<%@page import="java.io.IOException"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.time.ZoneId"%>
<%@page import="java.time.LocalDateTime"%>
<%@page import="java.nio.file.Files"%>
<%@page import="java.nio.file.Path"%>
<%@page import="java.nio.file.Paths"%>
<%@page trimDirectiveWhitespaces="true" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%!
    final Path pRoot = Paths.get("/tmp");
    final String PATH_PARAM = "path";
    final PathMatcher pm = FileSystems.getDefault().getPathMatcher("glob:" + pRoot.toFile().getAbsolutePath() + "/**");

    enum Info {

        FILENAME, SIZE, DATE, ATTRIBUTES, PARENT
    }

    enum Pane {

        LEFT, RIGHT
    }

//  Path formatting
    String pf(Path path, Info info) {
        return pf(path, info, null, null);
    }

//  Path formatting
//  HttpServletRequest and Pane only needed for url resolution
    String pf(Path path, Info info, HttpServletRequest request, Pane pane) {

        try {
            BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);

            switch (info) {
                case FILENAME:
                    if (!attr.isDirectory() || request == null || pane == null) {
                        return path.getName(path.getNameCount() - 1).normalize().toString();

                    } else {
                        return "<a href=\""
                                + request.getContextPath() + "?" + pane.toString().toLowerCase() + "=" + path.normalize().toString().replaceFirst(pRoot.toString() + "/", "")
                                + "\">" + path.getName(path.getNameCount() - 1).normalize().toString() + "</a>";
                    }
                case PARENT:
                    if (!attr.isDirectory() || request == null || pane == null) {
                        return "#";

                    } else {
                        return "<a href=\""
                                + request.getContextPath() + "?" + pane.toString().toLowerCase() + "=" + path.normalize().toString().replaceFirst(pRoot.toString() + "/", "")
                                + "\">..</a>";
                    }
                case DATE:
                    LocalDateTime ldt = LocalDateTime.ofInstant(attr.lastModifiedTime().toInstant(), ZoneId.systemDefault());
                    return ldt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                case SIZE:
                    return attr.isDirectory() ? "&lt;DIR&gt;" : String.valueOf(attr.size() / 1000) + " Kb";
                case ATTRIBUTES:
                    return PosixFilePermissions.toString(Files.getPosixFilePermissions(path));
                default:
                    return "";
            }
        } catch (IOException e) {
            return "";
        }
    }

    String pLink(Path path, String text) {
        if (!path.toFile().isDirectory()) {
            return path.normalize().toString();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<a");

        return sb.toString();
    }


%>

<% String contextPath = request.getContextPath();%>

<!DOCTYPE html>
<html>
    <head>
        <script>
            var contextPath='<%= contextPath %>';
        </script>           
        <script src="<%= contextPath%>/static/js/jquery-2.1.4.min.js"></script>
        <script src="<%= contextPath%>/static/js/bootstrap.min.js"></script>
        <script src="<%= contextPath%>/static/js/remoteexplorer.js"></script>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">       
        <link href="<%= contextPath%>/static/css/bootstrap.min.css" rel="stylesheet">
        <link href="<%= contextPath%>/static/css/local.css" rel="stylesheet">                     
        <title>Remote Explorer</title>
    </head>
    <body>




        <%
            for (Map.Entry<String, String[]> me : request.getParameterMap().entrySet()) {
                out.write(me.getKey() + " : " + me.getValue()[0]);
                out.newLine();
            }
        %>

        <%
//            Path pLeft = (Path)session.getAttribute(AppService.Pane.LEFT.toString().toLowerCase());            
//            Path pRight = (Path)session.getAttribute(AppService.Pane.RIGHT.toString().toLowerCase());         
//            Path pLeft = AppService.getpLeft();
//            Path pRight = AppService.getpRight();
            Attributes a = (Attributes) session.getAttribute("attributes");


        %>

        <!--        <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
                    <div class="modal-dialog" role="document">
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                                <h4 class="modal-title" id="myModalLabel">Modal title</h4>
                            </div>
                            <div class="modal-body">
                                ...
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                                <button type="button" class="btn btn-primary">Save changes</button>
                            </div>
                        </div>
                    </div>
                </div>
                <script>
                    $('#myModal').modal('show');
                </script>-->

        <% //                        response.sendRedirect(request.getHeader("referer"));
        %>

        <%//            String reqPath = request.getRequestURI().replaceFirst(contextPath + "/", "");
//            Path pCurrent = createSecuredPath(reqPath);
%>

        <div class="container-fluid">
            <div class="leftpane">
                <table class="table table-condensed">
                    <thead>
                        <tr><th colspan="4"><%= a.leftPath.toString()%></th></tr>
                        <tr>
                            <th>Filename</th>
                            <th>Size</th>
                            <th>Datetime</th>
                            <th>Permissions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
// Writing <a link to parent directory

                            if (!a.leftPath.equals(pRoot)) {
                        %>
                        <tr>                            
                            <td><%= pf(a.leftPath.getParent(), Info.PARENT, request, Pane.LEFT)%></td>
                            <td></td>
                            <td></td>
                            <td></td>
                        </tr>
                        <%
                            }

                            for (Path path : a.leftListing) {

                        %>
                        <tr class="item">
                            <td class="path"><%= pf(path, Info.FILENAME, request, Pane.LEFT)%></td>
                            <td><%= pf(path, Info.SIZE)%></td>
                            <td><%= pf(path, Info.DATE)%></td>
                            <td><%= pf(path, Info.ATTRIBUTES)%></td>
                        </tr>
                        <% }%>
                    </tbody>
                </table>
            </div>
            <!--
                        <div class="rightpane">
                            <table class="table table-condensed">
                                <thead>
                                    <tr><th colspan="4"><%= a.rightPath.toString()%></th></tr>
                                    <tr>
                                        <th>Filename</th>
                                        <th>Size</th>
                                        <th>Datetime</th>
                                        <th>Permissions</th>
                                    </tr>
                                </thead>
                                <tbody>
            <%

                if (!a.rightPath.equals(pRoot)) {
            %>
            <tr >
                <td><%= pf(a.rightPath.getParent(), Info.PARENT, request, Pane.RIGHT)%></td>
                <td></td>
                <td></td>
                <td></td>
            </tr>
            <%
                }
                for (Path path : a.rightListing) {
            %>
            <tr class="item">
                <td class="path"><%= pf(path, Info.FILENAME, request, Pane.RIGHT)%></td>
                <td><%= pf(path, Info.SIZE)%></td>
                <td><%= pf(path, Info.DATE)%></td>
                <td><%= pf(path, Info.ATTRIBUTES)%></td>
            </tr>
            <% }%>
        </tbody>
    </table>
</div>
            -->
            <div class="rightpane">
                <table class="table table-condensed">
                    <thead>
                        <tr><th colspan="4"><%= a.rightPath.toString()%></th></tr>
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

        <nav class="navbar navbar-default navbar-fixed-bottom">
            <div class="container-fluid">
                <button id="btncopy" type="button" class="btn btn-default navbar-btn">Copy</button>
                <button id="btnmove" type="button" class="btn btn-default navbar-btn" >Move</button>
                <button id="btncreate" type="button" class="btn btn-default navbar-btn">Create</button>
                <button id="btndelete" type="button" class="btn btn-default navbar-btn">Delete</button>
                <p class="navbar-text" id="test"></p>
                <p class="navbar-text navbar-right"><span id="tasksBadge" class="badge">666</span>&nbsp;Current tasks&nbsp;</p>

            </div>
        </nav>

    </body>
</html>
