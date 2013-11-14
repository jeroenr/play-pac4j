package org.pac4j.play.scala

import play.api.mvc.{Session, Request}
import org.pac4j.core.context.WebContext
import org.pac4j.play.{Constants, StorageHelper}
import _root_.scala.collection.JavaConverters._

/**
 * Created with IntelliJ IDEA.
 * User: jeroen
 * Date: 11/14/13
 * Time: 4:35 PM
 * To change this template use File | Settings | File Templates.
 */
case class ScalaWebContext[C](request: Request[C], session: Session) extends WebContext{

  def getSessionAttribute(key: String): AnyRef = StorageHelper.get(session.get(Constants.SESSION_ID).getOrElse(null), key)

  def setSessionAttribute(key: String, value: Any) {
    val maybeSessionId = session.get(Constants.SESSION_ID)
    if(maybeSessionId.isDefined) {
      StorageHelper.save(maybeSessionId.get, key, value)
    }
  }

  def getRequestParameter(name: String): String = request.getQueryString(name).getOrElse(null)

  def getRequestParameters = request.queryString.mapValues(_.toArray[String]).asJava

  def getRequestHeader(name: String): String = request.headers.get(name).getOrElse(null)

  def getRequestMethod: String = request.method

  def setResponseHeader(key: String, value: String) {
    throw new UnsupportedOperationException
  }

  def setResponseStatus(code: Int) {
    throw new UnsupportedOperationException
  }

  def writeResponseContent(content: String) {
    throw new UnsupportedOperationException
  }
}
