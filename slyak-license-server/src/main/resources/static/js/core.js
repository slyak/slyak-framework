function openFrameLayer(href, title) {
    href = href + (href.indexOf('?') > 0 ? "&v=" : "?v=") + Math.random();
    layer.open({
        type: 2,
        title: title,
        content: [href, 'no'],
        offset: ['60px'],
        area: ['800px'],
        btn: ['确认', '取消'],
        success: function (layero, index) {
            layer.iframeAuto(index);
            var body = layer.getChildFrame('body', index);
            var f = body.find("form");
            f.append("<button type=\"submit\" lay-submit style=\"display: none\">提交</button>");
        },
        yes: function (index, layero) {
            var body = layer.getChildFrame('body', index);
            var f = body.find("form");
            f.find("button[type='submit']").click();
            if (!f.find(".layui-form-danger").length) {
                layer.close(index);
                setTimeout(function () {
                    location.reload()
                }, 500)
            }
        }
    })
}

$.ajaxSetup({
    error: function (jqXHR, textStatus, errorMsg) {
        switch (jqXHR.status) {
            case(401 || 403):
                layer.closeAll();
                layer.alert("没有权限");
                break;
        }
    }
});

$(function () {
    $.ajaxSetup({cache: false});

    $('.input-date').each(function () {
        var el = $(this);
        var option = el.data('option') || {};
        option.elem = this;
        layui.laydate.render(option);
    });

    $(".frame-layer").click(function () {
        var _this = $(this);
        var href = _this.data("frame");
        openFrameLayer(href, _this.text())
    });

    $(".content-layer").click(function () {
        var _this = $(this);
        var selector = $(this).attr("data-content");
        var content = $(selector);
        var title = _this.text();
        layer.open({
            type: 1,
            title: title,
            area: ['600px', '450px'],
            content: content.html()
        })
    });


    var postReload = function (item) {
        var href = item.attr("href");
        var redirect = item.data("redirect") || location.href;
        layer.load(1);
        $.post(href, function (result) {
            layer.closeAll();
            layer.msg(result.message);
            setTimeout(function () {
                location.href = redirect;
            }, 1000);
        });
    };

    $(".confirm").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        var _this = $(this);
        var confirm = _this.data("confirm") || "确定要进行此操作?";
        layer.confirm(confirm, function () {
            postReload(_this);
        })
    });

    $(".ajax").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        postReload($(this));
    });

    function toSelect2Item(result) {
        return result.data.map(function (value) {
            return {
                "id": value.code,
                "text": value.title
            }
        });
    }

    $(".select2").each(function () {
        var __this = $(this);
        var code = __this.data("code");
        var editable = __this.data("editable") !== false;
        var args = {
            ajax: {
                url: "/api/dict/search",
                dataType: 'json',
                data: function (params) {
                    return {
                        code: code,
                        key: params.term
                    };
                },
                processResults: function (result) {
                    return {
                        results: toSelect2Item(result)
                    };
                }
            },
            allowClear: true,
            language: 'zh-CN',
            placeholder: "请选择",
            placeholderOption: "first"
        };
        var dataValue = __this.data("value");
        if (dataValue) {
            var multi = __this.attr("multiple");
            $.get("/api/dict/searchByCodes", {
                code: code,
                itemCodes: multi ? eval(dataValue).join(",") : dataValue
            }, function (result) {
                var transform = toSelect2Item(result);
                var options = $.extend(args, {data: transform});
                var sel2 = __this.select2(options);
                sel2.val(transform.map(function (d) {
                    return d.id;
                })).trigger("change");
                if (!editable) {
                    __this.attr("disabled", true);
                }
            });
        } else {
            __this.select2(args);
            if (!editable) {
                __this.attr("disabled", true);
            }
        }
    });

    layui.use('form', function () {
        var form = layui.form;
        form.verify({
            intGt0: [/^[1-9]\d*$/, '必须为大于0的正整数'],
            intGte0: [/^[0-9]\d*$/, '必须为自然数']
        })
    });
});