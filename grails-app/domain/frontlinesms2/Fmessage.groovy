package frontlinesms2

class Fmessage {
	String src
	String dst
	String text
	Date dateCreated
	Date dateRecieved
	boolean inbound
	boolean read
	static belongsTo = [activity:PollResponse]
//	PollResponse activity
	static mapping = {
		sort dateCreated:'desc'
		sort dateRecieved:'desc'
	}

	static constraints = {
		src(nullable:true)
		dst(nullable:true)
		text(nullable:true)
		activity(nullable:true)
		dateRecieved(nullable:true)
	}

	def getDisplayText() {
		def p = PollResponse.withCriteria {
			messages {
				eq('id', this.id)
			}
		}

		p?.size()?"${p[0].value} (\"${this.text}\")":this.text
	}

	static def getInboxMessages() {
		def messages = Fmessage.createCriteria().list {
			and {
				eq("inbound", true)
				isNull("activity")
			}
			order('dateRecieved', 'desc')
			order('dateCreated', 'desc')
		}
		messages
	}

	static def getSentMessages() {
		def messages = Fmessage.createCriteria().list {
			and {
				eq("inbound", false)
				isNull("activity")
			}
			order("dateCreated", "desc")
		}
		messages
	}
}
