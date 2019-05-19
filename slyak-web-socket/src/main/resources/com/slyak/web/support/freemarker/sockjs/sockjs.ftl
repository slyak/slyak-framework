<#ftl strip_whitespace=true>
<#-- @ftlvariable name="slyakRequestContext" type="com.slyak.web.support.freemarker.SlyakRequestContext" -->
<#macro cssAndJs>
    <@slyak.js url=[
    '/webjars/sockjs-client/sockjs.min.js',
    '/webjars/stomp-websocket/stomp.min.js',
    '/webjars/slyak-web-socket/slyaksocket.js'
    ]/>
</#macro>
<#macro connect topics>
    <script>
        (function () {
            var ss = slyaksocket(<@slyak.json object=topics/>);
            <#nested />
        }());
    </script>
</#macro>