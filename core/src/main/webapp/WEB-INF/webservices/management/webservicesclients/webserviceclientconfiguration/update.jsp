<%-- 
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Servi�os Partilhados da
 * Universidade de Lisboa:
 *  - Copyright � 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright � 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: paulo.abrantes@qub-it.com
 *
 * 
 * This file is part of FenixEdu bennu-webservices.
 *
 * FenixEdu bennu-webservices is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu bennu-webservices is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu bennu-webservices.  If not, see <http://www.gnu.org/licenses/>.
 --%>
 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<spring:url var="datatablesUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.latest.min.js"/>
<spring:url var="datatablesBootstrapJsUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesUrl}"></script>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl" value="/CSS/dataTables/dataTables.bootstrap.min.css"/>
<link rel="stylesheet" href="${datatablesCssUrl}"/>
<spring:url var="datatablesI18NUrl" value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json"/>

<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css"/>

<link href="//cdn.datatables.net/responsive/1.0.4/css/dataTables.responsive.css" rel="stylesheet"/>
<script src="//cdn.datatables.net/responsive/1.0.4/js/dataTables.responsive.js"></script>
<link href="//cdn.datatables.net/tabletools/2.2.3/css/dataTables.tableTools.css" rel="stylesheet"/>
<script src="//cdn.datatables.net/tabletools/2.2.3/js/dataTables.tableTools.min.js"></script>
<link href="//cdnjs.cloudflare.com/ajax/libs/select2/4.0.0-rc.1/css/select2.min.css" rel="stylesheet" />
<script src="//cdnjs.cloudflare.com/ajax/libs/select2/4.0.0-rc.1/js/select2.min.js"></script>

<!-- Choose ONLY ONE:  bennuToolkit OR bennuAngularToolkit -->
<%--${portal.angularToolkit()} --%>
${portal.toolkit()}

<%-- TITLE --%>
<div class="page-header">
	<h1><spring:message code="label.management.webservicesClients.updateWebServiceClientConfiguration" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display:inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a class="" href="${pageContext.request.contextPath}/webservices/management/webservicesclients/webserviceclientconfiguration/" ><spring:message code="label.event.back" /></a>
|&nbsp;&nbsp;</div>
	<c:if test="${not empty infoMessages}">
				<div class="alert alert-info" role="alert">
					
					<c:forEach items="${infoMessages}" var="message"> 
						<p>${message}</p>
					</c:forEach>
					
				</div>	
			</c:if>
			<c:if test="${not empty warningMessages}">
				<div class="alert alert-warning" role="alert">
					
					<c:forEach items="${warningMessages}" var="message"> 
						<p>${message}</p>
					</c:forEach>
					
				</div>	
			</c:if>
			<c:if test="${not empty errorMessages}">
				<div class="alert alert-danger" role="alert">
					
					<c:forEach items="${errorMessages}" var="message"> 
						<p>${message}</p>
					</c:forEach>
					
				</div>	
			</c:if>

<form method="post" class="form-horizontal">
<div class="panel panel-default">
  <div class="panel-body">


<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.WebServiceClientConfiguration.executionContext"/></div> 

<div class="col-sm-2">
<select id="webServiceClientConfiguration_executionContext" name="executioncontext" class="form-control">
		<c:forEach items="${executionContextValues}" var="field">
				<option value='<c:out value='${field}'/>'><c:out value='${field}' /></option>
	   </c:forEach>
</select>
	<script>
		$("#webServiceClientConfiguration_executionContext").val('<c:out value='${not empty param.executioncontext ? param.executioncontext : webServiceClientConfiguration.executionContext }'/>');
	</script>	
</div>
</div>

<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.WebServiceClientConfiguration.authenticationLevel"/></div> 

<div class="col-sm-2">
<select id="webServiceClientConfiguration_authenticationLevel" name="authenticationlevel" class="form-control">
		<c:forEach items="${authenticationLevelValues}" var="field">
				<option value='<c:out value='${field}'/>'><c:out value='${field}' /></option>
	   </c:forEach>
</select>
	<script>
		$("#webServiceClientConfiguration_authenticationLevel").val('<c:out value='${not empty param.authenticationlevel ? param.authenticationlevel : webServiceClientConfiguration.authenticationLevel }'/>');
	</script>	
</div>
</div>

<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.WebServiceClientConfiguration.url"/></div> 

<div class="col-sm-10">
	<input id="webServiceClientConfiguration_url" class="form-control" type="text" name="url"  value='<c:out value='${not empty param.url ? param.url : webServiceClientConfiguration.url }'/>' />
</div>	
</div>	

<div class="form-group row">
	<div class="col-sm-2 control-label"><spring:message code="label.WebServiceClientConfiguration.sslActive"/></div>
	<div class="col-sm-4">
		<select id="webServiceClientConfiguration_sslActive" name="sslactive" class="form-control">
			<option value="false"><spring:message code="label.no"/></option>
			<option value="true"><spring:message code="label.yes"/></option>				
		</select>
		<script>
			$("#webServiceClientConfiguration_sslActive").val('<c:out value='${not empty param.sslactive ? param.sslactive : webServiceClientConfiguration.sslActive }'/>');
		</script>	
	</div>
</div>		
	
<div class="form-group row withWSSecurity withSSL">
<div class="col-sm-2 control-label"><spring:message code="label.WebServiceClientConfiguration.domainKeyStore"/></div> 

<div class="col-sm-4">
	<%-- Relation to side 1 drop down rendered in input --%>
		 <select id="webServiceClientConfiguration_domainKeyStore" class="js-example-basic-single" name="domainkeystore">
		 <option value=""></option> 
		</select>
				</div>
