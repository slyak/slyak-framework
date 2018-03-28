<#ftl strip_whitespace=true>
<#-- @ftlvariable name="slyakRequestContext" type="com.slyak.web.support.freemarker.SlyakRequestContext" -->
<#macro cssAndJs>
    <@slyak.js url=[
    '/webjars/jquery/3.0.0/jquery.min.js',
    '/webjars/popper.js/1.11.1/dist/popper.min.js',
    '/webjars/bootstrap/4.0.0/js/bootstrap.min.js'
    ]/>
    <@slyak.css url=['/webjars/bootstrap/4.0.0/css/bootstrap.min.css']/>
</#macro>
<#--
TODO:
layout content
alters badge breadcrumb buttons button group card carousel collapse dropdowns forms
inputgroup jumbotron listgroup modal navs navbar popovers progress scrollspy tooltips
-->

<#macro table data=>
    <table class="table">
        <thead class="thead-dark">
            <tr>
                <th scope="col"></th>
            </tr>
        </thead>
    </table>
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