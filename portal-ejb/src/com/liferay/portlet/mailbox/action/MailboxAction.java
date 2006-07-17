/**
 * Copyright (c) 2000-2006 Liferay, LLC. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.liferay.portlet.mailbox.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.json.JSONArray;
import org.json.JSONObject;

import com.liferay.portal.struts.JSONAction;
import com.liferay.portal.util.Constants;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.mailbox.util.MailEnvelope;
import com.liferay.portlet.mailbox.util.MailFolder;
import com.liferay.portlet.mailbox.util.MailMessage;
import com.liferay.portlet.mailbox.util.MailUtil;
import com.liferay.util.ParamUtil;
import com.liferay.util.StringUtil;

/**
 * <a href="MailboxAction.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Ming-Gih Lam
 *
 */
public class MailboxAction extends JSONAction {

	public String getJSON(
			ActionMapping mapping, ActionForm form, HttpServletRequest req,
			HttpServletResponse res)
		throws Exception {

		String cmd = ParamUtil.getString(req, Constants.CMD);
		String rtString = "";
		
		if ("getFolders".equals(cmd)) {
			rtString = _getFolders(req);
		}
		else if ("getPreview".equals(cmd)) {
			rtString = _getPreviewHeaders(req);
		}
		else if ("getMessage".equals(cmd)) {
			rtString = _getMessage(req);
		}
		else if ("moveMessages".equals(cmd)) {
			_moveMessages(req);
		}
		else if ("deleteMessages".equals(cmd)) {
			_deleteMessages(req);
		}
		
		return rtString;
	}
	
	private String _deleteMessages(HttpServletRequest req) {
		try {
			String messages = ParamUtil.getString(req, "messages");
			long msgList[] = StringUtil.split(messages, ",", -1L);
			
			String userId = PortalUtil.getUserId(req);
			String password = PortalUtil.getUserPassword(req);
			
			MailUtil.deleteMessages(userId, password, msgList);
		}
		catch (Exception e) {
		}
		
		return null;
	}
	
	private String _getFolders(HttpServletRequest req) {
		JSONObject jsonObj = new JSONObject();
		JSONArray jFolders = new JSONArray();
		
		try {
			int count = 1;

			String userId = PortalUtil.getUserId(req);
			String password = PortalUtil.getUserPassword(req);

			List folders = MailUtil.getAllFolders(userId, password);
			
			for (int i = 0; i < folders.size(); i++) {
				MailFolder folderObj = (MailFolder)folders.get(i);
				JSONObject jFolderObj = new JSONObject();;
				
				String folderName = folderObj.getName();
				
				jFolderObj.put("name", folderName);
				jFolderObj.put("id", folderName);
				jFolderObj.put("newCount", folderObj.getNewMessageCount());
				jFolderObj.put("totalCount", folderObj.getMessageCount());
				
				if (MailUtil.MAIL_INBOX_NAME.equals(folderName)) {
					jFolders.put(0, jFolderObj);
				}
				else {
					jFolders.put(count++, jFolderObj);
				}
			}
		}
		catch (Exception e) {
		}
		
		jsonObj.put("folders", jFolders);
		
		return jsonObj.toString();
	}
	
	private String _getMessage(HttpServletRequest req) {
		JSONObject jsonObj = new JSONObject();
		
		try {
			String userId = PortalUtil.getUserId(req);
			String password = PortalUtil.getUserPassword(req);
			String folderId = ParamUtil.getString(req, "folderId");			
			int messageId = ParamUtil.getInteger(req, "messageId");

			MailUtil.setCurrentFolder(userId, password, folderId);
			
			MailMessage mm = MailUtil.getMessage(userId, messageId);
			jsonObj.put("body", mm.getHtmlBody());
			jsonObj.put("id", messageId);
		}
		catch (Exception e) {
		}
		
		return jsonObj.toString();
	}
	
	private String _getPreviewHeaders(HttpServletRequest req) {
		JSONObject jsonObj = new JSONObject();
			
		try {
			String userId = PortalUtil.getUserId(req);
			String password = PortalUtil.getUserPassword(req);
			String folderId = ParamUtil.getString(req, "folderId");			

			MailUtil.setCurrentFolder(userId, password, folderId);

			List list = MailUtil.getEnvelopes(userId);
			
			JSONArray meArray = new JSONArray();
			
			for (int i = 0; i < list.size(); i++) {
				MailEnvelope me = (MailEnvelope)list.get(i);
				JSONObject jMe = new JSONObject();
				
				jMe.put("id", me.getMsgUID());
				jMe.put("email", me.getEmail());
				jMe.put("subject", me.getSubject());
				jMe.put("date", me.getDate().toString());
				meArray.put(jMe);
			}
			
			jsonObj.put("headers", meArray);
		}
		catch (Exception e) {
		}
		
		return jsonObj.toString();
	}
	
	private String _moveMessages(HttpServletRequest req) {
		try {
			String userId = PortalUtil.getUserId(req);
			String password = PortalUtil.getUserPassword(req);
			String messages = ParamUtil.getString(req, "messages");
			String toFolder = ParamUtil.getString(req, "folderId");
			long msgList[] = StringUtil.split(messages, ",", -1L);
			
			MailUtil.moveMessages(userId, password, msgList, toFolder);
		}
		catch (Exception e) {
		}
		
		return null;
	}

}