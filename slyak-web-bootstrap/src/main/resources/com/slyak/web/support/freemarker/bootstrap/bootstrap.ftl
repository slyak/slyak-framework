<#ftl strip_whitespace=true>
<#-- @ftlvariable name="slyakRequestContext" type="com.slyak.web.support.freemarker.SlyakRequestContext" -->
<#macro cssAndJs>
    <@slyak.js url=[
    '/webjars/jquery/jquery.min.js',
    '/webjars/popper.js/dist/popper.min.js',
    '/webjars/bootstrap/js/bootstrap.min.js'
    ]/>
    <@slyak.css url=['/webjars/bootstrap/4.0.0/css/bootstrap.min.css']/>
</#macro>
<#--
TODO:
layout content
alters badge breadcrumb buttons button group card carousel collapse dropdowns forms
inputgroup jumbotron listgroup modal navs navbar popovers progress scrollspy tooltips
-->

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

<#macro navbar brand menu>
    <#assign menuBeans=MenuUtils.build(menu)/>
<nav class="navbar navbar-expand-lg navbar-light bg-light">
    <a class="navbar-brand" href="#">${brand}</a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent"
            aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarSupportedContent">
        <ul class="navbar-nav mr-auto">
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
                    <a class="nav-link" href="#">${menu.title}<#if isActive> <span
                            class="sr-only">(current)</span></#if></a>
                </#if>
                </li>
            </#list>
        </ul>
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