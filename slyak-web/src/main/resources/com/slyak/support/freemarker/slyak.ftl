<#ftl strip_whitespace=true>
<#-- @ftlvariable name="slyakRequestContext" type="com.slyak.support.freemarker.SlyakRequestContext" -->
<#macro query extra>${slyakRequestContext.query(extra)}</#macro>