package org.apache.maven.scm.provider.svn.svnjava.command.branch;

import org.apache.maven.scm.ScmBranch;
import org.apache.maven.scm.ScmBranchParameters;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFile;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmFileStatus;
import org.apache.maven.scm.ScmResult;
import org.apache.maven.scm.command.branch.AbstractBranchCommand;
import org.apache.maven.scm.command.branch.BranchScmResult;
import org.apache.maven.scm.command.tag.TagScmResult;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.svn.SvnTagBranchUtils;
import org.apache.maven.scm.provider.svn.command.SvnCommand;
import org.apache.maven.scm.provider.svn.repository.SvnScmProviderRepository;
import org.apache.maven.scm.provider.svn.svnjava.SvnJavaScmProvider;
import org.apache.maven.scm.provider.svn.svnjava.repository.SvnJavaScmProviderRepository;
import org.apache.maven.scm.provider.svn.svnjava.util.SvnJavaUtil;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * @author <a href="mailto:olamy@apache.org">olamy</a>
 */
public class SvnJavaBranchCommand
    extends AbstractBranchCommand
    implements SvnCommand
{

    /**
     * @see org.apache.maven.scm.command.branch.AbstractBranchCommand#executeBranchCommand(org.apache.maven.scm.provider.ScmProviderRepository, org.apache.maven.scm.ScmFileSet, java.lang.String, java.lang.String)
     */
    protected ScmResult executeBranchCommand( ScmProviderRepository repo, ScmFileSet fileSet, String branch,
                                              ScmBranchParameters scmBranchParameters )
        throws ScmException
    {
        if ( branch == null || StringUtils.isEmpty( branch.trim() ) )
        {
            throw new ScmException( "branch name must be specified" );
        }

        if ( !fileSet.getFileList().isEmpty() )
        {
            throw new ScmException( "This provider doesn't support branching subsets of a directory" );
        }
        SvnScmProviderRepository repository = (SvnScmProviderRepository) repo;

        SvnJavaScmProviderRepository javaRepo = (SvnJavaScmProviderRepository) repo;
        try
        {

            SVNURL destURL =
                SVNURL.parseURIEncoded( SvnTagBranchUtils.resolveBranchUrl( repository, new ScmBranch( branch ) ) );

            String message = "[maven-scm] copy for branch " + branch;
            if ( scmBranchParameters != null )
            {
                message = scmBranchParameters.getMessage();
            }

            SVNCommitInfo info;
            if ( scmBranchParameters != null && scmBranchParameters.isRemoteBranching() )
            {
                info = SvnJavaUtil.copy( javaRepo.getClientManager(), javaRepo.getSvnUrl(), destURL, false, message,
                                         null );
            }
            else
            {
                info = SvnJavaUtil.copy( javaRepo.getClientManager(), fileSet.getBasedir(), destURL, false, message,
                                         null );
            }

            if ( info.getErrorMessage() != null )
            {
                return new TagScmResult( SvnJavaScmProvider.COMMAND_LINE, "SVN tag failed.",
                                         info.getErrorMessage().getMessage(), false );
            }


            List<File> files;

            try
            {
                files = FileUtils.getFiles( fileSet.getBasedir(), "**", "**/.svn/**", false );
            }
            catch ( IOException e )
            {
                throw new ScmException( "Error while executing command.", e );
            }

            List<ScmFile> fileList = files.stream()
                    .map(f -> new ScmFile( f.getPath(), ScmFileStatus.TAGGED ))
                    .collect(Collectors.toList());

            return new BranchScmResult( SvnJavaScmProvider.COMMAND_LINE, fileList );
        }
        catch ( SVNException e )
        {
            return new BranchScmResult( SvnJavaScmProvider.COMMAND_LINE, "SVN tag failed.", e.getMessage(), false );
        }
    }

    /**
     * {@inheritDoc}
     */
    public ScmResult executeBranchCommand( ScmProviderRepository repo, ScmFileSet fileSet, String branch,
                                           String message )
        throws ScmException
    {
        ScmBranchParameters scmBranchParameters = new ScmBranchParameters( message );
        return executeBranchCommand( repo, fileSet, branch, scmBranchParameters );
    }
}
