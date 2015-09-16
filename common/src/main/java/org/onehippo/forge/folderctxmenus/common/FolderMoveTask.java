/*
 * Copyright 2015 Hippo B.V. (http://www.onehippo.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onehippo.forge.folderctxmenus.common;

import java.util.Locale;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;
import org.hippoecm.repository.util.JcrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FolderMoveTask extends AbstractFolderCopyOrMoveTask {

    private static Logger log = LoggerFactory.getLogger(FolderMoveTask.class);

    public FolderMoveTask(final Session session, final Locale locale, final Node sourceFolderNode,
            final Node destParentFolderNode, final String destFolderNodeName, final String destFolderDisplayName) {
        super(session, locale, sourceFolderNode, destParentFolderNode, destFolderNodeName, destFolderDisplayName);
    }

    @Override
    protected void doExecute() throws RepositoryException {
        if (getSourceFolderNode().getParent().isSame(getDestParentFolderNode())) {
            if (StringUtils.equals(getSourceFolderNode().getName(), getDestFolderNodeName())) {
                throw new RuntimeException("Cannot move to the same folder: " + getDestFolderPath());
            }
        }

        if (getSourceFolderNode().isSame(getDestParentFolderNode())) {
            throw new RuntimeException("Cannot move to the folder itself: " + getDestFolderPath());
        }

        if (getSession().nodeExists(getDestFolderPath())) {
            throw new RuntimeException("Destination folder already exists: " + getDestFolderPath());
        }

        getSession().move(getSourceFolderNode().getPath(), getDestFolderPath());

        setDestFolderNode(JcrUtils.getNodeIfExists(getDestParentFolderNode(), getDestFolderNodeName()));

        updateFolderTranslations(getDestFolderNode(), getDestFolderNodeName(), getLocale().getLanguage());

        getSession().save();
    }

}