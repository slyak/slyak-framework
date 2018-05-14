<#ftl strip_whitespace=true>
<#-- @ftlvariable name="slyakRequestContext" type="com.slyak.web.support.freemarker.SlyakRequestContext" -->

<#--<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-fileinput/4.4.5/js/plugins/piexif.min.js" type="text/javascript"></script>
<!-- sortable.min.js is only needed if you wish to sort / rearrange files in initial preview.
    This must be loaded before fileinput.min.js &ndash;&gt;
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-fileinput/4.4.5/js/plugins/sortable.min.js" type="text/javascript"></script>
<!-- purify.min.js is only needed if you wish to purify HTML content in your preview for
    HTML files. This must be loaded before fileinput.min.js &ndash;&gt;
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-fileinput/4.4.5/js/plugins/purify.min.js" type="text/javascript"></script>-->

<#macro cssAndJs>
    <@slyak.js url=[
    '/webjars/jquery/jquery.min.js',
    '/webjars/popper.js/umd/popper-utils.min.js',
    '/webjars/popper.js/umd/popper.min.js',
    '/webjars/bootstrap/js/bootstrap.min.js'
    ]/>
    <@slyak.css url=[
    '/webjars/bootstrap/css/bootstrap.min.css'
    ]/>
</#macro>
<#--
TODO:
layout content
alters badge breadcrumb buttons button group card carousel collapse dropdowns forms
inputgroup jumbotron listgroup modal navs navbar popovers progress scrollspy tooltips
-->

<#macro breadcrumb data=[{'title':'Home','url':'/'}]>
<nav aria-label="breadcrumb">
    <ol class="breadcrumb">
        <#list data as d>
            <#if d_has_next>
                <li class="breadcrumb-item"><a href="<@slyak.query url="${d.url}"/>">${d.title}</a></li>
            <#else >
                <li class="breadcrumb-item active" aria-current="page">${d.title}</li>
            </#if>
        </#list>
    </ol>
</nav>
</#macro>

<#macro input name type="text" editable=true value="" class="" placeholder="">
<input type="${type}"
       class="form-control${editable?string('','-plaintext')} ${class}"${editable?string(' ',' readonly')}
       value="${value}" name="${name}" placeholder="${placeholder}"/>
</#macro>

<#macro a href title modalContent="" modal=(modalContent?has_content || false) showSubmit=false  attributes...>
    <#assign modalId>modal_<@slyak.randomAlphanumeric/></#assign>
    <#assign _attrs = attributes/>
    <#if modal>
        <#if modalContent?has_content>
            <@smartModal id=modalId title=title content=modalContent/>
        <#else >
            <@modalIframe id=modalId title=title url=href showSubmit=showSubmit/>
        </#if>
        <#assign _attrs=_attrs+{'data-toggle':'modal','data-target':'#${modalId}'}/>
    </#if>
<a href="<@slyak.query url=href/>"<@slyak.attributes values=_attrs/>>${title}</a>
</#macro>

<#macro radios name value='' data=[{'title':'test','value':'test'}]>
    <#list data as d>
        <#assign idTmp>radio_<@slyak.randomAlphanumeric/></#assign>
    <div class="custom-control custom-radio custom-control-inline">
        <input type="radio" id="${idTmp}" name="${name}"
               class="custom-control-input"<#if (!(value?has_content) && d_index==0) || value==d.value> checked</#if>>
        <label class="custom-control-label" for="${idTmp}">${d.title}</label>
    </div>
    </#list>
</#macro>

<#macro checkboxes name values=[] data=[{'title':'test','value':'test'}]>
    <#list data as d>
        <#assign idTmp>checkbox_<@slyak.randomAlphanumeric/></#assign>
    <div class="custom-control custom-checkbox custom-control-inline">
        <input type="checkbox" id="${idTmp}" name="${name}"
               class="custom-control-input"<#if values?seq_contains(d.value)> checked</#if>>
        <label class="custom-control-label" for="${idTmp}">${d.title}</label>
    </div>
    </#list>
