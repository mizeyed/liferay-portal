<%--
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@ include file="/init.jsp" %>

<%
String cssClass = GetterUtil.getString(request.getAttribute("liferay-staging:status:cssClass"));
StagedModel stagedModel = (StagedModel)request.getAttribute("liferay-staging:status:stagedModel");

Date lastPublishDate = null;
Date modifiedDate = null;

if (stagedModel != null) {
	lastPublishDate = stagedModel.getLastPublishDate();
	modifiedDate = stagedModel.getModifiedDate();
}

boolean published = false;

Group themeDisplayScopeGroup = themeDisplay.getScopeGroup();

boolean stagedPortlet = group.isInStagingPortlet(portletDisplay.getId());

if (stagedPortlet) {
	if (lastPublishDate == null) {
		lastPublishDate = modifiedDate;
	}

	if ((lastPublishDate != null) && lastPublishDate.after(modifiedDate)) {
		published = true;
	}

	if (Validator.isNull(cssClass)) {
		cssClass = "label-success";

		if (!published) {
			cssClass = "label-warning";
		}
	}
}
%>