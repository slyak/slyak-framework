<#ftl strip_whitespace=true>
<#-- @ftlvariable name="slyakRequestContext" type="com.slyak.web.support.freemarker.SlyakRequestContext" -->
<#--attributes for dom-->
<#macro cssAndJs>
    <@slyak.js url=["/webjars/slyak-web/slyak-ui.js"]/>
</#macro>

<#macro handlebars>
    <@slyak.js url=["/webjars/jquery-sortable/js/jquery-sortable.min.js"]/>
<script>
    Handlebars.registerHelper('ifCond', function (v1, operator, v2, options) {
        switch (operator) {
            case '==':
                return (v1 == v2) ? options.fn(this) : options.inverse(this);
            case '===':
                return (v1 === v2) ? options.fn(this) : options.inverse(this);
            case '!=':
                return (v1 != v2) ? options.fn(this) : options.inverse(this);
            case '!==':
                return (v1 !== v2) ? options.fn(this) : options.inverse(this);
            case '<':
                return (v1 < v2) ? options.fn(this) : options.inverse(this);
            case '<=':
                return (v1 <= v2) ? options.fn(this) : options.inverse(this);
            case '>':
                return (v1 > v2) ? options.fn(this) : options.inverse(this);
            case '>=':
                return (v1 >= v2) ? options.fn(this) : options.inverse(this);
            case '&&':
                return (v1 && v2) ? options.fn(this) : options.inverse(this);
            case '||':
                return (v1 || v2) ? options.fn(this) : options.inverse(this);
            default:
                return options.inverse(this);
        }
    });
</script>
</#macro>
<#macro fontasome>
    <@slyak.css url="/webjars/font-awesome/web-fonts-with-css/css/fontawesome-all.css"/>
</#macro>

<#macro jqueryui>
    <@slyak.css url="/webjars/jquery-ui/jquery-ui.min.css"/>
    <@slyak.js url="/webjars/jquery-ui/jquery-ui.min.js"/>
</#macro>

<#macro a href test={} attributes...><a
        href="<@slyak.query url=href/>"<@slyak.attributes values=attributes/>><#nested /></a></#macro>

<#macro form action method="POST" enctype="application/x-www-form-urlencoded" attributes...>
<form action="<@slyak.query url=action/>" method="${method}" autocomplete="off"
      enctype="${enctype}"<@slyak.attributes values=attributes/>>
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
<#macro checkbox name value attributes...>
    <#assign id><@slyak.randomAlphanumeric/></#assign>
    <#assign idChk=id+"_chk"/>
    <#assign idField=id+"_field"/>
<input type="checkbox" <#if value==true>checked</#if> id="${idChk}"<@slyak.attributes/>>
<input type="hidden" name="${name}" value="${value!false}" id="${idField}">
<script>
    $(function () {
        $("#${idChk}").click(function () {
            $("#${idField}").val($("#${idChk}").is(":checked"));
        })
    });
</script>
</#macro>
<#macro table page struct={'thead':[{'title':'test','field':'test','attrs':{'with':'100%'}}]} showNumber=9 size=20 relativeUrl="" checkbox=false checkedIds=[] idField="" attributes...>
    <#assign tableId>table_<@slyak.randomAlphanumeric/></#assign>
<table<@slyak.attributes values=attributes/> id="${tableId}">
    <thead>
    <tr>
        <#if checkbox>
            <th>
                <input type="checkbox" class="checkAll" <#if page.totalElements==checkedIds?size>checked</#if>/>
            </th>
        </#if>
        <#list struct.thead as h>
            <th<@slyak.attributes values=h.attrs/>>${h.title}</th>
        </#list>
    </tr>
    </thead>
    <tbody>
        <#if page.totalElements gt 0>
            <#list 0..struct.thead?size-1 as idx>
                <#assign obj=page.content[idx]/>
                <#if checkbox>
                <td><input class="singleCheck" name="id" value="${obj[idField]}" type="checkbox"
                           <#if checkedIds?seq_contains(obj[idField])>checked</#if>/></td>
                </#if>
            <td>${obj[struct.thead[idx].field]}</td>
            </#list>
        <#else >
            <#assign col=checkbox?string((struct.thead?size+1)?string,struct.thead?size?string)/>
        <td colspan="${col}" style="text-align: center">暂无记录</td>
        </#if>
    </tbody>
</table>
    <@pagination value=page showNumber=showNumber relativeUrl=relativeUrl  size=size />
<script>
    var table = $("#${tableId}");
    table.find(".checkAll").("click", function () {
        var checked = $(this).prop("checked");
        if (checked) {
            table.find(".singleCheck").prop('checked');
        } else {
            table.find(".singleCheck").removeProp('checked');
        }
    });
</script>
</#macro>

<#macro wrapScript wrap=false>
    <#if wrap>
    <script>
            <#nested />
        </script>
    <#else >
        <#nested />
    </#if>
</#macro>

<#macro iframeAutoFit wrap=true>
    <@wrapScript wrap=wrap>
    window.iframeAutoFit = window.iframeAutoFit || function (frame) {
    var container = frame.contentWindow || frame.contentDocument.parentWindow;
    frame.height = container.document.documentElement.scrollHeight || container.document.body.scrollHeight;
    }
    </@wrapScript>
</#macro>

<#macro iframe src>
(function (){
    <@iframeAutoFit wrap=false/>
var iframe = document.createElement("iframe");
iframe.frameBorder = 0;
iframe.scrolling = 'no';
iframe.width = '100%';
iframe.src = "${src}";
if (iframe.attachEvent){
iframe.attachEvent("onload", function(){
iframeAutoFit(iframe);
});
} else {
iframe.onload = function(){
iframeAutoFit(iframe);
};
}
window.onresize=function(){
iframeAutoFit(iframe);
}
window.setInterval(function(){iframeAutoFit(iframe)}, 200);
return iframe;
}())
</#macro>

<#macro formatFileSize wrap=true>
    <@wrapScript wrap=wrap>
    window.formatFileSize = window.formatFileSize || function (fileSize){
    var arrUnit = ["B", "K","M", "G", "T", "P"];
    var powerIndex = Math.log2(fileSize) / 10;
    powerIndex = Math.floor(powerIndex);
    // index should in the unit range!
    var len = arrUnit.length;
    powerIndex = powerIndex < len ? powerIndex : len - 1;
    var sizeFormatted = fileSize / Math.pow(2, powerIndex * 10
    sizeFormatted = sizeFormatted.toFixed(2);
    return sizeFormatted + " " + arrUnit[powerIndex];
    }
    </@wrapScript>
</#macro>