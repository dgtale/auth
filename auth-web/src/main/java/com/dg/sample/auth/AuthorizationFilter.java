package com.dg.sample.auth;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.apache.commons.collections4.CollectionUtils;

import com.dg.sample.annotation.AuthenticatedUser;
import com.dg.sample.annotation.Secured;
import com.dg.sample.entity.user.Account;
import com.dg.sample.entity.user.Role;
import com.dg.sample.i18.MessageCode;
import com.dg.sample.rest.ResponseMessage;
import com.dg.sample.rest.ResponseUtil;

@Secured
@Provider
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements ContainerRequestFilter {

	@Inject
	private ResponseUtil responseUtil;

	@Context
	private ResourceInfo resourceInfo;

	@Inject
	@AuthenticatedUser
	private Account authenticatedUser;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		// Get the resource class which matches with the requested URL
		// Extract the roles declared by it
		Class<?> resourceClass = resourceInfo.getResourceClass();
		List<Role> classRoles = extractRoles(resourceClass);

		// Get the resource method which matches with the requested URL
		// Extract the roles declared by it
		Method resourceMethod = resourceInfo.getResourceMethod();
		List<Role> methodRoles = extractRoles(resourceMethod);

		try {
			// Check if the user is allowed to execute the method
			// The method annotations override the class annotations
			if (methodRoles.isEmpty()) {
				checkPermissions(classRoles);
			} else {
				checkPermissions(methodRoles);
			}

		} catch (SecurityException e) {
			ResponseMessage responseMessage =
					responseUtil.createResponseMessage(MessageCode.SEC001, e.getMessage(), Locale.ENGLISH);
			requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity(responseMessage).build());
		} catch (Exception e) {
			e.printStackTrace();
			ResponseMessage responseMessage =
					responseUtil.createResponseMessage(MessageCode.SYS001, e.getMessage(), Locale.ENGLISH);
			requestContext.abortWith(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseMessage).build());
		}
	}

	// Extract the roles from the annotated element
	private List<Role> extractRoles(AnnotatedElement annotatedElement) {
		if (annotatedElement == null) {
			return new ArrayList<Role>();
		} else {
			Secured secured = annotatedElement.getAnnotation(Secured.class);
			if (secured == null) {
				return new ArrayList<Role>();
			} else {
				Role[] allowedRoles = secured.value();
				return Arrays.asList(allowedRoles);
			}
		}
	}

	private void checkPermissions(List<Role> allowedRoles) throws Exception {
		if (CollectionUtils.isNotEmpty(allowedRoles)) {
			if (!allowedRoles.contains(authenticatedUser.getRole())) {
				throw new SecurityException("User not authorized for this operation");
			}
		}
	}
}
