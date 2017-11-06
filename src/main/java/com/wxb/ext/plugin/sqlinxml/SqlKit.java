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
package com.wxb.ext.plugin.sqlinxml;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

import com.google.common.collect.Lists;
import com.wxb.ext.kit.JaxbKit;
import com.jfinal.kit.PathKit;
import com.jfinal.log.Log;
import org.apache.commons.lang3.ArrayUtils;

public class SqlKit {

    protected static final Log LOG = Log.getLog(SqlKit.class);

    private static Map<String, String> sqlMap;

    public static String sql(String groupNameAndsqlId) {
        if (sqlMap == null) {
            throw new NullPointerException("SqlInXmlPlugin not start");
        }
        return sqlMap.get(groupNameAndsqlId);
    }

    static void clearSqlMap() {
        sqlMap.clear();
    }

    static void init() {
        sqlMap = new HashMap<String, String>();
        File file = new File(PathKit.getRootClassPath());
        List<File[]> fileList = Lists.newLinkedList();
        readFile(file,fileList);
        for(File[] fileArr : fileList){
            for (File xmlfile : fileArr) {
                SqlGroup group = JaxbKit.unmarshal(xmlfile, SqlGroup.class);
                String name = group.name;
                if (name == null || name.trim().equals("")) {
                    name = xmlfile.getName();
                }
                for (SqlItem sqlItem : group.sqlItems) {
                    sqlMap.put(name + "." + sqlItem.id, sqlItem.value);
                }
            }
        }


        LOG.debug("sqlMap" + sqlMap);
    }
    static void readFile(File file,List<File[]> fileList){
        if(file== null) return;
        File[] files = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith("sql.xml");
            }
        });
        if(ArrayUtils.isNotEmpty(files)) {
            fileList.add(files);
        }
        File[] dir_files = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File child) {
                return child.isDirectory();
            }
        });
        for(File dir : dir_files){
            readFile(dir,fileList);
        }
    }
}
