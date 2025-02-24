package edu.internet2.middleware.grouper.ext.org.apache.ddlutils.platform.cloudscape;

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

import java.io.IOException;

import edu.internet2.middleware.grouper.ext.org.apache.ddlutils.Platform;
import edu.internet2.middleware.grouper.ext.org.apache.ddlutils.model.Column;
import edu.internet2.middleware.grouper.ext.org.apache.ddlutils.model.Table;
import edu.internet2.middleware.grouper.ext.org.apache.ddlutils.platform.SqlBuilder;

/**
 * The SQL Builder for Cloudscape.
 * 
 * @version $Revision: 463757 $
 */
public class CloudscapeBuilder extends SqlBuilder
{
    /**
     * Creates a new builder instance.
     * 
     * @param platform The plaftform this builder belongs to
     */
    public CloudscapeBuilder(Platform platform)
    {
        super(platform);
        addEscapedCharSequence("'", "''");
    }

    /**
     * {@inheritDoc}
     */
    protected void writeColumnAutoIncrementStmt(Table table, Column column) throws IOException
    {
        print("GENERATED ALWAYS AS IDENTITY");
    }

    /**
     * {@inheritDoc}
     */
    public String getSelectLastIdentityValues(Table table)
    {
        return "VALUES IDENTITY_VAL_LOCAL()";
    }
}