</#macro>

<#macro textarea name editable=true rows=5 class="">
<textarea
        class="form-control${editable?string('','-plaintext')} ${class}"${editable?string(' ',' readonly')}
        name="${name}" rows="${rows}"><#nested /></textarea>
</#macro>

<#macro select name editable=true options=[{'title':'test','value':'test'}] value="" attributes...>
<select class="custom-select"<@slyak.attributes values=attributes/>>
    <#list options as opt>
        <option<#if value==opt.value> selected</#if> value="${opt.value}">${opt.title}</option>
    </#list>
</select>
</#macro>

<#macro formgroup label left=2 right=12-left required=false>
<div class="form-group row">
    <label for="staticEmail" class="col-sm-${left} col-form-label">${label}<#if required><span
            class="text-danger">*</span></#if></label>
    <div class="col-sm-${right}">
        <#nested />
    </div>
</div>
</#macro>

<#macro smartModal id title content="加载中..." class="modal-lg" onShown='' onSubmit=''>
<div class="modal" tabindex="-1" role="dialog" id="${id}">
    <div class="modal-dialog ${class}" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">${title}</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <p>${content}</p>
            </div>
            <div class="modal-footer">
                <#if onSubmit?has_content>
                    <button type="button" class="btn btn-primary">保存</button>
                </#if>
                <button type="button" class="btn btn-secondary" data-dismiss="modal">关闭</button>
            </div>
        </div>
    </div>
</div>
<script>
        <#nested />
    var modal${id} = $('#${id}');
    var form${id} = modal${id}.find("form");
        <#if onShown?has_content>
        modal${id}.on('shown.bs.modal', function (event) {
            var ret = ${onShown}($(this), modal${id});
            modal${id}.find(".modal-body > p").empty().append(ret);
        });
        </#if>
    modal${id}.find(".btn-primary").on("click", function (event) {
        var form = modal${id}.find("form");
        if (form.length > 0) {
            form.trigger("submit");
        } else {
            <#if onSubmit?has_content>
                ${onSubmit}($(this), modal${id});
            </#if>
        }
    });
        <#if onSubmit?has_content>
        modal${id}.find("form").on("submit", function (event) {
            eval('${onSubmit}')
        });
        </#if>
</script>
</#macro>

<#macro modalIframe id title url='' class="modal-lg" showSubmit=false>
    <#assign onSubmit=showSubmit?string("submitIframe_${id}","")/>
    <@smartModal id=id title=title class=class onSubmit="${onSubmit}" onShown="createIframe_${id}">
    var frame_${id};
    function createIframe_${id}(btn,modal){
    frame_${id} = <@slyakUI.iframe src='${url}'/>;
    return frame_${id};
    }
        <#if showSubmit>
        function submitIframe_${id}(btn,modal){
        var submitFunc = frame_${id}.contentWindow['onSubmit'];
        var hideFlag = true;
        if (submitFunc){
        hideFlag = submitFunc();
        if (hideFlag){
        var frameForm = $(frame_${id}.contentWindow.document.getElementsByTagName("form")[0]);
        if (frameForm){
        $.post(frameForm.attr("action"), frameForm.serialize());
        }
        }
        }
        hideFlag && modal.modal('hide') && parent.location.reload();
        }
        </#if>
    </@smartModal>
</#macro>

