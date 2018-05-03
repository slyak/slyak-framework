<#ftl strip_whitespace=true>
<#-- @ftlvariable name="slyakRequestContext" type="com.slyak.web.support.freemarker.SlyakRequestContext" -->
<#--attributes for dom-->
<#macro attributes values={}><#list values?keys as k> ${k}="${values[k]}"</#list></#macro>
<#--current query and replace param with extra map-->
<#macro query url="" extra={}>${slyakRequestContext.query(url,extra)}</#macro>
<#macro joinAhead values=[] delimiter=" ">${delimiter}<#list values as v>${v}</#list></#macro>
<#macro addClass values=[]><@joinAhead values=values delimiter=" "/></#macro>
<#macro addStyle values=[]><@joinAhead values=values delimiter=";"/></#macro>
<#macro resource url>${slyakRequestContext.getResource(url)}</#macro>
<#macro css url>${slyakRequestContext.css(url)}</#macro>
<#macro js url>${slyakRequestContext.js(url)}</#macro>
<#macro randomAlphanumeric count=5>${slyakRequestContext.randomAlphanumeric(count)}</#macro>
<#macro json object>${slyakRequestContext.json(object)}</#macro>