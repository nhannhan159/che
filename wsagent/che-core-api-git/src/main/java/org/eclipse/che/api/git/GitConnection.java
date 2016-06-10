/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.api.git;

import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.UnauthorizedException;
import org.eclipse.che.api.core.util.LineConsumerFactory;
import org.eclipse.che.api.git.params.AddParams;
import org.eclipse.che.api.git.params.CheckoutParams;
import org.eclipse.che.api.git.params.CloneParams;
import org.eclipse.che.api.git.params.CommitParams;
import org.eclipse.che.api.git.params.DiffParams;
import org.eclipse.che.api.git.shared.AddRequest;
import org.eclipse.che.api.git.shared.Branch;
import org.eclipse.che.api.git.shared.CheckoutRequest;
import org.eclipse.che.api.git.shared.BranchCreateRequest;
import org.eclipse.che.api.git.shared.BranchDeleteRequest;
import org.eclipse.che.api.git.shared.BranchListRequest;
import org.eclipse.che.api.git.shared.CloneRequest;
import org.eclipse.che.api.git.shared.CommitRequest;
import org.eclipse.che.api.git.shared.DiffRequest;
import org.eclipse.che.api.git.shared.FetchRequest;
import org.eclipse.che.api.git.shared.GitUser;
import org.eclipse.che.api.git.shared.InitRequest;
import org.eclipse.che.api.git.shared.LogRequest;
import org.eclipse.che.api.git.shared.LsFilesRequest;
import org.eclipse.che.api.git.shared.LsRemoteRequest;
import org.eclipse.che.api.git.shared.MergeRequest;
import org.eclipse.che.api.git.shared.MergeResult;
import org.eclipse.che.api.git.shared.MoveRequest;
import org.eclipse.che.api.git.shared.PullRequest;
import org.eclipse.che.api.git.shared.PullResponse;
import org.eclipse.che.api.git.shared.PushRequest;
import org.eclipse.che.api.git.shared.PushResponse;
import org.eclipse.che.api.git.shared.Remote;
import org.eclipse.che.api.git.shared.RemoteAddRequest;
import org.eclipse.che.api.git.shared.RemoteListRequest;
import org.eclipse.che.api.git.shared.RemoteReference;
import org.eclipse.che.api.git.shared.RemoteUpdateRequest;
import org.eclipse.che.api.git.shared.ResetRequest;
import org.eclipse.che.api.git.shared.RebaseRequest;
import org.eclipse.che.api.git.shared.RebaseResponse;
import org.eclipse.che.api.git.shared.Revision;
import org.eclipse.che.api.git.shared.RmRequest;
import org.eclipse.che.api.git.shared.ShowFileContentRequest;
import org.eclipse.che.api.git.shared.ShowFileContentResponse;
import org.eclipse.che.api.git.shared.Status;
import org.eclipse.che.api.git.shared.StatusFormat;
import org.eclipse.che.api.git.shared.Tag;
import org.eclipse.che.api.git.shared.TagCreateRequest;
import org.eclipse.che.api.git.shared.TagDeleteRequest;
import org.eclipse.che.api.git.shared.TagListRequest;

import javax.ws.rs.QueryParam;
import java.io.Closeable;
import java.io.File;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 * Connection to Git repository.
 *
 * @author andrew00x
 */
public interface GitConnection extends Closeable {
    File getWorkingDir();

    /**
     * Add content of working tree to Git index. This action prepares content to next commit.
     *
     * @param params
     *         add params
     * @throws GitException
     *         if any error occurs when add files to the index
     * @see AddRequest
     */
    void add(AddParams params) throws GitException;

    /**
     * Checkout a branch / file to the working tree.
     *
     * @param params
     *         checkout params
     * @throws GitException
     *         if any error occurs when checkout
     * @see CheckoutRequest
     */
    void checkout(CheckoutParams params) throws GitException;

    /**
     * Perform clone with sparse-checkout to specified directory.
     *
     * @param directory
     *         path to keep in working tree
     * @param remoteUrl
     *         url to clone
     * @param branch
     *         branch to checkout
     * @throws GitException
     * @throws UnauthorizedException
     *         if any error occurs when add files to the index
     */
    void cloneWithSparseCheckout(String directory, String remoteUrl, String branch) throws GitException, UnauthorizedException;

    /**
     * Create new branch.
     *
     * @param request
     *         create branch request
     * @return newly created branch
     * @throws GitException
     *         if any error occurs when create branch
     * @see BranchCreateRequest
     */
    Branch branchCreate(BranchCreateRequest request) throws GitException;

    /**
     * Delete branch.
     *
     * @param name
     *         name of the branch to delete
     * @param force
     *          <code>true</code> if need to delete with force
     * @throws GitException
     *         if any error occurs when deleting branch
     */
    void branchDelete(String name, boolean force) throws GitException, UnauthorizedException;

