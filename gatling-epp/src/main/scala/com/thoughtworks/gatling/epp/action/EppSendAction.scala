/**
 * Copyright 2011-2012 eBusiness Information, Groupe Excilys (www.excilys.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.gatling.epp.action

import com.excilys.ebi.gatling.core.action.Action
import com.excilys.ebi.gatling.core.session.Session
import com.excilys.ebi.gatling.core.result.writer.DataWriter
import com.excilys.ebi.gatling.core.result.message.RequestStatus

import com.thoughtworks.gatling.epp.EppSendBuilder

import java.lang.System._
import java.lang.Thread._
import java.lang.String
import java.nio.channels.SocketChannel
import java.nio.ByteBuffer
import java.nio.ByteBuffer._
import java.net.InetSocketAddress

import akka.actor.ActorRef
import grizzled.slf4j.Logging

class EppSendAction(requestName: String, next: ActorRef, requestBuilder: EppSendBuilder) extends Action with Logging {
  def execute(session: Session) {
    
    val requestStartDate = currentTimeMillis()
    
    val channel = session.getTypedAttribute[SocketChannel]("channel")
   
    channel.write(ByteBuffer.wrap("hello".getBytes))
    var response = ByteBuffer.allocate(1024)
    channel.read(response)
    System.out.println(new String(response.array))
    channel.close
    
    val responseEndDate = currentTimeMillis()
    val endOfRequestSendingDate = currentTimeMillis()
    val endResponseSendingDate = endOfRequestSendingDate
    
    var requestResult = RequestStatus.OK
    
    val requestMessage = "foo"

    // This is an important line. This actually records the request and it's result. Without this call
    // you won't see any data. Customise the parameters to your heart's content.
    DataWriter.logRequest(session.scenarioName, session.userId, "Request " + requestName, requestStartDate, responseEndDate, endOfRequestSendingDate, endResponseSendingDate, requestResult, requestMessage)

    // This is also an important line. This passes the focus onto the next action in the chain.
    // Without this line your pipeline will just hang indefinitely.
    next ! session
  }
}