<#macro _menus menuBeans>
<#-- @ftlvariable name="menuBeans" type="java.util.List<com.slyak.web.ui.Menu>" -->
    <#list menuBeans as menu>
        <#assign isActive= menu.isActive(slyakRequestContext.getRequestUri())/>
        <#assign hasChildren = menu.hasChildren() />
    <li class="nav-item<#if isActive> active</#if><#if menu.hasChildren()> dropdown</#if>">
        <#if hasChildren>
        <li class="nav-item dropdown">
            <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button"
               data-toggle="dropdown"
               aria-haspopup="true" aria-expanded="false">
            ${menu.title}
            </a>
            <div class="dropdown-menu" aria-labelledby="navbarDropdown">
                <#list menu.children as subMenu>
                    <#if menu.title == 'separator'>
                        <div class="dropdown-divider"></div>
                    <#else>
                        <a class="dropdown-item"
                           href="${slyakRequestContext.getContextUrl(subMenu.url)}">${subMenu.title}</a>
                    </#if>
                </#list>
            </div>
        </li>
        <#else >
        <a class="nav-link"
           href="${slyakRequestContext.getContextUrl(menu.url)}">${menu.title}<#if isActive> <span
                class="sr-only">(current)</span></#if></a>
        </#if>
    </li>
    </#list>
</#macro>

<#macro navbar brand left=[] right=[]>
<nav class="navbar navbar-expand-lg navbar-light bg-light">
    <a class="navbar-brand" href="#">${brand}</a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent"
            aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarSupportedContent">
        <#if left?size gt 0>
            <#assign menuBeansLeft=MenuUtils.build(left)/>
            <ul class="navbar-nav mr-auto">
                <@_menus menuBeansLeft/>
            </ul>
        </#if>
        <#if right?size gt 0>
            <#assign menuBeansRight=MenuUtils.build(right)/>
            <ul class="navbar-nav flex-row ml-md-auto d-none d-md-flex">
                <@_menus menuBeansRight/>
            </ul>
        </#if>
        <#nested />
    </div>
</nav>
</#macro>

<#macro keywordSearch id="searchArea">
<div class="input-group" id="${id}">
    <@input placeholder="请输入关键字" name="keyword" value=RequestParameters.keyword/>
    <div class="input-group-append">
        <button type="button" class="btn input-group-text btnSearch">搜索</button>
    </div>
</div>
<script>
    $(".btnSearch").click(function () {
        var current = location.href;
        location.href = current.substring(0, current.indexOf("?")) + "?keyword=" + $('#${id}').find('input[name=keyword]').val();
    })
</script>
</#macro>

<#--https://blog.csdn.net/u012526194/article/details/69937741-->
<#--https://github.com/kartik-v/bootstrap-fileinput/wiki/09.-%E5%8F%82%E6%95%B0-->
<#macro fileupload cssSelector uploadUrl downloadUrl="" editable=true initialPreviewConfig=[] preferIconicPreview=true hiddenFidsField="fids" deleteUrl="" onUploaded="" onCleared="" onError="" onlyImage=false fileExts=[] imageWidth=80 maxFileCount=1 showRemove=true showPreview=true dropZoneEnabled=false browseClass="btn btn-primary">
    <@slyak.js url=[
    '/webjars/bootstrap-fileinput/js/plugins/piexif.min.js',
    '/webjars/bootstrap-fileinput/js/plugins/sortable.min.js',
    '/webjars/bootstrap-fileinput/js/plugins/purify.min.js',
    '/webjars/bootstrap-fileinput/js/fileinput.min.js',
    '/webjars/bootstrap-fileinput/themes/fa/theme.min.js',
    '/webjars/bootstrap-fileinput/js/locales/zh.js'
    ] />
    <@slyak.css url=[
    '/webjars/bootstrap-fileinput/css/fileinput.min.css',
    '/webjars/slyak-web-bootstrap/fileinput.css'
    ]/>
