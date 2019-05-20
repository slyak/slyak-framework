<#-- @ftlvariable id="a" type="java.util.List<String>" -->
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" th:replace="layout1">
<div th:fragment="right">
    <div class="layui-card p15">
        <form class="layui-form search-form">
            <div class="layui-form-item">
                <#list entity.columns as c>
                <div class="layui-inline">
                    <label class="layui-form-label">${c.comment}</label>
                    <div class="layui-input-inline">
                        <#if c.enumable>
                            <div th:replace="fragments:: dict(code='${c.type?uncap_first}',fieldName='${c.name}',fieldValue=${r'${#request.getParameter('}'${c.name}'${r')}'},multi=false)"></div>
                            <#else >
                        <input type="text" name="${c.name}" th:value="${r'${#request.getParameter('}'${c.name}'${r')}'}" lay-verify="" autocomplete="off" placeholder="请输入" class="layui-input<#if c.type=="Date"> input-date</#if>"/>
                        </#if>
                    </div>
                </div>
                </#list>
                <div class="layui-inline">
                    <div class="layui-input-inline">
                        <button type="submit" class="layui-btn">查询</button>
                        <a class="layui-btn layui-btn-primary" href="list">重置</a>
                    </div>
                </div>
            </div>
        </form>

        <a class="layui-btn" href="/${entity.name?uncap_first}/edit">新增</a>

        <table class="layui-table">
            <thead>
            <tr>
                <#list entity.columns as c>
                <th>${c.comment}</th>
                </#list>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <!--/*@thymesVar id="page" type="org.springframework.data.domain.Page<com.slyak.license.domain.${entity.name}>"*/-->
            <tr th:each="item: ${r'${page.content}'}">
                <#list entity.columns as c>
                    <#if c.enumable>
                <td th:text="${r'${item.'}${c.name}${r'.title}'}">测试</td>
                        <#else >
                <td th:text="${r'${item.'}${c.name}${r'}'}">测试</td>
                    </#if>
                </#list>
                <td>
                    <a class="layui-btn layui-btn-xs mr10" th:href="'edit?id='+${r'${item.'}id${r'}'}">编辑</a>
                    <a class="layui-btn layui-btn-xs mr10 confirm" th:href="'delete?id='+${r'${item.'}id${r'}'}">删除</a>
                </td>
            </tr>
            <tr th:if="${r'${!page.hasContent()}'}">
                <td colspan="${entity.columns?size+1}" class="text-center">暂无记录</td>
            </tr>
            </tbody>
        </table>
    </div>
</div>
</html>