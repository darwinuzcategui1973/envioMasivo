package com.desige.webDocuments.utils.beans;

import java.util.Collection;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;

/**
 * Title: SuperAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>23/03/2004 (NC) Creation</li>
 *          <li>29/05/2005 (NC) Se agregó el método getRb</li>
 *          <li>16/02/2006 (NC) se eliminaron los métodos synchronized</li>
 *          <li>30/06/2006 (NC) Cambios en el manejo de session de los Usuarios
 *          bug</li>
 *          </ul>
 */

public abstract class SuperAction extends Action {
	ActionMapping actionMapping;
	ActionForm actionForm;
	HttpServletRequest request;
	ServletContext servletContext;
	HttpServletResponse response;
	ResourceBundle rb;

	/**
	 * 
	 */
	public SuperAction() {
	}

	/**
	 * 
	 */
	public abstract ActionForward execute(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response);

	public SuperAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		this.actionMapping = mapping;
		this.actionForm = form;
		this.request = request;
		this.response = response;
		this.servletContext = this.getServlet().getServletContext();
	}

	/**
	 * 
	 */
	private void setRb() {
		this.rb = ToolsHTML.getBundle(request);
	}

	/**
	 * 
	 * @return
	 */
	public ResourceBundle getRb() {
		return this.rb;
	}

	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 */
	public void init(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		this.actionMapping = mapping;
		this.actionForm = form;
		this.request = request;
		this.response = response;
		this.servletContext = this.getServlet().getServletContext();
		checkUserInSession();
		setRb();
	}

	/**
	 * 
	 * @param request
	 */
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * 
	 * @param response
	 */
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	/**
	 * 
	 * @param servletContext
	 */
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	/**
	 * 
	 * @return
	 */
	public ServletContext getServletContext() {
		return this.servletContext;
	}

	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param isEsp
	 */
	public void init(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response,
			boolean isEsp) {
		this.actionMapping = mapping;
		this.actionForm = form;
		this.request = request;
		this.response = response;
		checkUserInSession(isEsp);
		setRb();
	}

	/**
	 * 
	 */
	public synchronized void checkUserInSession() {
		Users usuario = (Users) getSessionObject("user");
		if (usuario != null) {
			// usuario.setLastTime(ToolsHTML.sdfShowConvert.format(new
			// java.util.Date()));
			usuario.setLastTime(ToolsHTML.sdfShowConvert1
					.format(new java.util.Date()));
			usuario.setLastLogin(ToolsHTML.sdfShowConvert1
					.format(new java.util.Date()));
			if (!HandlerDBUser.updateUser(usuario)) {
				getSession().invalidate();
			} else {
				int sessionTimeout = -1;
				int sessionEditTimeout = -1;
				try {
					sessionTimeout = HandlerParameters.PARAMETROS.getEndSession();

					sessionEditTimeout = HandlerParameters.PARAMETROS.getEndSessionEdit();

					getSession().setMaxInactiveInterval((sessionTimeout+sessionEditTimeout) * 60);
				} catch (Exception e) {
				}
			}
		} else {
			String userKey = null;
			String userValue = null;
			if (request.getParameter("logout") != null
					&& request.getParameter("logout").equals("true")) {
				userKey = String.valueOf(request.getSession().getAttribute(
						"userKey"));
				userValue = String.valueOf(request.getSession().getAttribute(
						"userValue"));
			}
			getSession().invalidate();
			if (userKey != null && userValue != null) {
				request.getSession().setAttribute("userKey", userKey);
				request.getSession().setAttribute("userValue", userValue);
			}
		}
	}

	/**
	 * 
	 * @param isEsp
	 */
	public synchronized void checkUserInSession(boolean isEsp) {
		Users usuario = (Users) getSessionObject("user");
		if (usuario != null) {
			usuario.setEspecialSession(isEsp);
			usuario.setLastTime(ToolsHTML.sdfShowConvert
					.format(new java.util.Date()));
			usuario.setLastLogin(ToolsHTML.sdfShowConvert1
					.format(new java.util.Date()));
			if (!HandlerDBUser.updateUser(usuario)) {
				getSession().invalidate();
			} else {
				int sessionTimeout = -1;
				try {
					sessionTimeout = HandlerParameters.PARAMETROS.getEndSession();

					getSession().setMaxInactiveInterval(sessionTimeout * 60);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		} else {
			getSession().invalidate();
		}
	}

	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param rb
	 */
	public void init(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response,
			ResourceBundle rb) {
		this.actionMapping = mapping;
		this.actionForm = form;
		this.request = request;
		this.response = response;
		this.rb = rb;
		checkUserInSession();
	}

	/**
	 * 
	 * @return
	 */
	public ActionForward getError() {
		return actionMapping.findForward("error");
	}

	/**
	 * 
	 * @param request
	 * @param inSession
	 * @return
	 */
	protected String getCmd(HttpServletRequest request, boolean inSession) {
		String cmd = "";
		if (inSession) {
			cmd = (String) request.getSession().getAttribute("cmd");
		} else {
			cmd = (String) request.getAttribute("cmd");
		}
		if (ToolsHTML.checkValue(cmd)) {
			return cmd;
		} else {
			cmd = request.getParameter("cmd");
			if (ToolsHTML.checkValue(cmd)) {
				return cmd;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param nameForm
	 * @param field
	 * @param isId
	 * @return
	 */
	protected String getDataFormResponse(String nameForm, String field,
			boolean isId) {
		// System.out.println("nameForm = " + nameForm);
		// System.out.println("field = " + field);
		if ((!ToolsHTML.isEmptyOrNull(nameForm))
				&& (!ToolsHTML.isEmptyOrNull(field))) {
			StringBuffer data = new StringBuffer("opener.document.")
					.append(nameForm);
			data.append(".").append(field);
			if (isId) {
				data.append(".value=id;");
			} else {
				data.append(".value=desc;");
			}
			return data.toString();
		}
		return "";
	}

	/**
	 * 
	 * @param obj
	 * @return
	 */
	private boolean isSetValue(Object obj) {
		if (obj instanceof String) {
			if (((String) obj).length() > 0) {
				return true;
			}
		}
		if (obj instanceof Collection) {
			if (!((Collection) obj).isEmpty()) {
				return true;
			}
		} else {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param request
	 * @param att
	 * @param valor
	 */
	protected void putAtributte(HttpServletRequest request, String att,
			Object valor) {
		// System.out.println("[putAtributte] att: " + att + "_ Valor: " +
		// valor);
		if ((valor != null) && (att != null)) {
			if (isSetValue(valor)) {
				// System.out.println("Attributte Name Set: " + att + "_" +
				// valor);
				request.setAttribute(att, valor);
			}
		}
	}

	/**
	 * 
	 * @param att
	 * @param valor
	 */
	protected void putAtributte(String att, Object valor) {
		if ((valor != null) && (att != null)) {
			if (isSetValue(valor)) {
				request.setAttribute(att, valor);
			} else {
				if (request.getAttribute(att) != null) {
					request.removeAttribute(att);
				}
			}
		}
	}

	/**
	 * 
	 * @param request
	 */
	protected void updateParemeters(HttpServletRequest request) {

		// StringTokenizer st = new StringTokenizer(
		// DesigeConf.getProperty("cat.parameters"), ",");
		// while (st.hasMoreTokens()) {
		for (StringTokenizer st = new StringTokenizer(
				DesigeConf.getProperty("cat.parameters"), ","); st
				.hasMoreTokens();) {
			String item = st.nextToken();
			if(!item.equals("moduloActivo")) {
				String valueParam = (String) ToolsHTML.getAttribute(request, item);
				if (ToolsHTML.checkValue(valueParam)) {
					request.setAttribute(item, valueParam);
				}
			}
		}
	}

	/**
	 * 
	 * @param nameAttribute
	 * @param request
	 */
	protected void removeAttribute(String nameAttribute,
			HttpServletRequest request) {
		if (request != null && nameAttribute != null) {
			if (request.getAttribute(nameAttribute) != null) {
				request.removeAttribute(nameAttribute);
			}

			if (request.getSession() != null
					&& request.getSession().getAttribute(nameAttribute) != null) {
				request.getSession().removeAttribute(nameAttribute);
			}
		}
	}

	/**
	 * 
	 * @param nameAttribute
	 */
	protected void removeAttribute(String nameAttribute) {
		removeAttribute(nameAttribute, request);
	}

	/**
	 * 
	 * @param attributeName
	 * @param obj
	 */
	public void putObjectSession(String attributeName, Object obj) {
		if (obj != null && attributeName != null
				&& !attributeName.trim().equals("") && getSession() != null) {
			if (isSetValue(obj)) {
				getSession().setAttribute(attributeName, obj);
			} else {
				removeObjectSession(attributeName);
			} 
		} else {
			removeObjectSession(attributeName);
		}
	}

	/**
	 * 
	 * @param attributeName
	 * @return
	 */
	public Object removeObjectSession(String attributeName) {
		HttpSession session = getSession();

		Object obj = null;
		if (session != null) {
			obj = session.getAttribute(attributeName);
		}

		if (obj != null) {
			getSession().removeAttribute(attributeName);
		}
		return obj;
	}

	/**
	 * 
	 * @return
	 */
	public HttpSession getSession() {
		return request.getSession();
	}

	/**
	 * 
	 * @return
	 */
	public ActionForward goSucces() {
		if (actionMapping == null) {
			return null;
		}
		return actionMapping.findForward("success");
	}

	/**
	 * 
	 * @param to
	 * @return
	 */
	public ActionForward goTo(String to) {
		if (!ToolsHTML.isEmptyOrNull(to)) {
			return actionMapping.findForward(to.trim());
		}
		return actionMapping.findForward("success");
	}

	/**
	 * 
	 * @param to
	 * @param parameters
	 * @return
	 */
	public ActionForward goTo(String to, String parameters) {
		if (!ToolsHTML.isEmptyOrNull(to)) {
			if (ToolsHTML.isEmptyOrNull(parameters)) {
				return actionMapping.findForward(to);
			} else {
				ActionForward forward = actionMapping.findForward(to);
				forward.setPath(forward.getPath() + "?" + parameters);
			}
		}
		return actionMapping.findForward("success");
	}

	/**
	 * 
	 * @param keyInfo
	 * @return
	 */
	public ActionForward goSucces(String keyInfo) {
		putObjectSession("info", this.rb.getString(keyInfo));
		return goTo("success");
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public String getMessage(String key) {
		return this.rb.getString(key);
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	protected ActionForward goError(String key) {
		if (key != null) {
			putObjectSession("error", this.rb.getString(key));
		}
		if ("E0035".equalsIgnoreCase(key)) {
			ActionForward resp = new ActionForward(
					"/pageRedirect.jsp?target=_parent", false);
			return resp;
		}
		return goTo("error");
	}

	/**
	 * 
	 * @param key
	 * @param AditionalInfo
	 * @return
	 */
	protected ActionForward goErrorII(String key, String AditionalInfo) {
		if (key != null) {
			putObjectSession("error", this.rb.getString(key) + AditionalInfo);
		}
		if ("E0035".equalsIgnoreCase(key)) {
			ActionForward resp = new ActionForward(
					"/pageRedirect.jsp?target=_parent", false);
			return resp;
		}
		return goTo("error");
	}

	/**
	 * 
	 * @param key
	 * @param parameters
	 * @return
	 */
	protected ActionForward goError(String key, String parameters) {
		putObjectSession("error", this.rb.getString(key));
		if ("E0035".equalsIgnoreCase(key)) {
			ActionForward resp = new ActionForward(
					"/pageRedirect.jsp?target=_parent", false);
			return resp;
		}
		return goTo("error", parameters);
	}

	/**
	 * 
	 * @return
	 */
	public ActionForward goError() {
		putObjectSession("error", this.rb.getString("E0008"));
		return goTo("error");
	}

	/**
	 * 
	 * @param att
	 * @return
	 */
	public Object getSessionObject(String att) {
		return getSession().getAttribute(att);
	}

	/**
	 * 
	 * @return
	 * @throws ApplicationExceptionChecked
	 */
	public synchronized Users getUserSession()
			throws ApplicationExceptionChecked {
		Users usuario = (Users) getSessionObject("user");

		if (usuario == null
				|| !HandlerDBUser.isValidSessionUser(usuario.getUser(),
						request.getSession())) {
			System.out.println("Se debe arrojar la exception E0035");
			throw new ApplicationExceptionChecked("E0035");
		}

		return usuario;
	}

	/**
	 * 
	 */
	protected void removeDataFieldEdit() {
		removeObjectSession("input");
		removeObjectSession("value");
		removeObjectSession("descript");
		removeObjectSession("dataTable");
		removeObjectSession("sizeParam");
		removeObjectSession("editManager");
		removeObjectSession("newManager");
		removeObjectSession("loadManager");
		removeObjectSession("loadEditManager");
		removeObjectSession("formEdit");
		removeObjectSession("typeOtroFormato");
	}

	/**
	 * 
	 * @param parameterName
	 * @return
	 */
	protected String getParameter(String parameterName) {
		String paramValue = request.getParameter(parameterName);
		if (ToolsHTML.isEmptyOrNull(paramValue)) {
			try {
				paramValue = (String) getSession().getAttribute(parameterName);
			} catch (Exception e) {
				paramValue = null;
			}
		}
		return paramValue;
	}

	/**
	 * 
	 * @param parameterName
	 * @param firstAttribute
	 * @return
	 */
	protected String getParameter(String parameterName, boolean firstAttribute) {
		String paramValue = null;
		if (firstAttribute) {
			paramValue = (String) request.getAttribute(parameterName);
		}
		if (ToolsHTML.isEmptyOrNull(paramValue)) {
			paramValue = request.getParameter(parameterName);
		}
		if (ToolsHTML.isEmptyOrNull(paramValue)) {
			paramValue = (String) getSession().getAttribute(parameterName);
		}
		return paramValue;
	}

}
