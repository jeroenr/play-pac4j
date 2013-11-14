package org.pac4j.play.scala

import org.pac4j.cas.logout.LogoutHandler
import org.pac4j.core.context.WebContext
import play.api.mvc.AnyContent
import org.apache.commons.lang3.StringUtils
import org.pac4j.play.{Config, StorageHelper}
import org.slf4j.LoggerFactory
import org.pac4j.cas.client.CasClient

/**
 * Created with IntelliJ IDEA.
 * User: jeroen
 * Date: 11/14/13
 * Time: 4:37 PM
 * To change this template use File | Settings | File Templates.
 */
/**
 * Created with IntelliJ IDEA.
 * User: jeroen
 * Date: 11/13/13
 * Time: 2:22 PM
 * To change this template use File | Settings | File Templates.
 */
object ScalaWebContextPlayLogoutHandler extends LogoutHandler {
  protected val logger = LoggerFactory.getLogger("models.LogoutHandler")

  protected val LOGOUT_REQUEST = "logoutRequest"


  override def isTokenRequest(context: WebContext): Boolean = {
    val scalaWebContext = context.asInstanceOf[ScalaWebContext[AnyContent]]
    scalaWebContext.getRequestParameter(CasClient.SERVICE_TICKET_PARAMETER) != null
  }

  override def isLogoutRequest(context: WebContext): Boolean = {
    val scalaWebContext = context.asInstanceOf[ScalaWebContext[AnyContent]]
    if ("POST" == scalaWebContext.getRequestMethod) {
      scalaWebContext.request.body.asFormUrlEncoded match {
        case Some(map) => map.get(LOGOUT_REQUEST).isDefined
        case _ => false
      }
    } else {
      false
    }
  }

  override def destroySession(context: WebContext) {
    val scalaWebContext = context.asInstanceOf[ScalaWebContext[AnyContent]]
    val logoutRequest = scalaWebContext.request.body.asFormUrlEncoded.get(LOGOUT_REQUEST).head
    logger.debug("logoutRequest : {}", logoutRequest)
    val ticket = StringUtils.substringBetween(logoutRequest, "SessionIndex>", "</")
    logger.debug("extract ticket : {}", ticket)
    val sessionId = StorageHelper.get(ticket).asInstanceOf[String]
    logger.debug("found sessionId : {}", sessionId)
    StorageHelper.removeProfile(sessionId)
    StorageHelper.remove(ticket)
  }

  override def recordSession(context: WebContext, ticket: String) {
    logger.debug("ticket : {}", ticket)
    val scalaWebContext = context.asInstanceOf[ScalaWebContext[AnyContent]]
    scalaWebContext.setSessionAttribute(ticket, Config.getProfileTimeout)
  }
}