    /**
     * Rename branch.
     *
     * @param oldName
     *         current name of branch
     * @param newName
     *         new name of branch
     * @throws GitException
     *         if any error occurs when delete branch
     */
    void branchRename(String oldName, String newName) throws GitException, UnauthorizedException;

    /**
     * List branches.
     *
     * @param listMode
     *         specifies mode of listing branches
     * @return list of branches
     * @throws GitException
     *         if any error occurs
     * @throws IllegalArgumentException
     *         if {@param listMode} is <code>null</code> or not 'a' or 'r'
     * @see BranchListRequest
     */
    List<Branch> branchList(String listMode) throws GitException;

    /**
     * Show information about files in the index and the working tree
     *
     * @param request
     *         list files request
     * @return list of files.
     * @throws GitException
     *         if any error occurs
     */
    List<String> listFiles(LsFilesRequest request) throws GitException;

    /**
     * Clone repository.
     *
     * @param params
     *         clone params
     * @throws URISyntaxException
     *         if {@link CloneRequest#getRemoteUri()} return invalid value
     * @throws GitException
     *         if any other error occurs
     * @see CloneRequest
     */
    void clone(CloneParams params) throws URISyntaxException, ServerException, UnauthorizedException;

    /**
     * Commit current state of index in new commit.
     *
     * @param params
     *         commit params
     * @return new commit
     * @throws GitException
     *         if any error occurs
     * @throws IllegalArgumentException
     *         if {@link CommitRequest#getMessage()} returns <code>null</code>
     * @see CommitRequest
     */
    Revision commit(CommitParams params) throws GitException;

    /**
     * Show diff between commits.
     *
     * @param params
     *         diff params
     * @return diff page. Diff info can be serialized to stream by using method {@link DiffPage#writeTo(java.io.OutputStream)}
     * @throws GitException
     *         if any error occurs
     * @see DiffPage
     * @see DiffRequest
     */
    DiffPage diff(DiffParams params) throws GitException;

    /**
     * Show content of the file from specified revision or branch.
     *
     * @param request
     *         request with file and hash of revision or branch
     * @return response with content of the file
     * @throws GitException
     *         if any error occurs
     * @see ShowFileContentRequest
     * @see ShowFileContentResponse
     */
    ShowFileContentResponse showFileContent(String file, String version) throws GitException;

    /**
     * Fetch data from remote repository.
     *
     * @param request
     *         fetch request
     * @throws GitException
     *         if any error occurs
     * @see FetchRequest
     */
    void fetch(FetchRequest request) throws UnauthorizedException, GitException;

    /**
     * Initialize new Git repository.
     *
     * @param request
     *         init request
     * @throws GitException
     *         if any error occurs
     * @see InitRequest
     */
    void init(InitRequest request) throws GitException;

    /**
     * Check if directory, which was used to create Git connection, is inside the working tree.
     *
     * @return <b>true</b> if only directory is inside working tree, and <b>false</b> if directory is outside the working tree including directory inside .git directory, or bare repository. 
     *
     * @throws GitException
     *         if any error occurs
     */
    boolean isInsideWorkTree() throws GitException;

    /**
     * Get commit logs.
     *
     * @param request
     *         log request
     * @return log page. Logs can be serialized to stream by using method {@link DiffPage#writeTo(java.io.OutputStream)}
     * @throws GitException
     *         if any error occurs
     * @see LogRequest
     */
    LogPage log(LogRequest request) throws GitException;

    /**
     * List references in a remote repository.
     *
     * @param request
     *         ls-remote request
     * @return list references in a remote repository.
     * @throws GitException
     *         if any error occurs
     * @see LsRemoteRequest
     */
    List<RemoteReference> lsRemote(LsRemoteRequest request) throws UnauthorizedException, GitException;

    /**
     * Merge commits.
     *
     * @param request
     *         merge request
     * @return result of merge
     * @throws IllegalArgumentException
     *         if {@link MergeRequest#getCommit()} returns invalid value, e.g. there is no specified commit
     * @throws GitException
     *         if any error occurs
     * @see MergeRequest
     */
    MergeResult merge(MergeRequest request) throws GitException;

    /**
     * Rebase on a branch
     *
     * @param request
     *         rebase request
     * @throws GitException
     *         if any error occurs when checkout
     * @see RebaseRequest
     */
    RebaseResponse rebase(RebaseRequest request) throws GitException;

    /**
     * Move or rename file or directory.
     *
     * @param request
     *         move request
     * @throws IllegalArgumentException
     *         if {@link MoveRequest#getSource()} or {@link MoveRequest#getTarget()} returns invalid value, e.g.
     *         there is not specified source or specified target already exists
     * @throws GitException
     *         if any error occurs
     * @see MoveRequest
     */
    void mv(MoveRequest request) throws GitException;

