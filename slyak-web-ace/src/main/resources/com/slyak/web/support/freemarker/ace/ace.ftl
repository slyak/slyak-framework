<#ftl strip_whitespace=true>
<#-- @ftlvariable name="slyakRequestContext" type="com.slyak.web.support.freemarker.SlyakRequestContext" -->
<#macro cssAndJs>
    <@slyak.js url=[
    '/webjars/ace/src-min-noconflict/ace.js'
    ]/>
</#macro>

<#macro init id mode="javascript" theme="monokai" minLines=20 maxLines=20>
<script>
    (function () {
        var editor = ace.edit("${id}");
        editor.setTheme("ace/theme/${theme}");
        editor.session.setMode("ace/mode/${mode}");
        editor.setOptions({
            autoScrollEditorIntoView: true,
            copyWithEmptySelection: true
        });
        editor.setAutoScrollEditorIntoView(true);
        editor.setOption("maxLines", ${maxLines});
        editor.setOption("minLines", ${minLines});
        <#nested />
    }());
</script>
</#macro>