<#ftl strip_whitespace=true>
<#-- @ftlvariable name="slyakRequestContext" type="com.slyak.web.support.freemarker.SlyakRequestContext" -->
<#macro cssAndJs>
    <@slyak.js url=[
    '/webjars/jquery/jquery.min.js',
    '/webjars/popper.js/popper.min.js',
    '/webjars/bootstrap/js/bootstrap.min.js'
    ]/>
    <@slyak.css url=['/webjars/bootstrap/css/bootstrap.min.css']/>
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

<#macro textarea name editable=true rows=5 value="" class="">
<textarea
        class="form-control${editable?string('','-plaintext')} ${class}"${editable?string(' ',' readonly')}
        name="${name}" rows="${rows}">${value}</textarea>
</#macro>

<#macro select name editable=true options=[{'title':'test','value':'test'}] value="" attributes...>
<select class="custom-select"<@slyak.attributes values=attributes/>>
    <#list options as opt>
        <option<#if value==opt.value> selected</#if> value="${opt.value}">${opt.title}</option>
    </#list>
</select>
</#macro>

<#macro form action enctype="application/x-www-form-urlencoded" attributes...>
<form action="<@slyak.query url=action/>" method="post" autocomplete="off"
      enctype="${enctype}" <@slyak.attributes values=attributes/>>
    <input style="display:none" type="text" name="fakename">
    <input style="display:none" type="password" name="fakepwd">
    <#nested />
</form>
</#macro>

<#macro formgroup label left=2 right=10 required=false>
<div class="form-group row">
    <label for="staticEmail" class="col-sm-${left} col-form-label">${label}<#if required><span
            class="text-danger">*</span></#if></label>
    <div class="col-sm-${right}">
        <#nested />
    </div>
</div>
</#macro>

<#macro model id title class="modal-lg" onShown='' onSubmit=''>
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
                <p>加载中 ...</p>
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
    var modal${id} = $('#${id}');
    var form${id} = modal${id}.find("form");
        <#if onShown?has_content>
        modal${id}.on('shown.bs.modal', function (event) {
            modal${id}.find(".modal-body > p").html(${onShown}($(this), $(event.relatedTarget)));
        });
        </#if>
    modal${id}.find(".btn-primary").on("click", function (event) {
        modal${id}.find("form").trigger("submit");
    });
        <#if onSubmit?has_content>
        modal${id}.find("form").on("submit", function (event) {
            eval('${onSubmit}')
        });
        </#if>
</script>
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

<#macro pagination value showNumber=9 relativeUrl="" size=20 classes=[] attributes...>
<#-- @ftlvariable name="value" type="org.springframework.data.domain.Page" -->
    <#if value.totalPages gt 1>
        <#assign currentNumber = value.number/>
        <#assign pg = slyakRequestContext.pagination(value.totalPages,currentNumber,showNumber)/>
        <#assign start = pg.start/>
        <#assign end = pg.end/>
        <#assign hasPrevious = pg.hasPrevious/>
        <#assign hasNext = pg.hasNext/>
    <nav aria-label="Slayk Pagination"<@slyak.attributes attributes/>>
        <ul class="pagination<@slyak.addClass classes/>">
            <#if hasPrevious>
                <li class="page-item">
                    <a class="page-link"
                       href="<@slyak.query url=relativeUrl extra={'page':currentNumber-1,'size':size}/>"
                       aria-label="Previous">
                        <span aria-hidden="true">上页</span>
                    </a>
                </li>
            </#if>
            <#list start..end as i>
                <#assign isActive=(i==currentNumber)/>
                <li class="page-item <#if isActive> active</#if>">
                    <a class="page-link" href="<@slyak.query url=relativeUrl extra={'page':i,'size':size}/>">${i+1}
                        <#if isActive><span class="sr-only">(current)</span></#if>
                    </a>
                </li>
            </#list>
            <#if hasNext>
                <li class="page-item">
                    <a class="page-link" aria-label="Next"
                       href=" <@slyak.query url=relativeUrl extra={'page':currentNumber+1,'size':size}/>">
                        <span aria-hidden="true">下页</span>
                    </a>
                </li>
            </#if>
        </ul>
    </nav>
    </#if>
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
        location.href = current.substring(0, current.indexOf("?"))+"?keyword="+$('#${id}').find('input[name=keyword]').val();
    })
</script>
</#macro>