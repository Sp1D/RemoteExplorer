var selectedItem;
var selectedPath;
var pane;
var rightPath;
var leftPath;

var tasks = 0;



function check() {
    $.getJSON('tasks', function (data) {
        if (data.count < tasks) {
            getContent('left');
            getContent('right');
        }
        tasks = data.count;
        $('#badgeTasks').text(tasks);
    });
    if (tasks > 0) {
        setTimeout(check, 500);
        $('#badgeTasks').addClass('inwork');
    } else {
        $('#badgeTasks').removeClass('inwork');
    }
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
        var html;
        var separator = data.separator;

//      It is link to parent dir ("..") 
        if (file.size.toString() === '&lt;PARENT&gt;') {
            fileString = '<a href="#" onclick="changedir(&apos;..&apos;,&apos;' + pane + '&apos;)">..</a>';
            html = '<tr class="item" onclick="clickchoose(this,&apos;' + pane + '&apos;)"><td colspan="4">' + fileString + '</td>' +
                    '</tr>';

        } else {

//      It is directory. Will show with <a> link
            if (file.size.toString() === '&lt;DIR&gt;') {
                fileString = '&nbsp;<a href="#" onclick="changedir(\'' + escape(file.name) + '\',\'' + pane + '\')">' + file.name + '</a>';
            }
//        Or regular FILE
            else {
                fileString = '<img src="static/icons/32px/' + file.icon + '" class="icon"/>&nbsp;' + file.name;
            }
            html = '<tr onclick="clickchoose(this,&apos;' + pane + '&apos;)"><td>' + fileString + '</td>' +
                    '<td>' + file.size + '</td>' +
                    '<td>' + file.date + '</td>' +
                    '<td>' + file.perm + '</td></tr>';
        }
        $('#' + pane + 'body').append(html);
    });
}


function getContent(pane) {
    /*
     IE кэширует GET запросы, поэтому cache : false
     Также можно использовать POST, но переделывать структуру приложения
     не хочется
     */
    $.ajax({
        dataType: "json",
        url: contextPath + '/content',
        data: pane,
        cache: false,
        success: parseContent
    });
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

    $.ajax({
        dataType: "json",
        url: '',
        data: data,
        cache: false,
        success: parseContent
    });

}

function select(param) {
    selectedItem = param;
    selectedPath = $(param).children('td.path').contents();
}

function clickchoose(elem, pan) {
    pane = pan;
    $(selectedItem).toggleClass('selected');
    select($(elem));
    $(elem).toggleClass('selected');
}

function closeDialogs() {
    $('.dialog').hide();
    $('#hideAll').hide();
}

function escape(s) {
    var escaped = /(\\x00|\\n|\\r|\\|'|"|\\x1a)/g;
    var str = new String(s);
    str = str.replace(escaped, '\\' + '$&');
    return str;
}

function winResize() {
    var winheight = $(window).height();
    $('.leftpane').css('height', winheight - 75);
    $('.rightpane').css('height', winheight - 75);
}

$(function () {
    winResize();

    getContent('right');
    getContent('left');

    $('#badgeTasks').text(tasks);

    check();



    $(window).resize(winResize);

    $('#btnCopy').click(function () {
        var paneTo;
        if (pane === 'left') {
            paneTo = 'right';
        } else
            paneTo = 'left';
        var req = {
            from: selectedPath.text().toString().trim(),
            to: paneTo
        };
        $.post(contextPath + '/copy', req, function (data) {
            tasks++;
            check(data);
        });

    });

    $('#btnMove').click(function () {
        var paneTo;
        if (pane === 'left') {
            paneTo = 'right';
        } else
            paneTo = 'left';
        var req = {
            from: selectedPath.text().toString().trim(),
            to: paneTo
        };
        $.post(contextPath + '/move', req, function (data) {
            tasks++;
            check(data);
        });


    });

    $('#btnDelete').click(function () {
        var req = {
            from: pane,
            to: selectedPath.text().toString().trim()
        };
        $.post(contextPath + '/delete', req, function (data) {

            tasks++;
            check(data);
        });

    });

    $('#btnCreate').click(function () {
        if (pane !== 'left' && pane !== 'right') {
            alert('Select pane please');
            return;
        }
        $('#dlgCreate').show();
        $('#hideAll').show();

    });

    $('#btnCreateDir').click(function () {
        var req = {
            from: pane,
            to: $('#dirname').val()
        };
        $.post(contextPath + '/create', req, function (data) {
            tasks++;
            check(data);
        });
        $('#dirname').val('');
        $('#dlgCreate').hide();
        $('#hideAll').hide();
    });

    $('.btnCancel').click(function () {
        $('.dialog').hide();
        $('#hideAll').hide();
    });

    $('ul.tasks li').click(function () {
        $('#popupTasks').toggle();
    });
});