<script>
    $(function () {
    <#--$("${cssSelector}").after("<input type='hidden' name='${hiddenFidsField}'/>");-->
        <#assign initialPreview>
            <#if initialPreviewConfig?size gt 0>
                <#assign previewUrls=[]/>
                <#list initialPreviewConfig as cfg>
                    <#assign previewUrls = previewUrls + [downloadUrl+cfg.key]/>
                </#list>
                <@slyak.json object=previewUrls/>
            <#else >
                <@slyak.json object=[]/>
            </#if>
        </#assign>

        $("${cssSelector}").fileinput({
            language: 'zh',
            theme: 'fa',
            uploadUrl: '<@slyak.query url=uploadUrl/>',
            <#if fileExts?size gt 0>
                allowedFileExtensions: <@slyak.json object=fileExts/>,
            </#if>
            uploadAsync: true,
            showBrowse: ${editable?string},
            showRemove: ${(editable && showRemove)?string},
            showPreview: ${showPreview?string},
            showClose: false,
            showCaption: true,
            browseClass: '${browseClass}',
            dropZoneEnabled: ${dropZoneEnabled?string},
            maxFileCount: ${maxFileCount},
            msgFilesTooMany: '选择上传的文件数量({n}) 超过允许的最大数值{m}！',
            deleteUrl: '<@slyak.query url=deleteUrl/>',
            initialPreview: ${initialPreview},
            initialPreviewAsData: true, // defaults markup
            initialPreviewFileType: 'image', // image is the default and can be overridden in config below
            <#if initialPreviewConfig?size gt 0>
                //eg: [{caption: "Business-1.jpg", size: 762980, key: 11},...]
                initialPreviewConfig: <@slyak.json object=initialPreviewConfig/>,
            </#if>
            preferIconicPreview: true, // this will force thumbnails to display icons for following file extensions
            previewFileIconSettings: { // configure your icon file extensions
                'doc': '<i class="fa fa-file-word text-primary"></i>',
                'xls': '<i class="fa fa-file-excel text-success"></i>',
                'ppt': '<i class="fa fa-file-powerpoint text-danger"></i>',
                'pdf': '<i class="fa fa-file-pdf text-danger"></i>',
                'zip': '<i class="fa fa-file-archive text-muted"></i>',
                'htm': '<i class="fa fa-file-code text-info"></i>',
                'txt': '<i class="fa fa-file-text text-info"></i>',
                'mov': '<i class="fa fa-file-movie text-warning"></i>',
                'mp3': '<i class="fa fa-file-audio text-warning"></i>',
                // note for these file types below no extension determination logic
                // has been configured (the keys itself will be used as extensions)
                'jpg': '<i class="fa fa-file-image text-danger"></i>',
                'gif': '<i class="fa fa-file-image text-muted"></i>',
                'png': '<i class="fa fa-file-image text-primary"></i>'
            },
            previewFileExtSettings: { // configure the logic for determining icon file extensions
                'doc': function (ext) {
                    return ext.match(/(doc|docx)$/i);
                },
                'xls': function (ext) {
                    return ext.match(/(xls|xlsx)$/i);
                },
                'ppt': function (ext) {
                    return ext.match(/(ppt|pptx)$/i);
                },
                'zip': function (ext) {
                    return ext.match(/(zip|rar|tar|gzip|gz|7z)$/i);
                },
                'htm': function (ext) {
                    return ext.match(/(htm|html)$/i);
                },
                'txt': function (ext) {
                    return ext.match(/(txt|ini|csv|java|php|js|css)$/i);
                },
                'mov': function (ext) {
                    return ext.match(/(avi|mpg|mkv|mov|mp4|3gp|webm|wmv)$/i);
                },
                'mp3': function (ext) {
                    return ext.match(/(mp3|wav)$/i);
                }
            },
            layoutTemplates: { // 预览图片按钮控制，这里屏蔽预览按钮
                    <#if !editable>actionDelete: '',</#if>
                actionZoom: ''
            }
        }).on("fileuploaded", function (event, data, previewId, index) {
            <#if onUploaded?has_content>
                ${onUploaded}(data);
            </#if>
        }).on("filecleared", function (event, data, msg) {
            <#if onCleared?has_content>
                ${onCleared}(data);
            </#if>
        }).on("fileerror", function (event, data, msg) {
            <#if onError?has_content>
                ${onError}(data);
            </#if>
        });
    });
</script>
</#macro>