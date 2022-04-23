package org.apache.maven.scm.provider.svn.svnjava.command.diff;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;

import org.apache.maven.scm.provider.svn.command.diff.SvnDiffCommandTckTest;
import org.apache.maven.scm.provider.svn.svnjava.SvnJavaScmTestUtils;

/**
 * @author <a href="mailto:dh-maven@famhq.com">David Hawkins</a>
 * @version $Id: SvnJavaDiffCommandTckTest.java 98 2009-03-25 23:49:02Z oliver.lamy $
 */
public class SvnJavaDiffCommandTckTest
    extends SvnDiffCommandTckTest
{
    /** {@inheritDoc} */
    public void initRepo()
        throws Exception
    {
        SvnJavaScmTestUtils.initializeRepository( getRepositoryRoot() );
    }

    /** {@inheritDoc} */
    public String getScmUrl()
        throws Exception
    {
        return SvnJavaScmTestUtils.getScmUrl( new File( getRepositoryRoot(), "trunk" ) );
    }

    @Override
    protected File getWorkingDirectory() {
        return super.getWorkingDirectory();
    }
}
