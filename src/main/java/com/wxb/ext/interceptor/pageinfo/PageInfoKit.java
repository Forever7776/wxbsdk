/**
 * Copyright (c) 2011-2013, kidzhou 周磊 (zhouleib1412@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wxb.ext.interceptor.pageinfo;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfinal.core.Controller;
import com.wxb.ext.kit.Reflect;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.TableMapping;
import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author kid create 2013-10-24
 */
public class PageInfoKit {

    protected final static Log LOG = Log.getLog(PageInfoKit.class);

    private static final String OPERATOR_SUFFIX = "_op";

    private static final String FILTER_PREFIX = "f_";

    private static final String COMMA = ",";

    @SuppressWarnings("rawtypes")
    public static Page populate(PageInfo pageInfo, PageInfoInterceptor pageInfoInterceptor) {
        Class<? extends Model<?>> model = pageInfoInterceptor.model();
        Table tableinfo = TableMapping.me().getTable(model);
        String modelName = StrKit.firstCharToLowerCase(tableinfo.getName());
        Map<String, Class<?>> columnTypeMap = Reflect.on(tableinfo).get("columnTypeMap");
        StringBuilder sb = new StringBuilder("select ");
        String tempStr = StringUtils.EMPTY;
//        String select = "select ";
        if (StrKit.isBlank(pageInfoInterceptor.columns())) {
            Set<String> set = columnTypeMap.keySet();
            for (String item : set) {
                sb.append(item).append(COMMA);
//                select += item + ",";
            }
            if (!pageInfoInterceptor.relations().isEmpty()) {
                for (RelationInfo relation : pageInfoInterceptor.relations()) {
                    Class<? extends Model<?>> modelClass = relation.getModel();
                    Table relationTableinfo = TableMapping.me().getTable(modelClass);
                    Map<String, Class<?>> relationColumnTypeMap = Reflect.on(relationTableinfo).get("columnTypeMap");
                    set = relationColumnTypeMap.keySet();
                    // 如果设置了前缀表明或者字段冲突的时候
                    for (String item : set) {
                        if (pageInfoInterceptor.useColumnLabel()
                                || columnConflict(item, model, modelClass, pageInfoInterceptor.relations())) {
                            String itemColum = modelName + COMMA + item;
                            if (sb.indexOf(itemColum)==-1) {
                                tempStr = "," + item + ",";
                                if(sb.indexOf(tempStr)>-1){
                                    sb =  new StringBuilder(StringUtils.replace(sb.toString(),tempStr,","+modelName+"."+item+","));
                                }
                                tempStr = " " + item + ",";
                                if(sb.indexOf(tempStr)>-1){
                                    sb = new StringBuilder(StringUtils.replace(sb.toString(),tempStr," "+modelName+"."+item+","));
                                }
                            }
                            sb.append(modelName).append(".").append(item).append(" ").append(modelName).append("_").append(item).append(COMMA);
                            /*select += StrKit.firstCharToLowerCase(modelClass.getSimpleName()) + "." + item + " "
                                    + StrKit.firstCharToLowerCase(modelClass.getSimpleName()) + "_" + item + ",";*/
                        } else {
                            sb.append(item).append(COMMA);
//                            select += item + ",";
                        }
                    }
                }
            }
            tempStr = sb.toString();
            //FIXME
            sb  = new StringBuilder(StringUtils.substring(tempStr,0,tempStr.length()-1));
//            select = select.substring(0, select.length() - 1);
        } else {
            sb.append(pageInfoInterceptor.columns());
//            select += pageInfoInterceptor.columns();
        }

        List<Object> paras = Lists.newArrayList();

//        String sqlExceptSelect = "from " + tableinfo.getName();
        StringBuilder ex_sb = new StringBuilder("from " + tableinfo.getName());
        for (RelationInfo relationInfo : pageInfoInterceptor.relations()) {
            String tableName = TableMapping.me().getTable(relationInfo.getModel()).getName();
            String val = relationInfo.getCondition();
            ex_sb.append(" left join ").append(tableName).append(" on ( ").append(val).append(") ");
//            sqlExceptSelect += " left join " + tableName + " on ( " + val + ") ";
        }
        ex_sb.append(" where 1=1 ");
//        sqlExceptSelect += " where 1=1 ";
        List<Filter> filters = pageInfo.getFilters();
        for (int i = 0; i < filters.size(); i++) {
            Filter filter = filters.get(i);
            List<Condition> conditions = filter.getConditions();
            ex_sb.append(filter.getRelation()).append(" ( 1=1 ");
//            sqlExceptSelect += filter.getRelation();
//            sqlExceptSelect += " ( 1=1 ";
            for(int j = 0; j < conditions.size(); j++){
                Condition condition = conditions.get(j);
                String fieldName = condition.getFieldName();
                Object value = condition.getValue();
                if (value == null) {
                    continue;
                }
                if (condition.getOperater().equals(Filter.OPERATOR_LIKE)) {
                    paras.add("%" + value + "%");
                } else if(!condition.getOperater().equals(Filter.OPERATOR_NULL) &&
                          !condition.getOperater().equals(Filter.OPERATOR_NOT_NULL)){
                    paras.add(value);
                }
                ex_sb.append(condition.getRelation()).append(" ").append(fieldName);
                if(condition.getOperater().equals(Filter.OPERATOR_NULL)){
                    ex_sb.append(" IS NULL ");
//                    sqlExceptSelect+= condition.getRelation()+" "+fieldName+" IS NULL ";
                }else if(condition.getOperater().equals(Filter.OPERATOR_NOT_NULL)){
                    ex_sb.append(" IS NOT NULL ");
//                    sqlExceptSelect+= condition.getRelation()+" "+fieldName+" IS NOT NULL ";
                }else {
                    ex_sb.append(" ").append(condition.getOperater()).append(" ? ");
//                    sqlExceptSelect += condition.getRelation()+" " + fieldName + " " + condition.getOperater() + " ? ";
                }
            }
            ex_sb.append(" ) ");
//            sqlExceptSelect +=" ) ";
        }
        String sorterField = pageInfo.getSorterField();
        if (sorterField != null) {
            ex_sb.append(" order by ").append(sorterField).append(" ").append(pageInfo.getSorterDirection());
//            sqlExceptSelect += " order by " + sorterField + " " + pageInfo.getSorterDirection();
        }
        // select = select.substring(0, select.length());
        String select = sb.toString();
        String sqlExceptSelect = ex_sb.toString();

        if (pageInfoInterceptor.relations().isEmpty()) {
            Model modelInstance = Reflect.on(model).create().get();
            return modelInstance.paginate(pageInfo.getPageNumber(), pageInfo.getPageSize(), select, sqlExceptSelect,
                    paras.toArray(new Object[] {}));
        } else {
            return Db.paginate(pageInfo.getPageNumber(), pageInfo.getPageSize(), select, sqlExceptSelect,
                    paras.toArray(new Object[] {}));
        }
    }