</div>		


<div class="form-group row withSSL">
<div class="col-sm-2 control-label"><spring:message code="label.WebServiceClientConfiguration.aliasForSSLCertificate"/></div> 

<div class="col-sm-10">
	<select id="webServiceClientConfiguration_aliasForSSLCertificate" name="aliasforsslcertificate" >
	</select>
</div>	
</div>
		
<div class="form-group row withWSSecurity">
<div class="col-sm-2 control-label"><spring:message code="label.WebServiceClientConfiguration.aliasForCerficate"/></div> 

<div class="col-sm-10">
	<select id="webServiceClientConfiguration_aliasForCerficate" name="aliasforcertificate" >
	</select>
</div>	
</div>		
  </div>
 <div class="form-group row withAuth">
<div class="col-sm-2 control-label"><spring:message code="label.WebServiceClientConfiguration.clientUsername"/></div> 

<div class="col-sm-10">
	<input id="webServiceClientConfiguration_clientUsername" class="form-control" type="text" name="clientusername"  value='<c:out value='${not empty param.clientusername ? param.clientusername : webServiceClientConfiguration.clientUsername }'/>' />
</div>	
</div>		
 <div class="form-group row withAuth">
<div class="col-sm-2 control-label"><spring:message code="label.WebServiceClientConfiguration.clientPassword"/></div> 

<div class="col-sm-10">
	<input id="webServiceClientConfiguration_clientPassword" class="form-control" type="password" name="clientpassword"  value='<c:out value='${not empty param.clientpassword ? param.clientpassword : webServiceClientConfiguration.clientPassword }'/>' />
</div>	
</div>  
  <div class="panel-footer">
		<input type="submit" class="btn btn-default" role="button" value="<spring:message code="label.submit" />"/>
	</div>
</div>
</form>

<script>
$(document).ready(function() {

	<%-- Block for providing domainKeyStore options --%>
	<%-- CHANGE_ME --%> <%-- INSERT YOUR FORMAT FOR element --%>
	domainKeyStore_options = [
		<c:forEach items="${WebServiceClientConfiguration_domainKeyStore_options}" var="element"> 
			{
				text : "<c:out value='${element.name}'/>", 
				id : "<c:out value='${element.externalId}'/>"
			},
		</c:forEach>
	];
	
	$("#webServiceClientConfiguration_domainKeyStore").select2(
		{
			data : domainKeyStore_options,
		}	  
		    );
		    
		    
		    $("#webServiceClientConfiguration_domainKeyStore").select2().select2('val', '<c:out value='${not empty param.domainkeystore ? param.domainkeystore : webServiceClientConfiguration.domainKeyStore.externalId }'/>');
		    <%-- End block for providing domainKeyStore options --%>
	
	
	});
	
	
	function requestCertificates() {
		var selectedID = $("#webServiceClientConfiguration_domainKeyStore").val();
		var selectedKeyAlias = "${not empty param.aliasforcertificate ? param.aliasforcertificate : webServiceClientConfiguration.aliasForCerficate }";
		var selectedSSLAlias = "${not empty param.aliasforsslcertificate ? param.aliasforsslcertificate : webServiceClientConfiguration.aliasForSSLCertificate }";
		$.getJSON("${pageContext.request.contextPath}/webservices/management/webservicesclients/webserviceclientconfiguration/update/${webServiceClientConfiguration.externalId}/entries/" + selectedID,
			function (data) {
				 $("#webServiceClientConfiguration_aliasForCerficate").empty();
				 $.each(data, function( key, val ) {
					 if (val == selectedKeyAlias) {
					 	$("#webServiceClientConfiguration_aliasForCerficate").append("<option value='" + val + "' selected>" + val + "</option>")
					 }else {
						$("#webServiceClientConfiguration_aliasForCerficate").append("<option value='" + val + "'>" + val + "</option>")
					 }
				});
				 
				 $("#webServiceClientConfiguration_aliasForSSLCertificate").empty();
				 $.each(data, function( key, val ) {
					 if (val == selectedSSLAlias) {
					 	$("#webServiceClientConfiguration_aliasForSSLCertificate").append("<option value='" + val + "' selected>" + val + "</option>")
					 }else {
						$("#webServiceClientConfiguration_aliasForSSLCertificate").append("<option value='" + val + "'>" + val + "</option>")
					 }
				});
				 
			}
		);
	}

	$(document).ready(function() {
		enableFields();
		requestCertificates();
		$("#webServiceClientConfiguration_domainKeyStore").change(function() {
			requestCertificates();
		});
		$("#webServiceClientConfiguration_authenticationLevel").change(function() {
			enableFields();	
		});
		$("#webServiceClientConfiguration_sslActive").change(function() {
			enableFields();	
		});
	});
	
	
	function enableFields() {
		var value = $("#webServiceClientConfiguration_authenticationLevel").val();
		var enabledSSL = $("#webServiceClientConfiguration_sslActive").val() == "true";
		
		$(".withSSL").hide();
		
		if (value == "NONE") {
			$(".withAuth").hide();
			$(".withWSSecurity").hide();
			$(".withAuth").attr('value','');
			$(".withWSSecurity").attr('value','');
		} else if (value == "WS_SECURITY") {
			$(".withAuth").show();
			$(".withWSSecurity").show();
		}
		else if (value == "BASIC_AUTH") {
			$(".withAuth").show();
			$(".withWSSecurity").hide();
			$(".withWSSecurity").attr('value','');
		}
		
		if (enabledSSL) {
			$(".withSSL").show();
		}
	}
	
</script>
