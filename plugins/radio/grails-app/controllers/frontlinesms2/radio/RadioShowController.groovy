package frontlinesms2.radio

import frontlinesms2.MessageController
import frontlinesms2.Poll
import java.util.Date
import grails.converters.*
import java.text.SimpleDateFormat

class RadioShowController extends MessageController {
	static allowedMethods = [save: "POST"]
//	static layout = 'radioShows'
	def index = {
		redirect action:radioShow
	}
	def create = {}

	def save = {	
		def showInstance = new RadioShow()
		showInstance.properties = params
		if (showInstance.validate()) {
			showInstance.save()
			flash.message = "Radio show created"
		}
		else {
			flash.message = "Name is not valid"
		}
		redirect(controller: 'message', action: "inbox")
	}
	
	def radioShow = {
		def showInstance = RadioShow.get(params.ownerId)
		def messageInstanceList = showInstance?.getShowMessages(params.starred)
		
		def radioMessageInstanceList = []
		messageInstanceList?.list(params).inject([]) { messageB, messageA ->
		    if(messageB && dateToString(messageB.dateReceived) != dateToString(messageA.dateReceived))
		        radioMessageInstanceList.add(dateToString(messageA.dateReceived))
		    radioMessageInstanceList.add(messageA)
		    return messageA
		}
		render view:'standard', model:[messageInstanceList: radioMessageInstanceList,
			   messageSection: 'radioShow',
			   messageInstanceTotal: messageInstanceList?.count(),
			   ownerInstance: showInstance] << this.getShowModel()
	}
	
	def startShow = {
		def showInstance = RadioShow.findById(params.id)
		println "params.id: ${params.id}"
		if(showInstance?.start()) {
			println "${showInstance.name} show started"
			showInstance.save(flush:true)
			render "$showInstance.id"
		} else {
			flash.message = "${RadioShow.findByIsRunning(true)?.name} show is already on air"
			render text:flash.message
		}
	}
	
	def stopShow = {
		def showInstance = RadioShow.findById(params.id)
		showInstance.stop()
		showInstance.save(flush:true)
		render "$showInstance.id"
	}
	
	def getShowModel(messageInstanceList) {
		def model = super.getShowModel(messageInstanceList)
		model << [radioShows: RadioShow.findAll()]
		return model
	}
	
	def getNewRadioMessageCount = {
		if(params.messageSection == 'radioShow') {
			def messageCount = [totalMessages:[RadioShow.get(params.ownerId)?.getShowMessages()?.count()]]
			render messageCount as JSON
		} else {
			getNewMessageCount()
		}
	}
	
	def addPoll = {
		def pollInstance = Poll.get(params.pollId)
		def showInstance = RadioShow.get(params.radioShowId)
		
		if(showInstance) {
			showInstance.addToPolls(pollInstance)
		}
		redirect controller:"message", action:"poll", params: [ownerId: params.pollId]
	}
	
	def selectPoll = {
		def pollInstance = Poll.get(params.ownerId)
		[ownerInstance:pollInstance]
	}
	
	private String dateToString(Date date) {
		new SimpleDateFormat("EEEE, MMMM dd", Locale.US).format(date)
	}
	
}