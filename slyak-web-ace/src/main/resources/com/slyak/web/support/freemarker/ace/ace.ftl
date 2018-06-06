<#ftl strip_whitespace=true>
<#-- @ftlvariable name="slyakRequestContext" type="com.slyak.web.support.freemarker.SlyakRequestContext" -->
<#macro cssAndJs>
    <@slyak.js url=[
    '/webjars/ace/src-min-noconflict/ace.js',
    '/webjars/ace/src-min-noconflict/ext-textarea.js',
    '/webjars/ace/src-min-noconflict/ext-language_tools.js'
    ]/>
</#macro>

<#macro init cssSelector mode="javascript" theme="monokai" minLines=20 maxLines=20 editable=true>
    <#assign editorId>editor_<@slyak.randomAlphanumeric/></#assign>
<script>
    (function () {
        var realEditor = $("${cssSelector}");
        var fakeEditor = $("<div id='${editorId}'/>");
        realEditor.after(fakeEditor);
        realEditor.hide();
        var editor = ace.edit("${editorId}");
        editor.setTheme("ace/theme/${theme}");
        editor.session.setMode("ace/mode/${mode}");
        editor.setOptions({
            autoScrollEditorIntoView: true,
            copyWithEmptySelection: true,
            enableBasicAutocompletion: true,
            enableSnippets: true,
            enableLiveAutocompletion: true
        });
        editor.setAutoScrollEditorIntoView(true);
        editor.setOption("maxLines", ${maxLines});
        editor.setOption("minLines", ${minLines});
        //setup real editor and fake editor value
        editor.getSession().setValue(realEditor.val());
        <#if !editable>editor.setReadOnly(true);</#if>
        function gotoLastLine() {
            var session = editor.getSession();
            var count = session.getLength();
            editor.gotoLine(count, session.getLine(count - 1).length);
        }

        editor.getSession().on("change", function () {
            realEditor.val(editor.getSession().getValue());
            <#if !editable>gotoLastLine();</#if>
        });

        gotoLastLine();
        <#nested />
    }());
</script>
</#macro>