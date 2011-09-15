package frontlinesms2.contact

import frontlinesms2.*

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver
import grails.plugin.geb.GebSpec

class DeleteContactSpec extends ContactGebSpec {
	def 'delete button is displayed and works'() {
		given:
			createTestContacts()
		when:
			to AliceContactPage
			deleteSingleButton.click()
		then:
			waitFor { confirmDeleteButton.displayed }
		when:
			confirmDeleteButton.click()
		then:
			waitFor { flashMessage.displayed }
			!Contact.findAllByName('Alice')
	}
	
	
	def 'should delete multiple selected contacts'() {
		given:
			createTestContacts()
			assert Contact.count() == 2
		when:
			to AliceContactPage
			contactSelect[0].click()
			contactSelect[1].click()
		then:
			waitFor { deleteAllButton.displayed }
		when:
			deleteAllButton.click()
		then:
			waitFor { confirmDeleteButton.displayed }
		when:
			confirmDeleteButton.click()
		then:
			waitFor { flashMessage.displayed }
			Contact.count() == 0
	}
}

class AliceContactPage extends geb.Page {
	static getUrl() { "contact/show/${Contact.findByName('Alice').id}" } 
	static content = {
		contactSelect(required:false) { $(".contact-select") }
		deleteSingleButton(required:false) { $('#btn_delete') }
		deleteAllButton(required:false) { $('#btn_delete_all') }
		confirmDeleteButton(required:false) { $("#done") }
		flashMessage(required:false) { $('div.flash') }
	}
}