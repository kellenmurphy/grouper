package edu.internet2.middleware.grouper.ext.org.apache.ddlutils.platform.derby;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.sql.SQLException;
import java.util.Map;

import edu.internet2.middleware.grouper.ext.org.apache.ddlutils.Platform;
import edu.internet2.middleware.grouper.ext.org.apache.ddlutils.model.Column;
import edu.internet2.middleware.grouper.ext.org.apache.ddlutils.model.ForeignKey;
import edu.internet2.middleware.grouper.ext.org.apache.ddlutils.model.Index;
import edu.internet2.middleware.grouper.ext.org.apache.ddlutils.model.Table;
import edu.internet2.middleware.grouper.ext.org.apache.ddlutils.model.TypeMap;
import edu.internet2.middleware.grouper.ext.org.apache.ddlutils.platform.DatabaseMetaDataWrapper;
import edu.internet2.middleware.grouper.ext.org.apache.ddlutils.platform.JdbcModelReader;

/**
 * Reads a database model from a Derby database.
 *
 * @version $Revision: $
 */
public class DerbyModelReader extends JdbcModelReader
{
    /**
     * Creates a new model reader for Derby databases.
     * 
     * @param platform The platform that this model reader belongs to
     */
    public DerbyModelReader(Platform platform)
    {
        super(platform);
    }
    
    /**
     * {@inheritDoc}
     */
    protected Column readColumn(DatabaseMetaDataWrapper metaData, Map values) throws SQLException
    {
        Column column       = super.readColumn(metaData, values);
        String defaultValue = column.getDefaultValue();

        if (defaultValue != null)
        {
            // we check for these strings
            //   GENERATED_BY_DEFAULT               -> 'GENERATED BY DEFAULT AS IDENTITY'
            //   AUTOINCREMENT: start 1 increment 1 -> 'GENERATED ALWAYS AS IDENTITY'
            if ("GENERATED_BY_DEFAULT".equals(defaultValue) || defaultValue.startsWith("AUTOINCREMENT:"))
            {
                column.setDefaultValue(null);
                column.setAutoIncrement(true);
            }
            else if (TypeMap.isTextType(column.getTypeCode()))
            {
                column.setDefaultValue(unescape(defaultValue, "'", "''"));
            }
        }
        return column;
    }

    /**
     * {@inheritDoc}
     */
    protected boolean isInternalForeignKeyIndex(DatabaseMetaDataWrapper metaData, Table table, ForeignKey fk, Index index)
    {
        return isInternalIndex(index);
    }

    /**
     * {@inheritDoc}
     */
    protected boolean isInternalPrimaryKeyIndex(DatabaseMetaDataWrapper metaData, Table table, Index index)
    {
        return isInternalIndex(index);
    }

    /**
     * Determines whether the index is an internal index, i.e. one created by Derby.
     * 
     * @param index The index to check
     * @return <code>true</code> if the index seems to be an internal one
     */
    private boolean isInternalIndex(Index index)
    {
        String name = index.getName();

        // Internal names normally have the form "SQL051228005030780"
        if ((name != null) && name.startsWith("SQL"))
        {
            try
            {
                Long.parseLong(name.substring(3));
                return true;
            }
            catch (NumberFormatException ex)
            {
                // we ignore it
            }
        }
        return false;
    }
}
