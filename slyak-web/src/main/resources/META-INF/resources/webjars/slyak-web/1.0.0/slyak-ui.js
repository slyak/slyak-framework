$(function () {
    $("a.confirm").on("click", function (e) {
        if (confirm("确定要执行此操作?")) {
            return true;
        } else {
            e.preventDefault();
            e.stopPropagation();
            return false;
        }
    });

    $("a.ajax").on("click", function (e) {
        var link = $(this);
        e.preventDefault();
        e.stopPropagation();
        $.get(link.attr("href"), function (ret) {
            var cb = link.attr("data-cb");
            if (cb) {
                if (window[cb]) {
                    window[cb](ret);
                } else {
                    var retMsg = eval('('+cb+')');
                    alert(retMsg[ret]);
                }
            } else {
                location.reload();
            }
        });
    });
});