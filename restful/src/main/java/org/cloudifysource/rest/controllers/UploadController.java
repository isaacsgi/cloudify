/*******************************************************************************
 * Copyright (c) 2013 GigaSpaces Technologies Ltd. All rights reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.cloudifysource.rest.controllers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.cloudifysource.dsl.internal.CloudifyConstants;
import org.cloudifysource.dsl.internal.CloudifyMessageKeys;
import org.cloudifysource.dsl.rest.response.AddTemplatesInternalResponse;
import org.cloudifysource.dsl.rest.response.UploadResponse;
import org.cloudifysource.rest.repo.UploadRepo;
import org.cloudifysource.restDoclet.annotations.InternalMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


/**
 * A controller for uploading files for future deployments.
 * Each uploaded file will be available for {@link CloudifyConstants#DEFAULT_UPLOAD_TIMEOUT_MILLIS} seconds.
 * The timeout can be edited via {@link UploadRepo#setCleanupTimeoutMillis(int)}.
 * @author yael
 * @since 2.6.0
 *
 */
@Controller
@RequestMapping(value = "/{version}/upload")
public class UploadController extends BaseRestController {

    private static final Logger logger = Logger.getLogger(UploadController.class.getName());

    @Autowired
    private UploadRepo uploadRepo;

    /**
     * Uploading a file to be used in future deployments.
     * The file will be kept at least {@link UploadRepo#TIMEOUT_SECOND} seconds.
     * @param fileName
	 *            The name of the file to upload.
     * @param file
	 *            The file to upload.
     * @return {@link UploadResponse} - contains the uploaded file's name.
     * @throws RestErrorException .
     */
    @PreAuthorize("isFullyAuthenticated() and hasPermission(#authGroups, 'deploy')")
    @RequestMapping(value = "/{fileName:.+}", method = RequestMethod.POST)
    public UploadResponse upload(
            @PathVariable() final String fileName,
            @RequestParam(value = CloudifyConstants.UPLOAD_FILE_PARAM_NAME, required = true) final MultipartFile file)
            throws RestErrorException {
    	return uploadInternal(fileName, file);
    }
    
	/**
	 * Add template files to the cloud configuration directory and to the cloud object. This method supposed to be
	 * invoked by the MNG on all REST instances.
	 * 
	 * @param fileName
	 *            The name of the file to upload.
	 * @param file
	 *            The file to upload.
	 * @return {@link AddTemplatesInternalResponse}
	 * @throws RestErrorException . 
	 */
	@InternalMethod
    @RequestMapping(value = "internal/{fileName:.+}", method = RequestMethod.POST)
    public UploadResponse uploadInternal(
    		@PathVariable() final String fileName,
            @RequestParam(value = CloudifyConstants.UPLOAD_FILE_PARAM_NAME, required = true) final MultipartFile file) 
            		throws RestErrorException {
        // determine file's name
        String name = fileName;
        if (StringUtils.isEmpty(fileName)) {
            name = file.getOriginalFilename();
        }
        if (logger.isLoggable(Level.INFO)) {
            logger.info("[uploadInternal] - received request to upload file " + name);
        }
        // upload file using uploadRepo
        String uploadKey = null;
        try {
            uploadKey = uploadRepo.put(name, file);
        } catch (IOException e) {
        	if (logger.isLoggable(Level.WARNING)) {
        		logger.warning("could not upload file " + name + " error was - " + e.getMessage());
        	}
            throw new RestErrorException(
                    CloudifyMessageKeys.UPLOAD_FAILED.getName(), name, e.getMessage());
        }
        if (logger.isLoggable(Level.INFO)) {
            logger.info("[uploadInternal] - seccussfuly uploaded file " + name + " [upload key = " + uploadKey + "]");
        }
        // create and return UploadResponse
        UploadResponse response = new UploadResponse();
        response.setUploadKey(uploadKey);
        return response;
    }
}
