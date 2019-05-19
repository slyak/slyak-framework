<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" th:replace="layout1">
<!--/*@thymesVar id="${entity.name?uncap_first}" type="com.slyak.license.domain.${entity.name}"*/-->
<div th:fragment="right">
    <div class="layui-card p15">
        <div class="layui-card-header" th:text="${r'${'}${entity.name?uncap_first}.id!=null${r'}'}?'编辑':'新增'">
            编辑
        </div>
        <div class="layui-card-body">
            <form class="layui-form">
                <input type="hidden" name="id" th:value="${r'${'}${entity.name?uncap_first}.id${r'}'}" th:if="${r'${'}${entity.name?uncap_first}!=null${r'}'}">
                <#list entity.columns as c>
                    <div class="layui-form-item">
                        <label class="layui-form-label">${c.comment}</label>
                        <div class="layui-input-block">
                            <#if c.enumable>
                                <div th:replace="fragments:: dict(code='${c.type?uncap_first}',fieldName='${c.name}',fieldValue=${r'${'}${entity.name?uncap_first}.${c.name}${r'}'},multi=false)"></div>
                                <#else >
                                <#assign checks=[]/>
                                <#if c.nullable>
                                    <#assign checks=checks+["required"]/>
                                </#if>
                            <input type="text" name="${c.name}" th:value="${r'${'}${entity.name?uncap_first}.${c.name}${r'}'}" lay-verify="${checks?join("|")}" autocomplete="off" placeholder="请输入" class="layui-input<#if c.type=="Date"> input-date</#if>">
                            </#if>
                            <#if c.type=='boolean'>
                                <input type="checkbox" name="${c.name}" lay-skin="switch" lay-text="ON|OFF" th:checked=""${r'${'}${entity.name?uncap_first}.${c.name}${r'}'}">
                            </#if>
                        </div>
                    </div>
                </#list>
                <div class="layui-form-item">
                    <div class="layui-input-block">
                        <button class="layui-btn" type="button" lay-submit="" lay-filter="submitForm">保存</button>
                        <a class="layui-btn layui-btn-primary" href="list">返回</a>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <script th:inline="javascript">
        $(function () {
            var isEdit = [[${r'${'}${entity.name?uncap_first}.id!=null${r'}'}]];
            layui.use('form', function () {
                var form = layui.form;
                form.on('submit(submitForm)', function (data) {
                    var field = data.field;
                    $.post('save', field, function (data) {
                        if (data.success) {
                            if (isEdit){
                                layer.msg('保存成功', {offset: '120px'});
                            } else {
                                location.href = 'list';
                            }
                        } else {
                            layer.msg(data.msg, {offset: '120px'});
                        }
                    });
                    return false;
                });
            });
        });
    </script>
</div>