    /**
     * Pull (fetch and merge at once) changes from remote repository to local branch.
     *
     * @param request
     *         pull request
     * @throws GitException
     *         if any error occurs
     * @throws IllegalArgumentException
     *         if remote configuration is invalid
     * @see PullRequest
     */
    PullResponse pull(PullRequest request) throws GitException, UnauthorizedException;

    /**
     * Send changes from local repository to remote one.
     *
     * @param request
     *         push request
     * @throws GitException
     *         if any error occurs
     * @throws IllegalArgumentException
     *         if remote configuration is invalid
     * @see PushRequest
     */
    PushResponse push(PushRequest request) throws GitException, UnauthorizedException;

    /**
     * Add new remote configuration.
     *
     * @param request
     *         add remote configuration request
     * @throws GitException
     *         if any error occurs
     * @throws IllegalArgumentException
     *         if remote (see {@link RemoteAddRequest#getName()}) already exists or any updated parameter (e.g.
     *         URLs) invalid
     * @see RemoteAddRequest
     */
    void remoteAdd(RemoteAddRequest request) throws GitException;

    /**
     * Remove the remote named <code>name</code>. All remote tracking branches and configuration settings for the remote are removed.
     *
     * @param name
     *         remote configuration to remove
     * @throws GitException
     *         if any error occurs
     * @throws IllegalArgumentException
     *         if remote <code>name</code> not found
     */
    void remoteDelete(String name) throws GitException;

    /**
     * Show remotes.
     *
     * @param request
     *         remote list request
     * @throws GitException
     *         if any error occurs
     * @throws IllegalArgumentException
     *         if remote <code>name</code> not found
     * @see RemoteListRequest
     */
    List<Remote> remoteList(RemoteListRequest request) throws GitException;

    /**
     * Update remote configuration.
     *
     * @param request
     *         update remote configuration request
     * @throws GitException
     *         if any error occurs
     * @throws IllegalArgumentException
     *         if remote configuration (see {@link RemoteUpdateRequest#getName()}) not found or any updated
     *         parameter (e.g. URLs) invalid
     * @see RemoteUpdateRequest
     */
    void remoteUpdate(RemoteUpdateRequest request) throws GitException;

    /**
     * Reset current HEAD to the specified state.
     *
     * @param request
     *         reset request
     * @throws GitException
     *         if any error occurs
     * @throws IllegalArgumentException
     *         if reset type or commit is invalid
     * @see ResetRequest
     * @see ResetRequest#getCommit()
     * @see ResetRequest#getType()
     */
    void reset(ResetRequest request) throws GitException;

    /**
     * Remove files.
     *
     * @param request
     *         remove request
     * @throws GitException
     *         if any error occurs
     * @throws IllegalArgumentException
     *         {@link RmRequest#getFiles()} returns <code>null</code> or empty array
     * @see RmRequest
     */
    void rm(RmRequest request) throws GitException;

    /**
     * Get status of working tree.
     *
     * @param format
     *         the format of the ouput
     * @return status.
     * @throws GitException
     *         if any error occurs
     */
    Status status(StatusFormat format) throws GitException;

    /**
     * Create new tag.
     *
     * @param request
     *         tag create request
     * @throws GitException
     *         if any error occurs
     * @throws IllegalArgumentException
     *         if tag name ( {@link TagCreateRequest#getName()} ) is invalid or <code>null</code>
     * @see TagCreateRequest
     */
    Tag tagCreate(TagCreateRequest request) throws GitException;

    /**
     * @param request
     *         delete tag request
     * @throws GitException
     *         if any error occurs
     * @throws IllegalArgumentException
     *         if there is tag with specified name (see {@link TagDeleteRequest#getName()})
     * @see TagDeleteRequest
     */
    void tagDelete(TagDeleteRequest request) throws GitException;

    /**
     * Get list of available tags.
     *
     * @param request
     *         tag list request
     * @return list of tags matched to request, see {@link TagListRequest#getPattern()}
     * @throws GitException
     *         if any error occurs
     * @see TagListRequest
     */
    List<Tag> tagList(TagListRequest request) throws GitException;

    /**
     * Gel list of commiters in current repository.
     *
     * @return list of commiters
     * @throws GitException
     */
    List<GitUser> getCommiters() throws GitException;

    /** Get configuration. */
    Config getConfig() throws GitException;

    /** Close connection, release associated resources. */
    @Override
    void close();

    /** Set publisher for git output, e.g. for sending git command output to the client side. */
    void setOutputLineConsumerFactory(LineConsumerFactory outputPublisherFactory);
}
