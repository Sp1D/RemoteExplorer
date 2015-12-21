var selectedItem;
var selectedPath;
var pane;
var rightPath;
var leftPath;

var tasks = 0;
var interval;
var keeperRunning = false;

function check() {
    $.getJSON('tasks', function (data) {
        if (data.tasks < tasks) {
            getContent('left');
            getContent('right');
        }
        tasks = data.tasks;
        $('#tasksBadge').text(tasks);
        
    });
    if (tasks === 0) {
        keeper(0);
    }
}

function keeper(count) {
    tasks = count;
    if (count === 0) {
        clearInterval(interval);
        keeperRunning = false;
    } else if (!keeperRunning) {
        check;
        interval = setInterval(check, 1000);
        keeperRunning = true;
        
    }
}

function select(param) {
    selectedItem = param;
    selectedPath = $(param).children('td.path').contents();
//    $('#test').text(pane + ':' + selectedPath.text());
}


function parseContent(data, status, xhr) {
    var pane = data.pane;

    $('#' + pane + 'body').html("");

//      Setting pane titles and pane paths variables
    if (pane === 'left') {
        $('#leftpath').text(data.leftPath);
        leftPath = data.leftPath;
    } else if (pane === 'right') {
        $('#rightpath').text(data.rightPath);
        rightPath = data.rightPath;
    }

    $.each(data.list, function (k, file) {
        var fileString;

//      It is link to parent dir ("..") His full path came from server
//      so we need to correct it and create one line <tr> section without any supplementary options
        if (file.size.toString() === '&lt;PARENT&gt;') {
            var pathString;
            pathString = file.name;
            pathString = pathString.replace(rootPath + '/', '');


            fileString = '<a href="#" onclick="changedir(&apos;' + pathString + '&apos;,&apos;' + pane + '&apos;)">..</a>';

//                Write new content
            var html = '<tr class="item" onclick="clickchoose(this,&apos;' + pane + '&apos;)"><td class="path" colspan="4">' + fileString + '</td>' +
                    '</tr>';
            $('#' + pane + 'body').append(html);


        } else {
            
//      It is regular file or directory link. Server gives us only filename (for perfomance issue)
//      so we need to correct it to create relative link such "tmp/1/2" without secured root dir
            var pathString;
            if (pane === 'left') {
                pathString = leftPath + '/' + file.name;
                pathString = pathString.replace(rootPath + '/', '');
            } else if (pane === 'right') {
                pathString = rightPath + '/' + file.name;
                pathString = pathString.replace(rootPath + '/', '');
            }
//      It is directory. Will show with <a> link
            if (file.size.toString() === '&lt;DIR&gt;') {
                fileString = '<a href="#" onclick="changedir(&apos;' + pathString + '&apos;,&apos;' + pane + '&apos;)">' + file.name + '</a>';
            }
//        Or regular FILE
            else
                fileString = file.name;

//                Write new content                
            var html = '<tr class="item" onclick="clickchoose(this,&apos;' + pane + '&apos;)"><td class="path">' + fileString + '</td>' +
                    '<td>' + file.size + '</td>' +
                    '<td>' + file.date + '</td>' +
                    '<td>' + file.perm + '</td></tr>';
            $('#' + pane + 'body').append(html);

        }
    });
}


function getContent(pane) {
    $.getJSON(contextPath + '/content', pane, parseContent);
}

function changedir(path, pan) {
    var data;
    var leftString = '';
    var rightString = '';

    if (pan === 'left') {
        data = {left: path};
    } else if (pan === 'right') {
        data = {right: path};
    }

    $.getJSON('', data, parseContent);

}

function clickchoose(elem, pan) {
    pane = pan;
    $(selectedItem).toggleClass('selected');
    select($(elem));
    $(elem).toggleClass('selected');
}

function createDir(path){
        var req = {
            from: pane,
            to: path
        };
        $.post(contextPath + '/create', req, function (data) {            
            keeper(data.tasks);
        });
    }

$(function () {
    getContent('right');
    getContent('left');

    $('#tasksBadge').text(tasks);

    if (tasks > 0) {
        keeper(tasks);
    }
    
    $('#createdirbutton').attr('onclick','createDir($(\'#dirname\').val())');

    $('#btncopy').click(function () {
        var paneTo;
        if (pane === 'left') {
            paneTo = 'right';
        } else
            paneTo = 'left';
        var req = {
            from: selectedPath.text(),
            to: paneTo
        };
        $.post(contextPath + '/copy', req, function (data) {            
            keeper(data.tasks);
        });

    });

    $('#btnmove').click(function () {
//                    var paneTo;
//                    if (pane === 'left') {
//                        paneTo = 'right'
//                    } else
//                        paneTo = 'left';
//                    var req = {
//                        from: selectedPath.text(),
//                        to: paneTo
//                    };
//                    $.post('<%= contextPath%>/move', req, function (data) {
//                        alert(data.toString());
//                    });
//                    
        

    });

    $('#btndelete').click(function () {
        var req = {
            from: pane,
            to: selectedPath.text()
        };
        $.post(contextPath + '/delete', req, function (data) {            
            keeper(data.tasks);
        });
//                   
    });
    
    
    
    $('#btncreate').click(function (){
        var currentPath;
        if (pane !== 'left' && pane !== 'right') {
            alert('Select pane please');
            return;
        }
        if (pane === 'left') {
            currentPath = leftPath + '/';            
        } else if (pane === 'right') {
            currentPath = rightPath + '/';            
        }
        $('#newdircurrentpath').text(currentPath);
        $('#newdirmodal').modal('show');
    });
});

