<#ftl strip_whitespace=true>
<#-- @ftlvariable name="slyakRequestContext" type="com.slyak.web.support.freemarker.SlyakRequestContext" -->
<#--attributes for dom-->
<#macro a href attributes...><a href="<@slyak.query url=href/>"<@slyak.attributes values=attributes/>><#nested /></a></#macro>
<#macro form action method="POST" enctype="application/x-www-form-urlencoded" attributes...>
<form action="<@slyak.query url=action/>" method="${method}" autocomplete="off" enctype="${enctype}"<@slyak.attributes values=attributes/>>
    <input style="display:none" type="text" name="fakename" title="fakename">
    <input style="display:none" type="password" name="fakepwd" title="fakepwd">
    <#nested />
</form>
</#macro>
<#macro pagination value showNumber=9 relativeUrl="" size=20 attributes...>
<#-- @ftlvariable name="value" type="org.springframework.data.domain.Page" -->
    <#if value.totalPages gt 1>
        <#assign currentNumber = value.number/>
        <#assign pg = slyakRequestContext.pagination(value.totalPages,currentNumber,showNumber)/>
        <#assign start = pg.start/>
        <#assign end = pg.end/>
        <#assign hasPrevious = pg.hasPrevious/>
        <#assign hasNext = pg.hasNext/>
    <nav aria-label="Slayk Pagination"<@slyak.attributes attributes/>>
        <ul class="pagination">
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
<#macro table struct page showNumber=9 size=20 relativeUrl="">
    <table>
        <thead>
            <tr>
                <#list struct.thead as h>
                <th<@slyak.attributes values=h.attrs/>>${h.title}</th>
                </#list>
            </tr>
        </thead>
        <tbody>
            <#list 0..struct.length as idx>
            <td>${page.content[idx][struct.prop]}</td>
            </#list>
        </tbody>
    </table>
    <@pagination value=page showNumber=showNumber relativeUrl=relativeUrl  size=size />
</#macro>