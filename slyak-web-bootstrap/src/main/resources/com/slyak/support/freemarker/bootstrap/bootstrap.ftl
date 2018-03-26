<#ftl strip_whitespace=true>
<#macro cssAndJs>
    <@slyak.js url=[
    '/webjars/jquery/2.1.1/jquery.js',
    '/webjars/popper.js/1.11.1/dist/popper.js',
    '/webjars/bootstrap/4.0.0/js/bootstrap.js'
    ]/>
    <@slyak.css url=['/webjars/bootstrap/4.0.0/css/bootstrap.css']/>
</#macro>
<#--
TODO:
layout content
alters badge breadcrumb buttons button group card carousel collapse dropdowns forms
inputgroup jumbotron listgroup modal navs navbar popovers progress scrollspy tooltips
-->
<#-- @ftlvariable name="slyakRequestContext" type="com.slyak.support.freemarker.SlyakRequestContext" -->
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
                    <a class="page-link" href="<@slyak.query url=relativeUrl extra={'page':currentNumber-1,'size':size}/>"
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