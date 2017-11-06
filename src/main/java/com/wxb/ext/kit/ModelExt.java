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
package com.wxb.ext.kit;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.jfinal.kit.StrKit;
import com.wxb.ext.interceptor.CallbackListener;
import com.wxb.ext.interceptor.pageinfo.Parent;
import com.jfinal.plugin.activerecord.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
public class ModelExt<M extends ModelExt<M>> extends Model<M> {


    private String version = "version";


    private static List<CallbackListener> callbackListeners = Lists.newArrayList();

    private Class<? extends ModelExt<M>> clazz;

    @SuppressWarnings("unchecked")
    public ModelExt() {
        Type genericSuperclass = getClass().getGenericSuperclass();
        clazz = (Class<? extends ModelExt<M>>) ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
    }

    public static void addCallbackListener(CallbackListener callbackListener) {
        callbackListeners.add(callbackListener);
    }

    @Override
    public boolean save() {
        for (CallbackListener callbackListener : callbackListeners) {
            callbackListener.beforeSave(this);
        }
        boolean result = super.save();
        for (CallbackListener callbackListener : callbackListeners) {
            callbackListener.afterSave(this);
        }
        return result;
    }

    @Override
    public boolean update() {
        for (CallbackListener callbackListener : callbackListeners) {
            callbackListener.beforeUpdate(this);
        }
        boolean result = super.update();
        for (CallbackListener callbackListener : callbackListeners) {
            callbackListener.afterUpdate(this);
        }
        return result;
    }

    @Override
    public boolean delete() {
        for (CallbackListener callbackListener : callbackListeners) {
            callbackListener.beforeDelete(this);
        }
        boolean result = super.delete();
        for (CallbackListener callbackListener : callbackListeners) {
            callbackListener.afterDelete(this);
        }
        return result;

    }

    @Override
    public boolean deleteById(Object id) {
        for (CallbackListener callbackListener : callbackListeners) {
            callbackListener.beforeDelete(this);
        }
        boolean result = super.deleteById(id);
        for (CallbackListener callbackListener : callbackListeners) {
            callbackListener.afterDelete(this);
        }
        return result;
    }

    public int deleteAll() {
        String[] primaryKey = TableMapping.me().getTable(clazz).getPrimaryKey();
        return Db.update("delete from " + tableName() + " where " + primaryKey[0] + "=?");
    }

    public int deleteByColumn(String column, Object value) {
        return deleteByColumns(new String[]{column}, value);
    }
    public int deleteByColumns(String[] columns, Object... values) {
        Preconditions.checkArgument(columns.length > 0, "columns is empty");
        Preconditions.checkArgument(values.length > 0, "values is empty");
        Preconditions.checkArgument(values.length == columns.length, "column size != values size");
        StringBuilder sb = new StringBuilder();
        Table tableInfo = TableMapping.me().getTable(clazz);
        sb.append("delete from ").append(tableInfo.getName());
        sb.append(" where 1=1");
        for (String column : columns) {
            sb.append(" and ").append(column).append(" = ?");
        }
        return Db.update(sb.toString(), values);
    }

    public int deleteByColumns(List<String> columns, List<Object> values) {
        return deleteByColumns(columns.toArray(new String[columns.size()]),values.toArray());
    }

    public List<M> findAll() {
        StringBuilder sb = new StringBuilder("select * from ").append(tableName());
        return find(sb.toString());
    }

    public M findFirstByColumn(String column, Object value) {
        return findFirstByColumns(new String[]{column},new Object[]{value});
    }

    public List<M> findByColumn(String column, Object value) {
        return findByColumns(Lists.newArrayList(column), Lists.newArrayList(value));
    }


    public M findFirstByColumns(String[] columns,Object... objs){
        List<M> result = findByColumns(true,columns,objs);
        return CollectionUtils.isEmpty(result)?null:result.get(0);
    }

    public M findFirstByColumns(List<String> columns, List<Object> values) {
        return findFirstByColumns(columns.toArray(new String[columns.size()]),values.toArray());
    }

    private List<M> findByColumns(boolean isFirst,String[] columns, Object... values) {
        Preconditions.checkArgument(columns.length > 0, "columns is empty");
        Preconditions.checkArgument(values.length > 0, "values is empty");
        Preconditions.checkArgument(values.length == columns.length, "column length != values length");
        StringBuilder sb = new StringBuilder("select * from ");
        sb.append(tableName()).append(" where 1=1");
        for (String column : columns) {
            sb.append(" and ").append(column).append(" = ?");
        }
        if(isFirst){
            sb.append(" limit 1");
        }
        return find(sb.toString(),values);
    }
    public List<M> findByColumns(String[] columns, Object... values){
        return findByColumns(false, columns, values);
    }
    public List<M> findByColumns(List<String> columns, List<Object> values) {
        return findByColumns(columns.toArray(new String[columns.size()]),values.toArray());
    }


    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<M> children(Class<? extends Model> model) {
        Parent child = model.getAnnotation(Parent.class);
        String foreignKey = child.foreignKey();
        Class<? extends Model> childModel = child.model();
        String childTableName = TableMapping.me().getTable(childModel).getName();
        String[] primaryKey = TableMapping.me().getTable(clazz).getPrimaryKey();
        try {
            return childModel.newInstance().find("select * from " + childTableName + " where " + foreignKey + "= ?",
                    get(primaryKey[0]));
        } catch (Exception e) {
            throw new ActiveRecordException(e.getMessage(), e);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public M parent(Class<? extends Model> model) {
        Parent parent = model.getAnnotation(Parent.class);
        String foreignKey = parent.foreignKey();
        Class<? extends Model> parentModel = parent.model();
        String parentTableName = TableMapping.me().getTable(parentModel).getName();
        String[] primaryKey = TableMapping.me().getTable(clazz).getPrimaryKey();
        try {
            return (M) parentModel.newInstance().findFirst(
                    "select * from " + parentTableName + " where " + foreignKey + "= ?", get(primaryKey[0]));
        } catch (Exception e) {
            throw new ActiveRecordException(e.getMessage(), e);
        }
    }

    private String tableName() {
        return TableMapping.me().getTable(clazz).getName();
    }

    public boolean isNull(String attr){
        return get(attr)==null;
    }
    public boolean isNotNull(String attr){
        return get(attr)!=null;
    }
    public boolean isBlank(String attr){
        return StrKit.isBlank(getStr(attr));
    }
    public boolean isNotBlank(String attr){
        return StringUtils.isNotBlank(getStr(attr));
    }

    public <T> T getPk(){
        String[] primaryKey = TableMapping.me().getTable(clazz).getPrimaryKey();
        return get(primaryKey[0]);
    }
    public boolean isNew(){
        return this==null?true:getPk()==null;
    }
    public M setDate(String attr){
        return set(attr,new Date());
    }
    public M setDate(String attr,Date date){
        return set(attr,date);
    }
}
