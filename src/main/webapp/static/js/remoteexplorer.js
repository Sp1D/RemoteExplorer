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

            function getContent() {
                $.getJSON(contextPath+'/content', function (data) {
                    $.each(data, function (k, file) {
                        var html = '<tr class="item"><td class="path">' + file.name + '</td>' +
                                '<td>' + file.size + '</td>' +
                                '<td>' + file.date + '</td>' +
                                '<td>' + file.perm + '</td></tr>' ;
                                $('#rightbody').append(html);
                    });
                });
            }

            $(function () {
                getContent();

                $('#tasksBadge').text(tasks);

                $('.leftpane tr.item').click(function () {
                    pane = 'left';
                    $(selectedItem).toggleClass('selected');
                    select($(this));
                    $(this).toggleClass('selected');
                });

                $('.rightpane tr.item').click(function () {
                    pane = 'right';
                    $(selectedItem).toggleClass('selected');
                    select($(this));
                    $(this).toggleClass('selected');
                });

                $('#btncopy').click(function () {
                    var paneTo;
                    if (pane === 'left') {
                        paneTo = 'right'
                    } else
                        paneTo = 'left';
                    var req = {
                        from: selectedPath.text(),
                        to: paneTo
                    };
                    $.post(contextPath+'/copy', req, function (data) {
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
                    $.getJSON(contextPath+'/content', function (data) {
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
                    $.post(contextPath+'/delete', req, function (data) {
                        keeper(data.tasks);
                    });
//                   
                });
            });

