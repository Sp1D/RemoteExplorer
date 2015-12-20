var selectedItem;
var selectedPath;
var pane;

var tasks = 0;
var interval;

function check() {
    $.getJSON('tasks', function (data) {
        tasks = data.tasks;
        $('#tasksBadge').text(tasks);
        location.reload(true);
    });
    if (tasks === 0) {
        keeper(0);
    }
}

function keeper(count) {
    if (count === 0) {
        clearInterval(interval);
    } else {
        interval = setInterval(check, 1000);
//                    check;
    }
}

function select(param) {
    selectedItem = param;
    selectedPath = $(param).children('td.path').contents();
    $('#test').text(pane + ':' + selectedPath.text());
}

function getContent(pane) {
    $.getJSON(contextPath + '/content', pane, function (data) {
        $('#' + pane + 'body').html("");
        $.each(data, function (k, file) {
            var fileString;
            if (file.name.toString() === '..') {
                fileString = '<a href="#" onclick="changedir(this,&apos;' + pane + '&apos;)">' + rootPath + '</a>';
                var html = '<tr class="item" onclick="clickchoose(this,&apos;' + pane + '&apos;)"><td class="path" colspan="4">' + fileString + '</td>' +
                        '</tr>';
                $('#' + pane + 'body').append(html);
            } else {                
                if (file.size.toString() === '&lt;DIR&gt;') {
                    fileString = '<a href="#" onclick="changedir(this,&apos;' + pane + '&apos;)">' + file.name + '</a>';
                } else
                    fileString = file.name;
                var html = '<tr class="item" onclick="clickchoose(this,&apos;' + pane + '&apos;)"><td class="path">' + fileString + '</td>' +
                        '<td>' + file.size + '</td>' +
                        '<td>' + file.date + '</td>' +
                        '<td>' + file.perm + '</td></tr>';
                $('#' + pane + 'body').append(html);

            }
        });
    });
}

function changedir(elem, pan) {
    var data = pan === 'left' ? {left: $(elem).text()} : {right: $(elem).text()};
    $.get('', data);
    getContent(pan);
}

function clickchoose(elem, pan) {
    pane = pan;
    $(selectedItem).toggleClass('selected');
    select($(elem));
    $(elem).toggleClass('selected');
}

$(function () {
    getContent('right');
    getContent('left');


    $('#tasksBadge').text(tasks);

    $('.leftpane tr.item').click(function () {
        pane = 'left';
        $(selectedItem).toggleClass('selected');
        select($(this));
        $(this).toggleClass('selected');
    });



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
//                        var t = $.parseJSON(data);
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
        $.getJSON(contextPath + '/content', function (data) {
            $.each(data, function (k, file) {
                var html = '<tr><td>' + file.filename + '</td>' +
                        '<td>' + file.size + '</td></tr>';
                $('#test').append(html);
            });
        });

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
});