    private static boolean columnConflict(String item, Class<? extends Model<?>> mainModel,
            Class<? extends Model<?>> currentModel, List<RelationInfo> relations) {
        if (TableMapping.me().getTable(mainModel).hasColumnLabel(item)) {
            return true;
        }
        for (RelationInfo relationInfo : relations) {
            if (currentModel != relationInfo.getModel()
                    && TableMapping.me().getTable(relationInfo.getModel()).hasColumnLabel(item)) {
                return true;
            }
        }
        return false;
    }

    public static PageInfo injectPageInfo(Class<? extends Model<?>> modelClass, Controller controller,
            List<RelationInfo> relations,Map<String,Object> other_paraMap) {
        Map<String, Record> modelAttrs = Maps.newHashMap();
        List<String> modelNames = Lists.newArrayList();
        Map<String, String> models = Maps.newHashMap();
        PageInfo pageInfo = new PageInfo();
//        String modelName = StrKit.firstCharToLowerCase(modelClass.getSimpleName());
        String modelName = TableMapping.me().getTable(modelClass).getName();
        pageInfo.setPageNumber(controller.getParaToInt("pageNumber", 1));
        pageInfo.setPageSize(controller.getParaToInt("pageSize", PageInfo.DEFAULT_PAGE_SIZE));
        modelNames.add(modelName);
        modelAttrs.put(modelName, new Record());
        for (RelationInfo relationInfo : relations) {
            String tableName = TableMapping.me().getTable(relationInfo.getModel()).getName();
            modelNames.add(tableName);
//            modelAttrs.put(StrKit.firstCharToLowerCase(relationInfo.getModel().getSimpleName()), new Record());
            modelAttrs.put(tableName, new Record());
        }

        Map<String, String[]> parasMap = controller.getParaMap();
        for (Entry<String, String[]> e : parasMap.entrySet()) {
            String paraKey = e.getKey();
            for (String entry : modelNames) {
                if (paraKey.startsWith(entry + ".")) {
                    String[] paraValue = e.getValue();
                    String value = paraValue[0] != null ? paraValue[0] + "" : null;
                    models.put(paraKey, value);
                }
            }
        }
        for(Entry<String, Object> e : other_paraMap.entrySet()){
            String paraKey = e.getKey();
            for (String entry : modelNames) {
                if (paraKey.startsWith(entry + ".")) {
                    Object paraValue = e.getValue();
                    models.put(paraKey, paraValue+"");
                }
            }
        }
        // filter
        Map<String, String> filter = Maps.newLinkedHashMap();
        Set<Entry<String, String>> entries = models.entrySet();
        for (Entry<String, String> entry : entries) {
            String key = entry.getKey();
            for (String item : modelNames) {
                if (key.startsWith(item + "." + FILTER_PREFIX)) { // 过滤条件
                    int index = key.indexOf(FILTER_PREFIX);
                    String value = entry.getValue();
                    if (StrKit.isBlank(value)) {
                        continue;
                    }
                    // int manyIndex = propertyName.lastIndexOf("0");
                    // if(manyIndex < 0)
                    // filterName = propertyName.substring("f_".length());
                    // else
                    // filterName = propertyName.substring("f_".length(),manyIndex);
                    filter.put(key.substring(0, index) + key.substring(index + FILTER_PREFIX.length()), value);
                }
            }
        }
        List<Filter> filters = Lists.newArrayList();
        for (Entry<String, String> entry : filter.entrySet()) {
            String key = entry.getKey();
            if (key.endsWith(OPERATOR_SUFFIX)) { // 操作符
                continue;
            }
            String operater = filter.get(key + OPERATOR_SUFFIX);
            if (StrKit.isBlank(operater)) {
                operater = Filter.OPERATOR_EQ;
            }
            int index = key.indexOf(".");
            modelAttrs.get(key.substring(0, index)).set(FILTER_PREFIX + key.substring(index + 1, key.length()),
                    entry.getValue());
            Filter newFilter = new Filter();
            newFilter.addConditions(key, entry.getValue(), operater);
            filters.add(newFilter);
        }
        pageInfo.setFilters(filters);

        addSorter(controller, pageInfo);
        for (Entry<String, Record> item : modelAttrs.entrySet()) {
            controller.setAttr(item.getKey(), item.getValue());
        }
        return pageInfo;
    }

    private static void addSorter(Controller controller, PageInfo pageInfo) {
        String sorterField = controller.getPara("sorterField");
//        String sorterField = controller.getRequest().getParameter("sorterField");
        if (StrKit.notBlank(sorterField)) {
            String sorterDirection = controller.getPara("sorterDirection");
//            String sorterDirection = controller.getRequest().getParameter("sorterDirection");
            if (StrKit.isBlank(sorterDirection)) {
                sorterDirection = "desc";
            }
            pageInfo.setSorterField(sorterField);
            pageInfo.setSorterDirection(sorterDirection);
        }
    }

}
